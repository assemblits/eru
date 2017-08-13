package com.eru.persistence

import com.eru.comm.connection.Connection
import com.eru.tag.Tag
import com.eru.comm.device.Device
import com.eru.historian.HistoricDao
import com.eru.logger.LogUtil
import com.eru.user.User
import com.eru.util.JpaUtil
import groovyx.gpars.agent.Agent
import javafx.collections.FXCollections
import javafx.collections.ObservableList

import javax.persistence.EntityManager
import java.sql.SQLException
import java.util.function.Consumer
import java.util.function.Predicate

/**
 * Created by mtrujillo on 9/3/2015.
 */
class Container {
    /* ********** Static Fields ********** */
    private static final Container instance = new Container();
    public static Container getInstance() {
        return instance;
    }

    /* **** Dao **** */
    // The lists in the Daos keep an image of the database status. This is useful when we want to see if any object is
    // in the database without tranasactions. The Dao list objects are the same as the container list objects, they have
    // the same reference.
    private DaoList<Device> deviceDaoList;
    private DaoList<Connection> connectionDaoList;
    private DaoList<Tag>        tagDaoList;
    private DaoList<User> userDaoList;

    /* **** Container Lists **** */
    // This lists are used in the APP, they are different than the Daos lists, but the objects are the same. Therefore,
    // any edition on the container lists will edit the dao object, but if any object is removed on the dao list, the
    // object will remain in the dao list.
    //
    // Another characteristic of this lists is that they are wrapped in an Agent, and the modifications on the objects
    // of the list has to be done through Closure.
    private Agent<ObservableList<Device>>     devicesAgent;
    private Agent<ObservableList<Connection>> connectionsAgent;
    private Agent<ObservableList<Tag>>        tagsAgent;
    private Agent<ObservableList<User>>       usersAgent;
    private Agent<Boolean>                    loadedAgent;


    /* ********** Constructor ********** */
    private Container() {
        this.devicesAgent     = new Agent(FXCollections.observableArrayList());
        this.connectionsAgent = new Agent(FXCollections.observableArrayList());
        this.tagsAgent        = new Agent(FXCollections.observableArrayList());
        this.usersAgent       = new Agent(FXCollections.observableArrayList());
        this.loadedAgent      = new Agent(false);
    }

    /* ********** Methods ********** */
    public synchronized Boolean loadDatabase(){
        final EntityManager EM       = JpaUtil.getGlobalEntityManager();

        // Clear the persistent context
        EM.clear()

        if(EM){
            // Clean agents
            this.devicesAgent.updateValue(FXCollections.observableArrayList());
            this.connectionsAgent.updateValue(FXCollections.observableArrayList());
            this.tagsAgent.updateValue(FXCollections.observableArrayList());
            this.usersAgent.updateValue(FXCollections.observableArrayList());

            // Create DAO using Hibernate Entity Manager
            userDaoList       = new DaoList<>(User.class, EM, Dao.Order.ASC, "userName");
            deviceDaoList     = new DaoList<>(Device.class, EM, Dao.Order.ASC, "name");
            connectionDaoList = new DaoList<>(Connection.class, EM, Dao.Order.ASC, "name");
            tagDaoList        = new DaoList<>(Tag.class, EM, Dao.Order.ASC, "name");

            // Load ALL with DAOs
            LogUtil.logger.info("Loading database");
            userDaoList.fillFromDatabase();
            tagDaoList.fillFromDatabase();
            deviceDaoList.fillFromDatabase();
            connectionDaoList.fillFromDatabase();

            // Copy the actual state of database to Container
            loadAllReadyDaosLists();

            // Update status
            loadedAgent.updateValue(true);

            LogUtil.logger.info("Database loaded");
        } else {
            // Update status
            loadedAgent.updateValue(false);
            LogUtil.logger.error("Container cannot load database. There is no Database connection.");
        }
        return loadedAgent.val
    }

    public void saveDatabase() {
        LogUtil.logger.info("Saving database");
        try {
            removeOrphanedPersistedData();
            executeCascadeSaving();
        } catch (Exception e){
            LogUtil.logger.error("Error saving database", e);
        }
        LogUtil.logger.info("Database saved");
    }

    /* ********** Private Methods ********** */
    private void loadAllReadyDaosLists(){
        usersAgent.val.addAll(userDaoList.getVal())
        tagsAgent.val.addAll(tagDaoList.getVal())
        devicesAgent.val.addAll(deviceDaoList.getVal())
        connectionsAgent.val.addAll(connectionDaoList.getVal())
    }

