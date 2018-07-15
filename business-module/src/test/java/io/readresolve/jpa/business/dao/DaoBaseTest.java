package io.readresolve.jpa.business.dao;

import javax.persistence.*;

import org.junit.*;

/**
 * Class handling {@code EntityManagerFactory} creation and realease to help in
 * testing data access classes.
 */
public class DaoBaseTest {

    /**
     * Entity manager factory for subclasses.
     */
    protected static EntityManagerFactory EMF;

    /**
     * Creates the entity manager factory.
     */
    @BeforeClass
    public static void init() {
	EMF = Persistence.createEntityManagerFactory("JpaPersistenceUnit");
    }

    /**
     * Returns the entity manager factory.
     *
     * @return the entity manager factory
     */
    protected final static EntityManagerFactory getFactory() {
	return EMF;
    }

    /**
     * Closes the entity manager factory.
     */
    @AfterClass
    public static void tearDown() {
	EMF.close();
    }
}
