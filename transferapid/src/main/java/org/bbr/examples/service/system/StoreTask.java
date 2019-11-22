package org.bbr.examples.service.system;

import java.util.List;
import java.util.function.Consumer;

import org.bbr.examples.ioc.Factory;
import org.bbr.examples.service.system.io.db.Store;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Runs a series of actions in a transaction.
 * Provides those with a DAO and a current session.
 */
public class StoreTask implements Task {

    private static LoggerAdaptor getLogger() {
        return Factory.of(Scheduler.class).getLogger();
    }

    private Store getStore() {
        return Factory.instance().getStoreSingleton();
    }

    private Session getSession() {
        return getStore().getCurrentSession();
    }

    private final List<Consumer<Store>> actions;
    private Transaction transaction;

    public StoreTask(List<Consumer<Store>> actions) {
        this.actions = actions;
    }

    @Override
    public void run() {
        try {
            begin();
            actions.forEach(action -> action.accept(getStore()));
            commit();
        } catch (Exception e) {
            getLogger().error("Error while accessing DB", e);
            rollback();
        } finally {
            close();
        }
    }

    private void begin() {
        transaction = getSession().beginTransaction();
    }

    private void commit() {
        transaction.commit();
    }

    private void rollback() {
        if (transaction != null) {
            transaction.rollback();
        }
        getSession().clear();
    }

    private void close() {
        getStore().closeCurrentSession();
    }
}
