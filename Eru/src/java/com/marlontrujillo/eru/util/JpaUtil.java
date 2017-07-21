package com.marlontrujillo.eru.util;


import com.marlontrujillo.eru.logger.LogUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author marlon
 */
public class JpaUtil {

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

    public static EntityManager getGlobalEntityManager(){
        if (globalEntityManager == null){
            if(emf == null && createEntityManagerFactory()) {
                globalEntityManager = emf.createEntityManager();
            }
        }
        return globalEntityManager;
    }

    private static boolean createEntityManagerFactory(){
        boolean success = false;
        try{
            emf = Persistence.createEntityManagerFactory("com.psv.jpa");
            LogUtil.logger.info("Entity Manager Factory connected.");
            success = true;
        } catch (Exception e){
            LogUtil.logger.error("Error creating a Entity Manager Factory : " + e.getMessage(), e);
        }
        return success;
    }
}
