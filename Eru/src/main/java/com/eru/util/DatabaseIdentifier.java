package com.eru.util;

import lombok.extern.log4j.Log4j;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.SQLException;
@Log4j
public class DatabaseIdentifier {

    private final EntityManager entityManager;

    public DatabaseIdentifier(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public String getDatabaseProductName() {
        try (Connection connection = getConnection(entityManager)) {
            return connection.getMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            log.error("Error trying to get database product name", e);
            return "ERROR";
        }
    }

    private Connection getConnection(EntityManager entityManager) throws SQLException {
        Session session = entityManager.unwrap(Session.class);
        SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
        ConnectionProvider cp = sfi.getConnectionProvider();
        return cp.getConnection();
    }
}
