package com.eru.persistence

import com.eru.entities.Connection
import com.eru.entities.Device
import com.eru.entities.Tag
import com.eru.entities.User
import com.eru.gui.ApplicationContextHolder
import com.eru.historian.HistoricDao
import groovyx.gpars.agent.Agent
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import lombok.extern.log4j.Log4j

import javax.persistence.EntityManager
import java.sql.SQLException
import java.util.function.Consumer
import java.util.function.Predicate

/**
 * Created by mtrujillo on 9/3/2015.
 */
@Log4j
class Container {
    private static final Container instance = new Container()

    public static Container getInstance() {
        return instance
    }

    // The lists in the Daos keep an image of the database status. This is useful when we want to see if any object is
    // in the database without tranasactions. The Dao list objects are the same as the container list objects, they have
    // the same reference.
    private DeviceRepository deviceRepository
    private ObservableList<Device> devices
    private ConnectionRepository connectionRepository
    private ObservableList<Connection> connections
    private TagRepository tagRepository
    private ObservableList<Tag> tags
    private UserRepository userRepository
    private ObservableList<User> users

    /* **** Container Lists **** */
    // This lists are used in the APP, they are different than the Daos lists, but the objects are the same. Therefore,
    // any edition on the container lists will edit the dao object, but if any object is removed on the dao list, the
    // object will remain in the dao list.
    //
    // Another characteristic of this lists is that they are wrapped in an Agent, and the modifications on the objects
    // of the list has to be done through Closure.
    private Agent<ObservableList<Device>> devicesAgent
    private Agent<ObservableList<Connection>> connectionsAgent
    private Agent<ObservableList<Tag>> tagsAgent
    private Agent<ObservableList<User>> usersAgent
    private Agent<Boolean> loadedAgent

    /* ********** Constructor ********** */

    private Container() {
        this.devicesAgent = new Agent(FXCollections.observableArrayList())
        this.connectionsAgent = new Agent(FXCollections.observableArrayList())
        this.tagsAgent = new Agent(FXCollections.observableArrayList())
        this.usersAgent = new Agent(FXCollections.observableArrayList())
        this.loadedAgent = new Agent(false)
    }

    /* ********** Methods ********** */

    public synchronized Boolean loadDatabase() {

        this.devicesAgent.updateValue(FXCollections.observableArrayList())
        this.connectionsAgent.updateValue(FXCollections.observableArrayList())
        this.tagsAgent.updateValue(FXCollections.observableArrayList())
        this.usersAgent.updateValue(FXCollections.observableArrayList())

        userRepository = ApplicationContextHolder.getApplicationContext().getBean(UserRepository)
        deviceRepository = ApplicationContextHolder.getApplicationContext().getBean(DeviceRepository)
        connectionRepository = ApplicationContextHolder.getApplicationContext().getBean(ConnectionRepository)
        tagRepository = ApplicationContextHolder.getApplicationContext().getBean(TagRepository)
        tags = FXCollections.observableArrayList(new ArrayList<>())
        devices = FXCollections.observableArrayList(new ArrayList<>())
        connections = FXCollections.observableArrayList(new ArrayList<>())
        users = FXCollections.observableArrayList(new ArrayList<>())

        users.addAll(userRepository.findAllByOrderByUserNameAsc())
        tags.addAll(tagRepository.findAllByOrderByNameAsc())
        devices.addAll(deviceRepository.findAllByOrderByNameAsc())
        connections.addAll(connectionRepository.findAllByOrderByNameAsc())

        // Copy the actual state of database to Container
        loadAllReadyDaosLists()

        // Update status
        loadedAgent.updateValue(true)

        log.info("Database loaded")

        return loadedAgent.val
    }

    public void saveDatabase() {
        log.info("Saving database")
        try {
            removeOrphanedPersistedData()
            executeCascadeSaving()
        } catch (Exception e) {
            log.error("Error saving database", e)
        }
        log.info("Database saved")
    }

    private void loadAllReadyDaosLists() {
        usersAgent.val.addAll(users)
        tagsAgent.val.addAll(tags)
        devicesAgent.val.addAll(devices)
        connectionsAgent.val.addAll(connections)
    }

