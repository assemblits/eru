package com.eru.util;

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseIdentifierTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private Session session;
    @Mock
    private SessionFactoryImplementor sessionFactoryImplementor;
    @Mock
    private ConnectionProvider connectionProvider;
    @Mock
    private Connection connection;
    @Mock
    private DatabaseMetaData metadata;
    private DatabaseIdentifier databaseIdentifier;

    @Before
    public void setUp() throws Exception {
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.getSessionFactory()).thenReturn(sessionFactoryImplementor);
        when(sessionFactoryImplementor.getConnectionProvider()).thenReturn(connectionProvider);
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metadata);

        databaseIdentifier = new DatabaseIdentifier(entityManager);
    }

    @Test
    public void getDatabaseProductName() throws Exception {
        when(metadata.getDatabaseProductName()).thenReturn("GoodDB");

        String databaseProductName = databaseIdentifier.getDatabaseProductName();

        Assert.assertEquals("GoodDB", databaseProductName);
    }

    @Test
    public void getDatabaseProductNameReturnERRORWhenExceptionHappen() throws Exception {
        doThrow(SQLException.class).when(connectionProvider).getConnection();

        String databaseProductName = databaseIdentifier.getDatabaseProductName();

        Assert.assertEquals("ERROR", databaseProductName);
    }

}