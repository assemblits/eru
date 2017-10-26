/******************************************************************************
 * Copyright (c) 2017 Assemblits contributors                                 *
 *                                                                            *
 * This file is part of Eru The open JavaFX SCADA by Assemblits Organization. *
 *                                                                            *
 * Eru The open JavaFX SCADA is free software: you can redistribute it        *
 * and/or modify it under the terms of the GNU General Public License         *
 *  as published by the Free Software Foundation, either version 3            *
 *  of the License, or (at your option) any later version.                    *
 *                                                                            *
 * Eru is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.            *
 ******************************************************************************/
package org.assemblits.eru.historian;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.entities.Tag;
import org.assemblits.eru.util.Constants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HistoricDao {
    private final JdbcTemplate jdbcTemplate;

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
