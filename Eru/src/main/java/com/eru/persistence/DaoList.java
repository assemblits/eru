package com.eru.persistence;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.log4j.Log4j;

import javax.persistence.EntityManager;
import java.util.ArrayList;

/**
 * Created by mtrujillo on 16/05/14.
 */
@Log4j
public class DaoList<T> {

    private final String            orderBy;
    private final Dao.Order         order;
    private final Dao<T>            dao;
    private final ObservableList<T> value;

    public DaoList(Class<T> tClass, EntityManager entityManager, Dao.Order order, String orderBy) {
        this.dao        = new Dao<>(entityManager, tClass);
        this.value      = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
        this.order      = order;
        this.orderBy    = orderBy;
    }

    public void fillFromDatabase() {
        try {
            if (dao.getCount()>0){
                value.addAll(dao.findEntities(orderBy, order));
            }
        } catch (Exception e) {
            value.clear();
            log.error("Error in Dao Service", e);
        }
    }

    public void createOnDB(final T o){
        try {
            dao.create(o);
        } catch (Exception e) {
            log.error("Error in Dao Service", e);
        }
    }

    public void updateOnDB(final T o){
        try {
            dao.update(o);
        } catch (Exception e) {
            log.error("Error in Dao Service", e);
        }
    }

    public void deleteOnDB(final T o){
        try {
            dao.delete(o);
        } catch (Exception e) {
            log.error("Error in Dao Service", e);
        }
    }

    public ObservableList<T> getVal() {
        return value;
    }

}
