package com.eru.historian;

import com.eru.entities.Tag;
import com.eru.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Log4j
@Component
@RequiredArgsConstructor
//// TODO: 24-08-17 Refactor
public class HistoricDao {
    /* ********** Fields ********** */
    private final JdbcTemplate jdbcTemplate;

    /* ********** Methods ********** */
    public List<Map<String, String>> getTagsHistoric(List<Tag> tags) throws SQLException {
        List<Map<String, String>> tagHistoric = new LinkedList<>();
        StringBuilder columnNames = new StringBuilder();

        if (tags.isEmpty()) {
            log.info("Trying to get Historic data from database, but there is no tags configured");
            return tagHistoric;
        } else {
            for (Tag t : tags) {
                columnNames.append(t.getName()).append(",");
            }
            columnNames.append("time_stamp");
        }

        //TODO
        String retrieveHistoricQuery = "SELECT " + columnNames + " FROM " + Constants.HISTORIC_TABLE_NAME + " ORDER BY time_stamp ASC";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(retrieveHistoricQuery);
        for (Map<String, Object> result : results) {
            Map<String, String> resultMap = new LinkedHashMap<>();
            for (Tag tag : tags) {
                resultMap.put(tag.getName(), (String) result.get(tag.getName()));
            }
            resultMap.put("time_stamp", (String) result.get("time_stamp"));
            tagHistoric.add(resultMap);
        }
        return tagHistoric;
    }

    public List<Map<String, String>> getTagsHistoric(String tagName) throws SQLException {
        List<Map<String, String>> tagHistoric = new LinkedList<>();
        StringBuilder columnNames = new StringBuilder();

        if (tagName.isEmpty()) {
            log.info("Trying to get Historic data from database, but there is no tags configured");
            return tagHistoric;
        } else {
            columnNames.append(tagName).append(",");
            columnNames.append("time_stamp");
        }

        //TODO
        String retrieveHistoricQuery = "SELECT " + columnNames + " FROM " + Constants.HISTORIC_TABLE_NAME + " ORDER BY time_stamp ASC";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(retrieveHistoricQuery);
        for (Map<String, Object> result : results) {
            Map<String, String> resultMap = new LinkedHashMap<>();
            resultMap.put(tagName, (String) result.get(tagName));
            resultMap.put("time_stamp", (String) result.get("time_stamp"));
            tagHistoric.add(resultMap);
        }
        return tagHistoric;
    }

    public List<Map<String, String>> getTagsHistoric(List<Tag> tags, LocalDate initDate, LocalDate finalDate) throws SQLException {
        List<Map<String, String>> tagHistoric = new LinkedList<>();
        StringBuilder columnNames = new StringBuilder();

        if (tags.isEmpty()) {
            throw new SQLException("There is no tags to save.");
        } else {
            for (Tag t : tags) {
                columnNames.append(t.getName()).append(",");
            }
            columnNames.append("time_stamp");
        }

        String retrieveHistoricQuery = "SELECT " + columnNames + " FROM " + Constants.HISTORIC_TABLE_NAME +
                " WHERE time_stamp BETWEEN TO_DATE ('" + initDate.getYear() + "/" + initDate.getMonthValue() + "/" + initDate.getDayOfMonth() + "', 'yyyy/mm/dd')" +
                " AND TO_DATE ('" + finalDate.getYear() + "/" + finalDate.getMonthValue() + "/" + finalDate.getDayOfMonth() + "', 'yyyy/mm/dd')" +
                " ORDER BY time_stamp ASC";


        List<Map<String, Object>> results = jdbcTemplate.queryForList(retrieveHistoricQuery);
        for (Map<String, Object> result : results) {
            Map<String, String> resultMap = new LinkedHashMap<>();
            for (Tag tag : tags) {
                resultMap.put(tag.getName(), (String) result.get(tag.getName()));
            }
            resultMap.put("time_stamp", (String) result.get("time_stamp"));
            tagHistoric.add(resultMap);
        }
        return tagHistoric;
    }

    public void addHistoricTagColumn(Tag tag) throws SQLException {
        String deleteString = "ALTER TABLE historic ADD COLUMN " + tag.getName() + " real";
        jdbcTemplate.execute(deleteString);
    }

    public void deleteHistoricTagColumnIfExist(Tag tag) throws SQLException {
        String deleteString = "ALTER TABLE historic DROP COLUMN IF EXISTS " + tag.getName();
        jdbcTemplate.execute(deleteString);
    }

    public synchronized void executeUpdate(String query) throws SQLException {
        jdbcTemplate.execute(query);
    }

    public synchronized void maintainRowLimit(String tableName, int limit) throws SQLException {
        int size = getTableCount(tableName);

        // Delete if necessary
        if (size > limit) {
            final String stringForDeleteRows = "DELETE FROM " + tableName + " WHERE CTID IN(SELECT CTID FROM " + tableName + " ORDER BY time_stamp LIMIT " + (size - limit) + ")";
            jdbcTemplate.execute(stringForDeleteRows);
        }
    }

    public synchronized int getTableCount(String tableName) throws SQLException {
        return jdbcTemplate.queryForObject("SELECT count(*) as count FROM " + tableName, Integer.class);
    }
}
