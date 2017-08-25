package com.eru.persistence;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

/**
* Created by mtrujillo on 9/02/14.
*/
// TODO: 25-08-17 refactor
@Transactional
public class Dao<T> {

    public enum Order {
        ASC, DESC
    }

    private final Class<T>      entityClass;
    private final EntityManager em;

    public Dao(EntityManager em, Class<T> type){
        this.em = em;
        this.entityClass = type;
    }

    public void create(T t){
        try {
            em.persist(t);
        }catch (Exception e){
            System.err.println("Exception with " + entityClass.getName());
            e.printStackTrace();
        }
    }

    // Merge creates a new instance of your entity, copies the state from the
    // supplied entity, and makes the new copy managed. The instance you pass
    // in will not be managed (any changes you make will not be part of the
    // transaction - unless you call merge again).
    public T update(T t){
        try {
            //em.clear();
            t = em.merge(t);
            return t;
        }catch (Exception e){
            System.err.println("Exception with " + entityClass.getName());
            e.printStackTrace();
        }
        return t;
    }

    public void delete(T t){
        try {
            //em.clear();
            t = em.merge(t);
            em.remove(t);
        }catch (Exception e){
            System.err.println("Exception with " + entityClass.getName());
            e.printStackTrace();
        }
    }

    public List<T> findEntities() {
        return findEntities(true, -1, -1, false, "", null);
    }

    public List<T> findEntities(String orderBy, Order o) {
        return findEntities(true, -1, -1, true, orderBy, o);
    }

    public List<T> findEntities(String orderBy, Order o, int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult, true, orderBy, o);
    }

    private List<T> findEntities(boolean all, int maxResults, int firstResult, boolean filter, String orderBy, Order o){
        try {
            //em.clear();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery();
            Root<T> t = cq.from(entityClass);
            cq.select(t);
            if(filter){
                switch (o) {
                    case ASC:
                        cq.orderBy(cb.asc(t.get(orderBy)));
                        break;
                    case DESC:
                        cq.orderBy(cb.desc(t.get(orderBy)));
                        break;
                }
            }
            Query q = em.createQuery(cq);
            if(!all){
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return (List<T>) q.getResultList();
        } catch (Exception e){
            System.err.println("Exception with " + entityClass.getName());
            e.printStackTrace();
            return null;
        }
    }

    public int getCount() {
        try {
            //em.clear();
            final String QUERY = "SELECT count(e) FROM " + entityClass.getSimpleName() + " e";
            return em.createQuery(QUERY, Long.class).getSingleResult().intValue();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return -1;
    }

}