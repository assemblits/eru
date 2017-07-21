package com.marlontrujillo.eru.comm;

import com.marlontrujillo.eru.alarming.Alarming;
import com.marlontrujillo.eru.historian.Historian;
import com.marlontrujillo.eru.comm.connection.Connection;
import com.marlontrujillo.eru.comm.member.Communicator;
import com.marlontrujillo.eru.comm.member.Director;
import com.marlontrujillo.eru.logger.LogUtil;
import com.marlontrujillo.eru.persistence.Container;
import com.marlontrujillo.eru.util.EngineScriptUtil;
import com.marlontrujillo.eru.util.TagUtil;

/**
 * Created by mtrujillo on 22/05/17.
 */
public class FieldBusCommunicator {
    private static final FieldBusCommunicator instance = new FieldBusCommunicator();
    public static FieldBusCommunicator getInstance(){
        return instance;
    }

    private Director director;
    private boolean  started;

    private FieldBusCommunicator() {
        this.director = new Director();
    }

    public void start() throws Exception {
        director.removeAllCommunicators();

        {//TODO: find a better place to do this:
            EngineScriptUtil.getInstance().loadTags(Container.getInstance().getTagsAgent().getInstantVal());
            TagUtil.linkAll(Container.getInstance().getTagsAgent().getInstantVal());
        }

        Container.getInstance().getConnectionsAgent().getVal().forEach(Connection::connect);

        Thread directorThread = new Thread(director, "FIELD_BUS_DIRECTOR_THREAD_NRO_" + System.currentTimeMillis());
        directorThread.setDaemon(true);
        directorThread.start();

        Historian.getInstance().start();
        Alarming.getInstance().start();

        started = true;
    }

    public void stop() throws InterruptedException {
        director.stop();
        {//TODO: find a better place to do this:
            TagUtil.unlinkAll();
        }
        Container.getInstance().getConnectionsAgent().getVal().forEach(Connection::discconnect);

        Historian.getInstance().stop();
        Alarming.getInstance().stop();

        started = false;
    }

    public void subscribe(Communicator communicator) {
        try {
            director.addCommunicator(communicator);
        } catch (InterruptedException e) {
            LogUtil.logger.error(e);
        }
    }
    public boolean isStarted() {
        return started;
    }
    public void setStarted(boolean started) {
        this.started = started;
    }
}
