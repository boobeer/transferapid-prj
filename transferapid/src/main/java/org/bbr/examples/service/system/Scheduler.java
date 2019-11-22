package org.bbr.examples.service.system;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

import org.bbr.examples.config.Settings;
import org.bbr.examples.error.TransferRuntimeException;
import org.bbr.examples.ioc.Factory;

/**
 * A simple scheduler processing receiving events (may send those further to other services in a real system).
 */
public class Scheduler {

    public static final int OBSERVE_DELAY = 500;
    private final Timer timer = new Timer("Timer");

    private static LoggerAdaptor getLogger() {
        return Factory.of(Scheduler.class).getLogger();
    }

    private Settings getSettings() {
        return settings;
    }

    public int getObserveDelay() {
        return getSettings().getIntProperty("OBSERVE_DELAY", OBSERVE_DELAY);
    }

    private Settings settings;
    private TimerTask timerTask;
    private Supplier<Task> taskSupplier;

    Scheduler() {}

    public static void visit(Factory factory) {
        Scheduler scheduler = new Scheduler();
        factory.with(scheduler);
        scheduler.settings = factory.getSettingsSingleton();
    }

    synchronized public void setTask(Supplier<Task> taskSupplier) {
        if (taskSupplier == null) {
            throw new TransferRuntimeException("Scheduled job is null!");
        }
        try {
            stop();
            this.taskSupplier = taskSupplier;
            timerTask = buildTimerTask();
            start();
        } catch (Exception e) {
            getLogger().error("Error while restarting a scheduled task!" + e.getLocalizedMessage());
            throw new TransferRuntimeException("Error while restarting a scheduled task!");
        }
    }

    private TimerTask buildTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    tryProcessTask();
                } catch (Exception e) {
                    getLogger().error("Error while processing a request!" + e.getLocalizedMessage());
                    logTaskError();
                }
            }
        };
    }

    void tryProcessTask() {
        getLogger().info("Waiting for requests...");
        Task task = taskSupplier.get();
        if (task != null) {
            task.run();
        }
    }

    void logTaskError() {
        getLogger().error("Error while processing a request!");
    }

    synchronized private void start() {
        if (timerTask == null) {
            return;
        }
        timer.scheduleAtFixedRate(timerTask, getObserveDelay(), getObserveDelay());
    }

    synchronized boolean stop() {
        if (timerTask == null) {
            return false;
        }
        timerTask.cancel();
        timerTask = null;
        return true;
    }
}
