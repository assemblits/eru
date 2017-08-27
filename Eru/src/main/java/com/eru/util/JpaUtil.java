package com.eru.util;


import com.eru.exception.EntityManagerFactoryCreationException;
import lombok.extern.log4j.Log4j;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

/**
 * @author marlon
 */
@Log4j
public class JpaUtil {

    private static final String PERSISTENCE_UNIT = "com.psv.jpa";

    private static EntityManagerFactory emf;
    /**
     * For an application-managed entity manager the persistence context is created when the entity manager is created
     * and kept until the entity manager is closed.
     * A resource-local entity manager or an entity manager created with EntityManagerFactory.createEntityManager()
     * (application-managed) has a one-to-one relationship with a persistence context. In other situations persistence
     * context propagation occurs.
     */
    private static EntityManager globalEntityManager;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) createEntityManagerFactory();
        return emf;
    }

    public static EntityManager getGlobalEntityManager() {
        if (globalEntityManager == null) {
            if (emf == null) {
                createEntityManagerFactory();
            }
            globalEntityManager = emf.createEntityManager();
        }
        return globalEntityManager;
    }

    private static void createEntityManagerFactory() {
        PropertiesLoader propertiesLoader = new PropertiesLoader();
        emf = createEntityManagerFactory(PERSISTENCE_UNIT, propertiesLoader.loadPropertiesAsMap("database"))
                .orElseGet(() -> createEntityManagerFactory(PERSISTENCE_UNIT, propertiesLoader.loadPropertiesAsMap("fallback-database"))
                        .orElseThrow(() -> {
                            log.error("Unable to create entity manager factory");
                            return new EntityManagerFactoryCreationException();
                        }));
    }

    private static Optional<EntityManagerFactory> createEntityManagerFactory(String persistenceUnitName, Map<String, String> properties) {
        try {
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
            log.info(format("Entity Manager Factory connected with %s persistence unit.", persistenceUnitName));
            return Optional.of(entityManagerFactory);
        } catch (Exception e) {
            log.warn(format("Error creating a Entity Manager Factory with %s persistence unit: %s",
                    persistenceUnitName, e.getMessage()), e);
            return Optional.empty();
        }
    }
}
