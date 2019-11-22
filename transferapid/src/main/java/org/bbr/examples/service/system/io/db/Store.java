package org.bbr.examples.service.system.io.db;

import java.util.List;

import org.bbr.examples.error.TransferRuntimeException;
import org.bbr.examples.ioc.Factory;
import org.bbr.examples.service.system.LoggerAdaptor;
import org.bbr.examples.service.system.io.db.dto.Entry;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * A simple DAO object with some basic operations on Database and a session per thread.
 */
public class Store {

    private static LoggerAdaptor getLogger() {
        return Factory.of(Store.class).getLogger();
    }

    private ThreadLocal<Session> currentSession = new ThreadLocal<>();
    private volatile SessionFactory sessionFactory;

    private static class StoreSingleton {
        private static final Store instance = new Store();
    }

    private Store() {
    }

    public static Store instance() {
        return StoreSingleton.instance;
    }

    private void buildSessionFactory() {
        if (sessionFactory == null) {
            synchronized (this) {
                if (sessionFactory == null) {
                    sessionFactory = new Configuration().configure()
                            .buildSessionFactory();
                }
            }
        }
    }

    private SessionFactory getSessionFactory() {
        buildSessionFactory();
        return sessionFactory;
    }

    public Session getCurrentSession() {
        if (currentSession.get() == null) {
            currentSession.set(getSessionFactory().openSession());
        }
        return currentSession.get();
    }

    public void closeCurrentSession() {
        try (Session session = getCurrentSession()) {
            if (session != null) {
                currentSession.set(null);
            }
        }
    }

    public void save(Entry entry) {
        try {
            getCurrentSession().persist(entry);
        } catch (Exception e) {
            getLogger().error("Error while saving a row!");
            throw new TransferRuntimeException("Error while saving a row!", e);
        }
    }

    public <T extends Entry> List<T> read(String query, Entry entry) {
        try {
            return getCurrentSession().createQuery(query).setProperties(entry).list();
        } catch (Exception e) {
            getLogger().error("Error while reading a row!");
            throw new TransferRuntimeException("Error while saving a row!", e);
        }
    }

    public void delete(Entry entry) {
        try {
            getCurrentSession().delete(entry);
        } catch (Exception e) {
            getLogger().error("Error while saving a row!");
            throw new TransferRuntimeException("Error while saving a row!", e);
        }
    }
}
