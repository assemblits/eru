package org.assemblits.eru.dolphin;

import groovyx.gpars.agent.Agent;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.server.adapter.DolphinServlet;

/**
 * Created by mtrujillo on 8/30/2015.
 */
public class Server extends DolphinServlet {

    public static Agent<Boolean> databaseLoadedAgent   = new Agent<>(false);
    public static Agent<Boolean> pause                 = new Agent<>(false);
    public static Agent<Boolean> communicationsBlocked = new Agent<>(false);
    public static Agent<Integer> threadsCount          = new Agent<>(0);

    @Override
    protected void registerApplicationActions(DefaultServerDolphin serverDolphin) {
        try {
            threadsCount.updateValue(threadsCount.getVal() + 1);
            final String THREAD_ID = "PSV[" + threadsCount.getVal() + "]";
            serverDolphin.register(new ServerActions(THREAD_ID));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
