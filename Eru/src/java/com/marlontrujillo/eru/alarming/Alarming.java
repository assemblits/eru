package com.marlontrujillo.eru.alarming;

import com.marlontrujillo.eru.logger.LogUtil;
import com.marlontrujillo.eru.tag.Tag;
import com.marlontrujillo.eru.persistence.Container;
import com.marlontrujillo.eru.persistence.Dao;
import com.marlontrujillo.eru.util.JpaUtil;
import com.marlontrujillo.eru.util.Preferences;
import groovy.lang.Closure;
import groovyx.gpars.agent.Agent;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mtrujillo on 15/10/2014.
 */

public class Alarming {
    /* ********** Static Fields ********** */
    private static final Alarming ourInstance = new Alarming();
    public  static Alarming getInstance() {
        return ourInstance;
    }

    /* ********** Fields ********** */
    private EntityManager                               entityManager;
    private Dao<Alarm>                                  alarmDao;
    private Agent<ObservableList<Alarm>>                alarmsAgent;
    private boolean                                     running;
    private StringProperty                              status;
    private BooleanProperty                             alarmed;
    private ExecutorService                             executorService;
    private Map<Tag, AlarmListenerToCreateNewAlarms> tagsRegisteredListeners;

    /* ********** Constructor ********** */
    private Alarming() {
        entityManager   = JpaUtil.getEntityManagerFactory().createEntityManager();
        running         = false;
        alarmDao        = new Dao<>(entityManager, Alarm.class);
        alarmsAgent     = new Agent<>(FXCollections.observableArrayList());
        status          = new SimpleStringProperty();
        alarmed         = new SimpleBooleanProperty();
        executorService  = Executors.newSingleThreadExecutor(r -> {
            Thread newThread = new Thread(r);
            newThread.setDaemon(true);
            return newThread;
        });
        tagsRegisteredListeners = new HashMap<>();
    }

    /* ********** Methods ********** */
    public synchronized boolean start() {
        if(running) {
            LogUtil.logger.error("Alarming: detected a load command, but the communications are already started.");
        } else {
            try {
                // Check if there is a database connection
                if (entityManager == null){
                    entityManager   = JpaUtil.getEntityManagerFactory().createEntityManager();
                }

                // Clear persistent context and memory
                entityManager.clear();
                alarmsAgent.getVal().clear();

                // Setting limits for alarms in memory
                final int alarmTableCount = alarmDao.getCount();
                final int alarmsToShowLimit = Preferences.getInstance().getAlarmsInMemoryLimit();
                final int firstResult = alarmTableCount - alarmsToShowLimit < 0 ? 0 : alarmTableCount - alarmsToShowLimit;

                // Loading alarms from database
                LogUtil.logger.info("Loading " + alarmsToShowLimit  + " of " + alarmTableCount + "  alarms from database.");
                final ObservableList<Alarm> databaseAlarms = FXCollections.observableArrayList(alarmDao.findEntities("timeStamp", Dao.Order.ASC, alarmsToShowLimit, firstResult));
                alarmsAgent.updateValue(databaseAlarms);
                updateStatus();

                running = true;

                installAlarmListenerOnTags(Container.getInstance().getTagsAgent().getInstantVal());
                LogUtil.logger.info("Alarming: alarms loaded.");
            }catch (Exception e){
                final String errorMSG = "Alarming module cannot load.";
                status.setValue(errorMSG);
                alarmed.setValue(true);
                LogUtil.logger.error(errorMSG, e);
            }
        }
        return running;
    }

    public synchronized void stop(){
        removeAlarmListenerOnRegisteredTags();
        running = false;
    }

    public void load(Alarm newAlarm) {
        if(newAlarm == null) return;

        final int alarmsInDatabaseLimit = Preferences.getInstance().getAlarmsDatabaseLimit();
        final int alarmsInMemoryLimit   = Preferences.getInstance().getAlarmsInMemoryLimit();

        executorService.execute(()->{
                    // In Database
                    alarmDao.create(newAlarm);
                    while(alarmDao.getCount() > alarmsInDatabaseLimit){
                        alarmDao.delete(alarmDao.findEntities("timeStamp", Dao.Order.ASC, 1, 0).get(0));
                    }

                    // In memory
                    alarmsAgent.send(new Closure(this) {
                        void doCall(ObservableList<Alarm> alarms){
                            alarms.add(newAlarm);
                            while (alarms.size() > alarmsInMemoryLimit) {
                                alarms.remove(0);
                            }
                        }
                    });

                    // Update status
                    updateStatus();
                }
        );
    }

