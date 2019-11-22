package org.bbr.examples.service.business;

import java.util.Optional;

import org.bbr.examples.error.TransferRuntimeException;
import org.bbr.examples.ioc.Factory;
import org.bbr.examples.service.business.startegy.Operations;
import org.bbr.examples.service.event.Event;
import org.bbr.examples.service.system.LoggerAdaptor;
import org.bbr.examples.service.system.Task;

/**
 * Schedules incoming events and returns without blocking.
 */
public class TransferProcessor implements TransferService {

    private static LoggerAdaptor getLogger() {
        return Factory.of(TransferProcessor.class).getLogger();
    }

    private Operations operations;
    private EventsQue eventsQue;

    TransferProcessor() {}

    public static void visit(Factory factory) {
        factory.with(new TransferProcessor());
        factory.getTransferProcessor().init(factory);
    }

    private void init(Factory factory) {
        operations = factory.getOperationsSingleton();
        eventsQue = factory.getEventsQueSingleton();
        factory.getScheduler().setTask(this::observe);
    }

    @Override
    public void process(Event event) {
        eventsQue.push(event);
    }

    private Task worker(Event event) {
        try {
            getLogger().info("New request received: " + event.getClass().getName());
            return event.fire(operations);
        } catch (Exception e) {
            logTaskError();
            throw new TransferRuntimeException("Error while processing transfer request!", e);
        }
    }

    private void logTaskError() {
        getLogger().error("Error while processing transfer request!");
    }

    private Task observe() {
        return Optional.ofNullable(eventsQue.pop()).map(this::worker).orElse(null);
    }

}