    /**
     * Clean the objects in two steps:
     *  1) If there is a user in DaoList but was deleted in the app (orphan user): delete it from DaoList and DB.
     *  2) If there is a user in the App but is not present in DaoList, add it to DaoList
     *  3) Has to be sent and wait because they use the same Entity Manager/Database Connection.
     */
    private void removeOrphanedPersistedData(){

        /* ** USERS ** */
        usersAgent.sendAndWait { ObservableList<User> usersInApp ->
            final List<User> orphanUsers = new ArrayList<>(userDaoList.val)
            orphanUsers.removeAll(usersInApp.asList())
            orphanUsers.stream().forEach(new Consumer<User>() {
                @Override
                void accept(User orphanUser) {
                    userDaoList.deleteOnDB(orphanUser)
                }
            })
            userDaoList.val.removeAll(orphanUsers)

            final List<User> newUsers = new ArrayList<>(usersInApp)
            newUsers.removeAll(userDaoList.val)
            newUsers.stream().forEach(new Consumer<User>() {
                @Override
                void accept(User newUser) {
                    userDaoList.createOnDB(newUser)
                }
            })
            userDaoList.val.addAll(newUsers)
        }

        /* ** HISTORICAL ** */
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        HistoricDao historicDao = new HistoricDao(em);

        tagsAgent.sendAndWait { tagsInApp ->
            tagsInApp.stream().filter(new Predicate<Tag>() {
                @Override
                boolean test(Tag appTag) {
                    !appTag.getHistoricalEnabled()
                }
            }).forEach(new Consumer<Tag>() {
                @Override
                void accept(Tag tagWithHistoricalDisabled) {
                    try {
                        historicDao.deleteHistoricTagColumnIfExist(tagWithHistoricalDisabled);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            })
        }
        em.close();

        /* ** TAGS ** */
        tagsAgent.sendAndWait { ObservableList<Tag> tagsInApp ->
            final List<Tag> orphanTags = new ArrayList<>(tagDaoList.val)
            orphanTags.removeAll(tagsInApp.asList())
            orphanTags.stream().forEach(new Consumer<Tag>() {
                @Override
                void accept(Tag orphanTag) {
                    tagDaoList.deleteOnDB(orphanTag)
                }
            })
            tagDaoList.val.removeAll(orphanTags)

            final List<Tag> newTags = new ArrayList<>(tagsInApp)
            newTags.removeAll(tagDaoList.val)
            newTags.stream().forEach(new Consumer<Tag>() {
                @Override
                void accept(Tag newTag) {
                    tagDaoList.createOnDB(newTag)
                }
            })
            tagDaoList.val.addAll(newTags)
        }

        /* ** MODBUS SERIAL DEVICES ** */
        devicesAgent.sendAndWait { ObservableList<Device> devicesInApp ->
            final List<Device> orphanDevices = new ArrayList<>(deviceDaoList.val)
            orphanDevices.removeAll(devicesInApp.asList())
            orphanDevices.stream().forEach(new Consumer<Device>() {
                @Override
                void accept(Device orphanDevice) {
                    deviceDaoList.deleteOnDB(orphanDevice)
                }
            })
            deviceDaoList.val.removeAll(orphanDevices)

            final List<Device> newDevices = new ArrayList<>(devicesInApp)
            newDevices.removeAll(deviceDaoList.val)
            newDevices.stream().forEach(new Consumer<Device>() {
                @Override
                void accept(Device newDevice) {
                    deviceDaoList.createOnDB(newDevice)
                }
            })
            deviceDaoList.val.addAll(newDevices)
        }

        /* ** MODBUS CONNECTIONS ** */
        connectionsAgent.sendAndWait { ObservableList<Connection> connectionsInApp ->
            final List<Connection> orphanConnections = new ArrayList<>(connectionDaoList.val)
            orphanConnections.removeAll(connectionsInApp.asList())
            orphanConnections.stream().forEach(new Consumer<Connection>() {
                @Override
                void accept(Connection orphanConnection) {
                    connectionDaoList.deleteOnDB(orphanConnection)
                }
            })
            connectionDaoList.val.removeAll(orphanConnections)

            final List<Connection> newConnections = new ArrayList<>(connectionsInApp)
            newConnections.removeAll(connectionDaoList.val)
            newConnections.stream().forEach(new Consumer<Connection>() {
                @Override
                void accept(Connection newConnection) {
                    connectionDaoList.createOnDB(newConnection)
                }
            })
            connectionDaoList.val.addAll(newConnections)
        }
    }

    private void executeCascadeSaving() {
        /* ** USERS ** */
        userDaoList.val.forEach(new Consumer<User>() {
            @Override
            void accept(User userInDaoList) {
                userDaoList.updateOnDB(userInDaoList)
            }
        });

        /* ** HISTORICAL ** */
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        HistoricDao historicDao = new HistoricDao(em);
        tagDaoList.getVal().stream()
                .filter(new Predicate<Tag>() {
            @Override
            boolean test(Tag tagInDaoList) {
                tagInDaoList.getHistoricalEnabled();
            }
        })
                .forEach(new Consumer<Tag>() {
            @Override
            void accept(Tag tagWithHistoricalEnabled) {
                try {
                    historicDao.addHistoricTagColumn(tagWithHistoricalEnabled);
                } catch (SQLException e) {
                    LogUtil.logger.debug("Error saving tagsAgent", e);
                }
            }
        });
        em.close();

        /* ** TAGS ** */
        tagDaoList.val.forEach(new Consumer<Tag>() {
            @Override
            void accept(Tag tagInDaoList) {
                tagDaoList.updateOnDB(tagInDaoList);
            }
        });

        /* ** MODBUS DEVICES ** */
        devicesAgent.val.forEach(new Consumer<Device>() {
            @Override
            void accept(Device deviceInDaoList) {
                deviceDaoList.updateOnDB(deviceInDaoList);
            }
        });

        /* ** MODBUS CONNECTIONS ** */
        connectionsAgent.val.forEach(new Consumer<Connection>() {
            @Override
            void accept(Connection connectionInDaoList) {
                connectionDaoList.updateOnDB(connectionInDaoList);
            }
        });
    }

    /* ********** Getters ********** */

    Agent<ObservableList<Device>> getDevicesAgent() {
        return devicesAgent;
    }

    Agent<ObservableList<Connection>> getConnectionsAgent() {
        return connectionsAgent;
    }

    Agent<ObservableList<Tag>> getTagsAgent() {
        return tagsAgent
    }

    Agent<ObservableList<User>> getUsersAgent() {
        return usersAgent
    }

    Agent<Boolean> getDatabaseLoadedAgent() {
        return loadedAgent
    }
}
