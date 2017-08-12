package com.eru.historian;

import com.eru.logger.LogUtil;
import com.eru.persistence.Container;
import com.eru.tag.Tag;
import com.eru.util.Constants;
import com.eru.util.Preferences;
import com.eru.util.JpaUtil;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import javax.persistence.EntityManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by mtrujillo on 14/07/2014.
 */
public class Historian {

    /* ********** Static Fields ********** */
    private static final Historian                  instance   = new Historian();
    private boolean running;

    public static Historian getInstance(){
        return instance;
    }

    /* ********** Fields ********** */
    private final int                      limit;
    private final int                      samplingTime;
    private final ReadOnlyListWrapper<Tag> historicalTagList;
    public static final SimpleDateFormat   TIME_STAMP_FORMAT   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private Executor                       historianExecutor;
    private Thread                         currentHistorianThread;

    /* ********** Constructor ********** */
    private Historian() {
        limit               = Preferences.getInstance().getHistorianCountLimit();
        samplingTime        = Preferences.getInstance().getHistorianSamplingTimeMillis();
        historicalTagList   = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
        historianExecutor  = Executors.newSingleThreadExecutor(r -> {
            currentHistorianThread = new Thread(r);
            currentHistorianThread.setDaemon(true);
            return currentHistorianThread;
        });
    }

    /* ********** Methods ********** */
    public void start(){
        if(!running){
            historianExecutor.execute(new HistorianRunnable(this.limit, this.samplingTime, getEnableTagsFromContainer()));
            running = true;
        }
    }
    public void stop(){
        if(running) {
            currentHistorianThread.interrupt();
        }
    }

    /* ********** Private Methods ********** */
    private Tag[] getEnableTagsFromContainer(){
        historicalTagList.clear();
        try {
            historicalTagList.addAll(Container.getInstance().getTagsAgent().getVal().stream()
                    .filter(Tag::getHistoricalEnabled)
                    .collect(Collectors.toList()));
        } catch (InterruptedException e) {
            LogUtil.logger.error("Cannot load tags for historical daemon.", e);
        }
        return historicalTagList.toArray(new Tag[historicalTagList.size()]);
    }

    /* ********** Nested Classes ********** */
    private class HistorianRunnable implements Runnable {
        /* ********** Fields ********** */
        private          Tag[]            historicalTagList;
        private          EntityManager    em;
        private          HistoricDao      historicDao;
        private final    int              serviceLimit;
        private final    int              serviceSamplingTime;
        private final    StringBuffer     columnNames;
        private final    StringBuffer     columnValues;
        private volatile boolean          running = false;

        /* ********** Constructor ********** */
        private HistorianRunnable(int serviceLimit, int serviceSamplingTime, Tag[] historicalTagList) {
            this.serviceLimit           = serviceLimit;
            this.serviceSamplingTime    = serviceSamplingTime;
            this.columnNames            = new StringBuffer();
            this.columnValues           = new StringBuffer();
            this.historicalTagList      = historicalTagList;
        }

        /* ********** Method ********** */
        @Override
        public void run() {
            Thread.currentThread().setName("Historian Thread");

            LogUtil.logger.info("Historian: Linking...");
            em          = JpaUtil.getEntityManagerFactory().createEntityManager();
            historicDao = new HistoricDao(em);
            running     = true;

            if(historicalTagList.length == 0){
                LogUtil.logger.error("Historian: There is no tags to record. List size: " + historicalTagList.length);
                running = false;
            }

            LogUtil.logger.info("Historian: Recording tags history of: " + historicalTagList.length + " tags. Daemon:");

            while (running) {
                // Clear buffers
                columnNames.setLength(0);
                columnValues.setLength(0);

                // Extract values from JavaFX Thread Objects *** ONLY READ OBJECTS ***
                for (Tag tag : historicalTagList) {
                    columnNames.append(tag.getName()).append(", ");
                    columnValues.append(tag.getValue()).append(", ");
                }

                // Append timestamp
                columnNames.append("time_stamp");
                columnValues.append("'").append(TIME_STAMP_FORMAT.format(new Date())).append("'");

                try {
                    // Create the query
                    historicDao.executeUpdate("INSERT INTO " + Constants.HISTORIC_TABLE_NAME + " (" + columnNames + ") VALUES (" + columnValues + ")");
                    historicDao.maintainRowLimit(Constants.HISTORIC_TABLE_NAME, serviceLimit);
                    // Sleep
                    Thread.sleep(serviceSamplingTime);
                } catch (InterruptedException e) {
                    running = false;
                } catch (SQLException e) {
                    LogUtil.logger.error("Historian: SQL error: ", e);
                }
            }
            LogUtil.logger.info("Historian was stopped.");
        }

    }
}
