package com.eru.historian;

import com.eru.entities.Tag;
import com.eru.gui.ApplicationContextHolder;
import com.eru.preferences.EruPreferences;
import com.eru.util.Constants;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by mtrujillo on 14/07/2014.
 * TODO
 */
@Slf4j
public class Historian {

    public static final SimpleDateFormat TIME_STAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    /* ********** Static Fields ********** */
    private static final Historian instance = new Historian();
    /* ********** Fields ********** */
    private final int limit;
    private final int samplingTime;
    private final ReadOnlyListWrapper<Tag> historicalTagList;
    private boolean running;
    private Executor historianExecutor;
    private Thread currentHistorianThread;
    /* ********** Constructor ********** */
    private Historian() {
        EruPreferences eruPreferences = ApplicationContextHolder.getApplicationContext().getBean(EruPreferences.class);
        limit = eruPreferences.getHistorianLimit();
        samplingTime = eruPreferences.getHistorianSamplingTime();
        historicalTagList = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
        historianExecutor = Executors.newSingleThreadExecutor(r -> {
            currentHistorianThread = new Thread(r);
            currentHistorianThread.setDaemon(true);
            return currentHistorianThread;
        });
    }

    public static Historian getInstance() {
        return instance;
    }

    /* ********** Methods ********** */
    public void start() {
        if (!running) {
            historianExecutor.execute(new HistorianRunnable(this.limit, this.samplingTime, getEnableTagsFromContainer()));
            running = true;
        }
    }

    public void stop() {
        if (running) {
            currentHistorianThread.interrupt();
        }
    }

    /* ********** Private Methods ********** */
    private Tag[] getEnableTagsFromContainer() {
//        historicalTagList.clear();
//        try {
//            historicalTagList.addAll(Container.getInstance().getTagsAgent().getVal().stream()
//                    .filter(Tag::getHistoricalEnabled)
//                    .collect(Collectors.toList()));
//        } catch (InterruptedException e) {
//            log.error("Cannot load tags for historical daemon.", e);
//        }
        return historicalTagList.toArray(new Tag[historicalTagList.size()]);
    }

    /* ********** Nested Classes ********** */
    private class HistorianRunnable implements Runnable {
        private final int serviceLimit;
        private final int serviceSamplingTime;
        private final StringBuffer columnNames;
        private final StringBuffer columnValues;
        /* ********** Fields ********** */
        private Tag[] historicalTagList;
        private EntityManager em;
        private HistoricDao historicDao;
        private volatile boolean running = false;

        /* ********** Constructor ********** */
        private HistorianRunnable(int serviceLimit, int serviceSamplingTime, Tag[] historicalTagList) {
            this.serviceLimit = serviceLimit;
            this.serviceSamplingTime = serviceSamplingTime;
            this.columnNames = new StringBuffer();
            this.columnValues = new StringBuffer();
            this.historicalTagList = historicalTagList;
        }

        /* ********** Method ********** */
        @Override
        public void run() {
            Thread.currentThread().setName("Historian Thread");

            log.info("Historian: Linking...");
            em = ApplicationContextHolder.getApplicationContext().getBean(EntityManager.class);
            historicDao = new HistoricDao(ApplicationContextHolder.getApplicationContext().getBean(JdbcTemplate.class));
            running = true;

            if (historicalTagList.length == 0) {
                log.error("Historian: There is no tags to record. List size: {}", historicalTagList.length);
                running = false;
            }

            log.info("Historian: Recording tags history of: {} tags. Daemon:", historicalTagList.length);

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
                    log.error("Historian: SQL error: ", e);
                }
            }
            log.info("Historian was stopped.");
        }

    }
}
