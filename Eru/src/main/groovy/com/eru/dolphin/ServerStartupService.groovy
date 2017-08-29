package com.eru.dolphin


import javafx.concurrent.Service
import javafx.concurrent.Task
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder

/**
 * Created by mtrujillo on 2/13/2016.
 */
class ServerStartupService extends Service<Void> {

    private Server server;

    /* ********** Static Fields ********** */
    private static final ServerStartupService instance = new ServerStartupService();
    static ServerStartupService getInstance(){
        return instance;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateProgress(20, 100);

                LogUtil.logger.info("Loading database..")
                updateMessage("Loading database..");
                Container.getInstance().loadDatabase();

                updateProgress(70, 100);

                LogUtil.logger.info("Preparing server for remote connections")
                updateMessage("Preparing server for remote connections");
                server = new Server(1080);
                ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
                context.setContextPath("/PowerSceneViewer");
                server.setHandler(context);
                context.addServlet(new ServletHolder(new com.eru.dolphin.Server()), "/")
                server.start()

                updateProgress(100, 100);
            }
        }
    }

    public void stop(){
        Runnable runnable = new Runnable() {
            @Override
            void run() {
                try {
//                    log.info("Shutting down the server...");
                    server.stop();
//                    log.info("Server has stopped.");
                } catch (Exception ex) {
//                    log.error("Error when stopping Jetty server: "+ex.getMessage(), ex);
                }
            }
        }

        Thread threadToStopServlet = new Thread(runnable);
        threadToStopServlet.setName("Thread to Stop Servlet.")
        threadToStopServlet.setDaemon(true);
        threadToStopServlet.start()
    }

    public boolean serverIsRunning(){
        return server.isRunning();
    }
}