    /**
     * Clean the objects in two steps:
     *  1) If there is a user in DaoList but was deleted in the app (orphan user): delete it from DaoList and DB.
     *  2) If there is a user in the App but is not present in DaoList, add it to DaoList
     *  3) Has to be sent and wait because they use the same Entity Manager/Database Connection.
     */
    private void removeOrphanedPersistedData() {

        /* ** USERS ** */
        usersAgent.sendAndWait { ObservableList<User> usersInApp ->
            final List<User> orphanUsers = new ArrayList<>(users)
            orphanUsers.removeAll(usersInApp.asList())
            orphanUsers.stream().forEach(new Consumer<User>() {
                @Override
                void accept(User orphanUser) {
                    userRepository.delete(orphanUser)
                }
            })
            users.removeAll(orphanUsers)

            final List<User> newUsers = new ArrayList<>(usersInApp)
            newUsers.removeAll(users)
            newUsers.stream().forEach(new Consumer<User>() {
                @Override
                void accept(User newUser) {
                    userRepository.save(newUser)
                }
            })
            users.addAll(newUsers)
        }

        /* ** HISTORICAL ** */
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager()
        HistoricDao historicDao = new HistoricDao(em)

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
                        historicDao.deleteHistoricTagColumnIfExist(tagWithHistoricalDisabled)
                    } catch (SQLException e) {
                        e.printStackTrace()
                    }
                }
            })
        }
        em.close()

        /* ** TAGS ** */
        tagsAgent.sendAndWait { ObservableList<Tag> tagsInApp ->
            final List<Tag> orphanTags = new ArrayList<>(tags)
            orphanTags.removeAll(tagsInApp.asList())
            orphanTags.stream().forEach(new Consumer<Tag>() {
                @Override
                void accept(Tag orphanTag) {
                    tagRepository.delete(orphanTag)
                }
            })
            tags.removeAll(orphanTags)

            final List<Tag> newTags = new ArrayList<>(tagsInApp)
            newTags.removeAll(tags)
            newTags.stream().forEach(new Consumer<Tag>() {
                @Override
                void accept(Tag newTag) {
                    tagRepository.save(newTag)
                }
            })
            tags.addAll(newTags)
        }

        /* ** MODBUS SERIAL DEVICES ** */
        devicesAgent.sendAndWait { ObservableList<Device> devicesInApp ->
            final List<Device> orphanDevices = new ArrayList<>(devices)
            orphanDevices.removeAll(devicesInApp.asList())
            orphanDevices.stream().forEach(new Consumer<Device>() {
                @Override
                void accept(Device orphanDevice) {
                    deviceRepository.delete(orphanDevice)
                }
            })
            devices.removeAll(orphanDevices)

            final List<Device> newDevices = new ArrayList<>(devicesInApp)
            newDevices.removeAll(devices)
            newDevices.stream().forEach(new Consumer<Device>() {
                @Override
                void accept(Device newDevice) {
                    deviceRepository.save(newDevice)
                }
            })
            devices.addAll(newDevices)
        }

        /* ** MODBUS CONNECTIONS ** */
        connectionsAgent.sendAndWait { ObservableList<Connection> connectionsInApp ->
            final List<Connection> orphanConnections = new ArrayList<>(connections)
            orphanConnections.removeAll(connectionsInApp.asList())
            orphanConnections.stream().forEach(new Consumer<Connection>() {
                @Override
                void accept(Connection orphanConnection) {
                    connectionRepository.delete(orphanConnection)
                }
            })
            connections.removeAll(orphanConnections)

            final List<Connection> newConnections = new ArrayList<>(connectionsInApp)
            newConnections.removeAll(connections)
            newConnections.stream().forEach(new Consumer<Connection>() {
                @Override
                void accept(Connection newConnection) {
                    connectionRepository.save(newConnection)
                }
            })
            connections.addAll(newConnections)
        }
    }

    private void executeCascadeSaving() {
        /* ** USERS ** */
        users.forEach(new Consumer<User>() {
            @Override
            void accept(User user) {
                userRepository.save(user)
            }
        })

        /* ** HISTORICAL ** */
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager()
        HistoricDao historicDao = new HistoricDao(em)
        tags.stream()
                .filter(new Predicate<Tag>() {
            @Override
            boolean test(Tag tagInDaoList) {
                tagInDaoList.getHistoricalEnabled()
            }
        })
                .forEach(new Consumer<Tag>() {
            @Override
            void accept(Tag tagWithHistoricalEnabled) {
                try {
                    historicDao.addHistoricTagColumn(tagWithHistoricalEnabled)
                } catch (SQLException e) {
                    log.debug("Error saving tagsAgent", e)
                }
            }
        })
        em.close()

        tags.forEach(new Consumer<Tag>() {
            @Override
            void accept(Tag tag) {
                tagRepository.save(tag)
            }
        })

        devicesAgent.val.forEach(new Consumer<Device>() {
            @Override
            void accept(Device deviceInDaoList) {
                deviceRepository.save(deviceInDaoList)
            }
        })

        connectionsAgent.val.forEach(new Consumer<Connection>() {
            @Override
            void accept(Connection connection) {
                connectionRepository.save(connection)
            }
        })
    }

    Agent<ObservableList<Device>> getDevicesAgent() {
        return devicesAgent
    }

    Agent<ObservableList<Connection>> getConnectionsAgent() {
        return connectionsAgent
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
