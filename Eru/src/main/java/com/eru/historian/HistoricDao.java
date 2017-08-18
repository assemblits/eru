package com.eru.historian;

import com.eru.logger.LogUtil;
import com.eru.util.Constants;
import com.eru.entities.Tag;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HistoricDao {
    /* ********** Fields ********** */
    private Connection conn = null;


    /* ********** Constructor ********** */
    public HistoricDao(EntityManager em) {
        this.conn = getConnectionFromHibernate(em);
    }


    /* ********** Methods ********** */
    public List<Map<String, String>> getTagsHistoric(List<Tag> tags) throws SQLException {
        List<Map<String, String>> tagHistoric = new LinkedList<>();
        StringBuilder columnNames  = new StringBuilder();

        if (tags.isEmpty()) {
            LogUtil.logger.info("Trying to get Historic data from database, but there is no tags configured");
            return tagHistoric;
        } else {
            for(Tag t : tags){
                columnNames.append(t.getName()).append(",");
            }
            columnNames.append("time_stamp");
        }

        //TODO
        String retrieveHistoricQuery = "SELECT " + columnNames + " FROM " + Constants.HISTORIC_TABLE_NAME + " ORDER BY time_stamp ASC";

        if(conn == null){
            throw new SQLException("There is no connection with historic table.");
        } else {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(retrieveHistoricQuery);
            while (resultSet.next()) {
                Map<String, String> resultMap = new LinkedHashMap<>();
                for (Tag tag : tags) {
                    resultMap.put(tag.getName(), resultSet.getString(tag.getName()));
                }
                resultMap.put("time_stamp", resultSet.getString("time_stamp"));
                tagHistoric.add(resultMap);
            }
        }
        return  tagHistoric;
    }

    public List<Map<String, String>> getTagsHistoric(String tagName) throws SQLException {
        List<Map<String, String>> tagHistoric = new LinkedList<>();
        StringBuilder columnNames  = new StringBuilder();

        if (tagName.isEmpty()) {
            LogUtil.logger.info("Trying to get Historic data from database, but there is no tags configured");
            return tagHistoric;
        } else {
            columnNames.append(tagName).append(",");
            columnNames.append("time_stamp");
        }

        //TODO
        String retrieveHistoricQuery = "SELECT " + columnNames + " FROM " + Constants.HISTORIC_TABLE_NAME + " ORDER BY time_stamp ASC";

        if(conn == null){
            throw new SQLException("There is no connection with historic table.");
        } else {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(retrieveHistoricQuery);
            while (resultSet.next()) {
                Map<String, String> resultMap = new LinkedHashMap<>();
                resultMap.put(tagName, resultSet.getString(tagName));
                resultMap.put("time_stamp", resultSet.getString("time_stamp"));
                tagHistoric.add(resultMap);
            }
        }
        return  tagHistoric;
    }

    public List<Map<String, String>> getTagsHistoric(List<Tag> tags, LocalDate initDate, LocalDate finalDate) throws SQLException {
        List<Map<String, String>> tagHistoric = new LinkedList<>();
        StringBuilder columnNames  = new StringBuilder();

        if (tags.isEmpty()) {
            throw new SQLException("There is no tags to save.");
        } else {
            for(Tag t : tags){
                columnNames.append(t.getName()).append(",");
            }
            columnNames.append("time_stamp");
        }

        String retrieveHistoricQuery = "SELECT " + columnNames + " FROM "+ Constants.HISTORIC_TABLE_NAME +
                " WHERE time_stamp BETWEEN TO_DATE ('" + initDate.getYear()  + "/" + initDate.getMonthValue()  + "/" + initDate.getDayOfMonth()  + "', 'yyyy/mm/dd')" +
                " AND TO_DATE ('" + finalDate.getYear() + "/" + finalDate.getMonthValue() + "/" + finalDate.getDayOfMonth() + "', 'yyyy/mm/dd')" +
                " ORDER BY time_stamp ASC";


        if(conn == null){
            throw new SQLException("There is no connection with historic table.");
        } else {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(retrieveHistoricQuery);
            while (resultSet.next()) {
                Map<String, String> resultMap = new LinkedHashMap<>();
                for (Tag tag : tags) {
                    resultMap.put(tag.getName(), resultSet.getString(tag.getName()));
                }
                resultMap.put("time_stamp", resultSet.getString("time_stamp"));
                tagHistoric.add(resultMap);
            }
        }
        return  tagHistoric;
    }

    public void addHistoricTagColumn(Tag tag) throws SQLException {
        String deleteString = "ALTER TABLE historic ADD COLUMN " + tag.getName() + " real";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(deleteString);
    }

    public void deleteHistoricTagColumnIfExist(Tag tag) throws SQLException {
        String deleteString = "ALTER TABLE historic DROP COLUMN IF EXISTS " + tag.getName();
        Statement stmt = conn.createStatement();
            stmt.executeUpdate(deleteString);
    }

    public synchronized void executeUpdate(String query) throws SQLException {
        if (conn == null) {
            throw new SQLException("There is no connection with historic table.");
        } else {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
        }
    }

    public synchronized void maintainRowLimit(String tableName, int limit) throws SQLException {
        int size = getTableCount(tableName);

        // Delete if necessary
        if (size > limit) {
            final String stringForDeleteRows = "DELETE FROM " + tableName + " WHERE CTID IN(SELECT CTID FROM " + tableName + " ORDER BY time_stamp LIMIT " + (size - limit) + ")";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(stringForDeleteRows);
        }
    }

    public synchronized int getTableCount(String tableName) throws SQLException {
        if (conn == null) {
            throw new SQLException("There is no connection with historic table.");
        } else {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(*) as count FROM " + tableName);
            rs.next();
            return rs.getInt("count");
        }
    }


    /* ********** Private Methods ********** */
    private Connection getConnectionFromHibernate(EntityManager em) {
        try {
            Session session = em.unwrap(Session.class);
            SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
            ConnectionProvider cp = sfi.getConnectionProvider();
            return cp.getConnection();
        } catch (SQLException ex) {
            System.err.println("HISTORIAN ERROR: " + ex.getMessage());
            return null;
        }
    }

}
