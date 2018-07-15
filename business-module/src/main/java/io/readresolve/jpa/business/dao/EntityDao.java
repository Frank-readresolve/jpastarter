package io.readresolve.jpa.business.dao;

import java.util.*;

import javax.persistence.*;

/**
 * A base Data Access Object class to deal with common <b>atomic</b> operations
 * on a single {@code EntityManagerFactory} in a non-JTA context. Transactional
 * operations fully manage the transactional context from beginning to
 * rollbacking when necessary.
 * <p>
 * Please note that the class is not responsible for closing the entity manager
 * factory, this should be done by a client class like an initialization servlet
 * or a context listener.
 * <p>
 * If the provided entity manager factory is closed or a JTA context factory,
 * any subsequent access to any of the method will result in an
 * {@code IllegalStateException}.
 */
public class EntityDao {

    /**
     * The underliying entity manager factory.
     */
    private EntityManagerFactory factory;

    /**
     * Creates a new {@code EntityDao} with given factory.
     *
     * @param factory
     *            an entity manager factory
     * @throws NullPointerException
     *             if {@code factory} is {@code null}
     */
    public EntityDao(EntityManagerFactory factory) {
	Objects.requireNonNull(factory);
	this.factory = factory;
    }

    /**
     * Returns the underliying entity manager factory.
     *
     * @return the entity manager factory
     */
    public final EntityManagerFactory getFactory() {
	return factory;
    }

    /**
     * Returns the value of the {@code hibernate.jdbc.batch_size} property.
     *
     * @return the JDBC batch size configured value
     * @throws NumberFormatException
     *             if batch size property is not set
     */
    public final int getBatchSize() {
	String batchSizeStr = (String) factory.getProperties()
		.get("hibernate.jdbc.batch_size");
	return Integer.valueOf(batchSizeStr);
    }

    /**
     * Persists an entity.
     *
     * @param entity
     *            an entity instance to persist
     */
    public final void persist(Object entity) {
	EntityManager em = null;
	try {
	    em = start();
	    em.persist(entity);
	} finally {
	    end(em);
	}
    }

    /**
     * Updates an entity.
     *
     * @param entity
     *            an entity instance to update
     */
    public final void update(Object entity) {
	EntityManager em = null;
	try {
	    em = start();
	    em.merge(entity);
	} finally {
	    end(em);
	}
    }

    /**
     * Removes an entity; either managed or detached.
     *
     * @param entity
     *            an entity instance to remove
     */
    public final void remove(Object entity) {
	EntityManager em = null;
	try {
	    em = start();
	    em.remove(em.contains(entity) ? entity : em.merge(entity));
	} finally {
	    end(em);
	}
    }

    /**
     * Return a persisted entity with given identifier.
     *
     * @param <T>
     * @param type
     *            the type of entity
     * @param id
     *            the identifier
     * @return a persisted entity with given identifier; {@code null} if does
     *         not exist
     */
    public final <T> T find(Class<T> type, Object id) {
	EntityManager em = startSimple();
	T result = em.find(type, id);
	end(em);
	return result;
    }

    /**
     * Starts a unit of work beginning a transaction.
     *
     * @return a new entity manager
     * @throws IllegalStateException
     *             if a transaction is already active
     */
    protected final EntityManager start() {
	EntityManager em = factory.createEntityManager();
	em.getTransaction().begin();
	return em;
    }

    /**
     * Starts a unit of work without beginning a transaction.
     *
     * @return a new entity manager
     */
    protected final EntityManager startSimple() {
	return factory.createEntityManager();
    }

    /**
     * Convenient atomic-like method for subclasses in order to manage commit,
     * rollback end close given entity manager.
     *
     * @param em
     *            an entity manager
     */
    @SuppressWarnings("unused")
    protected final static void end(EntityManager em) {
	try {
	    EntityTransaction tx = em.getTransaction();
	    if (tx.isActive()) {
		try {
		    tx.commit();
		} catch (Exception ex) {
		    tx.rollback();
		}
	    }
	} finally {
	    em.clear();
	    em.close();
	}
    }

    /**
     * Executes and returns a single result given specified query string.
     * <p>
     * This implementation does not handle parameters
     *
     * @param jpqlQueryString
     *            a JPQL query string
     * @return a single result
     */
    public final Object selectSingle(String jpqlQueryString) {
	EntityManager em = startSimple();
	Object result = em.createQuery(jpqlQueryString).getSingleResult();
	end(em);
	return result;
    }

    /**
     * Executes and returns a single result given specified query string.
     * <p>
     * This implementation does not handle parameters
     *
     * @param jpqlQueryString
     *            a JPQL query string
     * @return a single result
     */
    public final List<?> selectMultiple(String jpqlQueryString) {
	EntityManager em = startSimple();
	List<?> result = em.createQuery(jpqlQueryString).getResultList();
	end(em);
	return result;
    }
}
