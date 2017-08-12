package com.eru.dolphin

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Service
import javafx.concurrent.Task
import org.opendolphin.LogConfig
import org.opendolphin.core.ModelStoreEvent
import org.opendolphin.core.ModelStoreListener
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.comm.HttpClientConnector
import org.opendolphin.core.client.comm.JavaFXUiThreadHandler
import org.opendolphin.core.comm.JsonCodec

import static com.eru.util.Commands.*
import static com.eru.util.DolphinConstants.*

/**
 * Created by mtrujillo on 4/7/2015.
 */
class ClientStartupService extends Service<Void>{

    private ClientDolphin                     clientDolphin;
    private ObservableList<PresentationModel> alarmsPMObservableList = FXCollections.observableArrayList();
    private String URLToConnect;

    private static final ClientStartupService instance = new ClientStartupService();
    static ClientStartupService getInstance(){
        return instance;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateProgress(10, 100);

                // Create ClientDolphin and connection
                updateMessage("Connecting...");
                LogConfig.noLogs();
                clientDolphin = new ClientDolphin();
                clientDolphin.setClientModelStore(new ClientModelStore(clientDolphin));
//                HttpClientConnector connector = new HttpClientConnector(clientDolphin, "http://localhost:1080/PowerSceneViewer/");
                HttpClientConnector connector = new HttpClientConnector(clientDolphin, "http://" + URLToConnect + ":1080/PowerSceneViewer/");
                connector.setCodec(new JsonCodec());
                connector.setUiThreadHandler(new JavaFXUiThreadHandler());
                connector.setOnException {
                    updateMessage("Error connecting with " + URLToConnect + "...");
                    updateProgress(0, 100);
                }
                clientDolphin.setClientConnector(connector);

                updateProgress(40, 100);
                clientDolphin.sync {
                    clientDolphin.send(INITIALIZE_SYSTEM)
                }

                updateProgress(60, 100);
                clientDolphin.sync {
                    clientDolphin.addModelStoreListener(TYPE_ALARM, new ModelStoreListener() {
                        @Override
                        void modelStoreChanged(ModelStoreEvent event) {
                            final PresentationModel pm = event.getPresentationModel();
                            if (event.getType() == ModelStoreEvent.Type.ADDED) {
                                alarmsPMObservableList.add(pm);
                            }
                            if (event.getType() == ModelStoreEvent.Type.REMOVED) {
                                alarmsPMObservableList.remove(pm);
                            }
                        }
                    });

                    clientDolphin.send(INITIALIZE_PM)

                    Closure poll;
                    poll = {
                        clientDolphin.send SYNCHRONIZE_PM, poll }
                    poll();
                    updateMessage("Launching interface...");

                    clientDolphin.sync{
                        updateProgress(100, 100);
                    }

                }
            }
        }
    }

    ClientDolphin getClientDolphin() {
        return clientDolphin
    }

    ObservableList<PresentationModel> getAlarmsPMObservableList() {
        return alarmsPMObservableList
    }
    public void setURLToConnect ( String URLToConnect ) {
        this.URLToConnect = URLToConnect
    }
}