    public void installAlarmListenerOnTags(List<Tag> tagsToListen){
        LogUtil.logger.info("Alarming: Installing listeners on tags:");

        if(tagsRegisteredListeners.size()>0){
            removeAlarmListenerOnRegisteredTags();
        }

        // Check if Alarming module is running, has to be running cause the listen create new alarms on database.
        if(running){
            tagsToListen.stream()
                    .filter(Tag::getAlarmEnabled)
                    .forEach(alarmEnabledTag -> {
                        AlarmListenerToCreateNewAlarms listener = new AlarmListenerToCreateNewAlarms(alarmEnabledTag);
                        alarmEnabledTag.alarmedProperty().addListener(listener);
                        tagsRegisteredListeners.put(alarmEnabledTag, listener);
                    });
            LogUtil.logger.info("Alarming: listeners on tags installed.");
        } else {
            LogUtil.logger.error("Alarming: Tags cannot be listen because there are no db connection in Alarming module.");
        }
    }

    public void removeAlarmListenerOnRegisteredTags() {
        LogUtil.logger.info("Alarming: Removing listeners on tags...");
        for(Tag tag : tagsRegisteredListeners.keySet()){
            tag.alarmedProperty().removeListener(tagsRegisteredListeners.get(tag));
        }
        tagsRegisteredListeners.clear();
        LogUtil.logger.info("Alarming: listeners on tags removed.");
    }

    public void setAcknowledgeAllAlarms(String userInCharge){
        LogUtil.logger.info("Acknowledging all alarms...");
        alarmsAgent.send(new Closure(this) {
            void doCall(ObservableList<Alarm> alarms){
                alarms.forEach(alarm -> {
                    if(!alarm.getAcknowledged()){
                        alarm.setAcknowledged(true);
                        alarm.setUserInCharge(userInCharge);
                        alarmDao.update(alarm);
                    }
                });
            }
        });
    }

    // TODO:
//    public void setAcknowledge(String alarmTimeStamp,String userInCharge){
//        if (alarmTimeStamp == null) return;
//
//        dtoAlarmsAgent.send(new Closure(this){
//            void doCall(List<DTO> dtos){
//                dtos.stream()
//                        .filter(dto -> dto.getSlots().get(0).toString().equals(alarmTimeStamp))
//                        .forEach(selectedDTO -> {
//
//                            if (!al.getAcknowledged()) {
//                                al.setAcknowledged(true);
//                                al.setUserInCharge(userInCharge);
//                                alarmDaoList.updateOnDB(al);
//                                updateStatus();
//                            }
//                        });
//            }
//        });
//    }

    /* ********** Private Methods ********** */
    private void updateStatus(){
        // Clean Status
        alarmed.set(false);
        status.set("");

        // Look new Alarm not acknowledge
        alarmsAgent.send(new Closure(this) {
            void doCall(ObservableList<Alarm> alarms) {
                for (Alarm a : alarms) {
                    if (!a.getAcknowledged()) {
                        status.set("LAST ALARM: " + a.getDescription());
                        alarmed.set(true);
                    }
                }
            }
        });
    }

    /* ********** Setters and Getters ********** */
    public String getStatus() {
        return status.get();
    }
    public StringProperty statusProperty() {
        return status;
    }

    public boolean getAlarmed() {
        return alarmed.get();
    }
    public BooleanProperty alarmedProperty() {
        return alarmed;
    }

    public Agent<ObservableList<Alarm>> getAlarmsAgent() {
        return alarmsAgent;
    }

    public boolean isRunning() {
        return running;
    }
}

class AlarmListenerToCreateNewAlarms implements InvalidationListener {
    private final Tag tagToListen;

    public AlarmListenerToCreateNewAlarms(Tag tagToListen) {
        this.tagToListen = tagToListen;
    }

    @Override
    public void invalidated(Observable observable) {
        if (tagToListen.getAlarmed()) {
            Alarming.getInstance().load(
                    new Alarm(Timestamp.from(Instant.now()),
                            tagToListen.getDescription().concat(" alarm = ACTIVATED"),
                            "",
                            tagToListen.getAlarmGroupName(),
                            Alarm.Priority.HIHI));
        } else {
            Alarming.getInstance().load(
                    new Alarm(Timestamp.from(Instant.now()),
                            tagToListen.getDescription().concat(" alarm = DEACTIVATED"),
                            "",
                            tagToListen.getAlarmGroupName(),
                            Alarm.Priority.NOMINAL));
        }
    }
}