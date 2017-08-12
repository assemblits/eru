package com.marlontrujillo.eru.util;

import com.marlontrujillo.eru.exception.EntityManagerFactoryCreationException;
import com.marlontrujillo.eru.logger.LogUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Persistence.class, LogUtil.class})
public class JpaUtilTest {

    @Mock
    private EntityManagerFactory entityManagerFactory;
    @Mock
    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.mockStatic(LogUtil.class);

        when(Persistence.createEntityManagerFactory("com.psv.jpa"))
                .thenReturn(entityManagerFactory);

        when(Persistence.createEntityManagerFactory("embedded"))
                .thenReturn(entityManagerFactory);

        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
    }

    @Test(expected = EntityManagerFactoryCreationException.class)
    public void createEntityManagerWithFailingPersistenceUnits() throws Exception {
        when(Persistence.createEntityManagerFactory(Mockito.anyString()))
                .thenThrow(PersistenceException.class);

        JpaUtil.getEntityManagerFactory();
    }

    @Test
    public void getGlobalEntityManager() throws Exception {
        EntityManager globalEntityManager = JpaUtil.getGlobalEntityManager();

        Assert.assertNotNull(globalEntityManager);
    }

    @Test
    public void createEntityManagerWithDefaultPersistenceUnit() throws Exception {
        EntityManagerFactory createdEntityManagerFactory = JpaUtil.getEntityManagerFactory();

        Assert.assertNotNull(createdEntityManagerFactory);
    }

    @Test
    public void createEntityManagerWithFallbackPersistenceUnit() throws Exception {
        when(Persistence.createEntityManagerFactory(Mockito.eq("com.psv.jpa")))
                .thenThrow(PersistenceException.class);

        EntityManagerFactory createdEntityManagerFactory = JpaUtil.getEntityManagerFactory();

        Assert.assertNotNull(createdEntityManagerFactory);
    }
}
