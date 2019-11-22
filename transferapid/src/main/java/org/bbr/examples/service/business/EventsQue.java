package org.bbr.examples.service.business;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.bbr.examples.config.Settings;
import org.bbr.examples.error.TransferRuntimeException;
import org.bbr.examples.ioc.Factory;
import org.bbr.examples.service.event.Event;

/**
 * A que of events to be processed (sent further to other services in a real system for example)
 */
public class EventsQue {

    private static final int DEFAULT_TIMEOUT = 1000;
    static final int DEFAULT_LIMIT = 100;
    private int limit;
    private BlockingDeque<Event> que;

    private static class TransferQueSingleton {
        private static final EventsQue instance = new EventsQue();
    }

    EventsQue() {}

    public static EventsQue instance() {
        return TransferQueSingleton.instance;
    }

    private BlockingDeque<Event> getQue() {
        buildQue();
        return que;
    }

    private void buildQue() {
        if (que == null) {
            synchronized (this) {
                if (que == null) {
                    limit = getSettings().getIntProperty("REQUESTS_LIMIT", DEFAULT_LIMIT);
                    que = new LinkedBlockingDeque<>(limit);
                }
            }
        }
    }

    private Settings getSettings() {
        return Factory.instance().getSettingsSingleton();
    }

    private int getPushTimeout() {
        return getSettings().getIntProperty("PUSH_TIMEOUT", DEFAULT_TIMEOUT);
    }


    void push(Event event) {
        if (getQue().size() >= limit) {
            throw new TransferRuntimeException("Too many requests!");
        }
        try {
            getQue().offer(event, getPushTimeout(), TimeUnit.MILLISECONDS);
        } catch (NullPointerException | IllegalArgumentException | InterruptedException e) {
            throw new TransferRuntimeException("Error while queuing a request!", e);
        }
    }

    Event pop() {
        try {
            if(getQue().size() == 0) {
                return null;
            }
            return getQue().poll(getPushTimeout(), TimeUnit.MILLISECONDS);
        } catch (NullPointerException | IllegalArgumentException | InterruptedException e) {
            throw new TransferRuntimeException("Error while processing a request!", e);
        }
    }

}
