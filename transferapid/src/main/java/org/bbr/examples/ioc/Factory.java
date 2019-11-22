package org.bbr.examples.ioc;

import org.bbr.examples.config.Settings;
import org.bbr.examples.service.business.TransferProcessor;
import org.bbr.examples.service.business.EventsQue;
import org.bbr.examples.service.business.startegy.Operations;
import org.bbr.examples.service.controller.TransferController;
import org.bbr.examples.service.system.LoggerAdaptor;
import org.bbr.examples.service.system.Scheduler;
import org.bbr.examples.service.system.io.db.Store;
import org.bbr.examples.service.system.io.fs.FileStore;
import org.bbr.examples.service.system.web.EmbeddedServer;
import org.bbr.examples.utils.JsonProcessor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Some simple IOC implementation.
 */
public class Factory {

    private final Class<?> clazz;
    private TransferProcessor processor;
    private Scheduler scheduler;
    private FileStore fileStore;
    private Settings settings;
    private Store store;
    private EventsQue que;
    private Operations operations;
    private EmbeddedServer server;

    private Map<String,LoggerAdaptor> loggers = new ConcurrentHashMap<>();

    private Factory(Class<?> clazz) {
        this.clazz = clazz;
    }

    Factory() {
        this(null);
    }

    public static Factory of(Class<?> clazz) {
        return new Factory(clazz);
    }

    static void injectSpy(Factory spy) {
        FactoryInstance.injectedSpy = spy;
    }

    private static class FactoryInstance {
        private static final Factory instance = new Factory();
        private static Factory injectedSpy;
    }

    public static Factory instance() {
        return Optional
                .ofNullable(FactoryInstance.injectedSpy)
                .orElse(FactoryInstance.instance)
                .customInit();
    }

    public Factory customInit() {
        if (processor == null) {
            synchronized (this) {
                if (processor == null) {
                    Scheduler.visit(this);
                    TransferProcessor.visit(this);
                }
            }
        }
        return this;
    }

    public void with(TransferProcessor processor) {
        this.processor = processor;
    }

    public void with(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    public LoggerAdaptor getLogger() {
        return loggers.computeIfAbsent(clazz.getName(),(key) -> new LoggerAdaptor(clazz));
    }


    public FileStore getFileStoreSingleton() {
        return Optional.ofNullable(fileStore).orElseGet(() -> fileStore = FileStore.instance());
    }

    public Settings getSettingsSingleton() {
        return Optional.ofNullable(settings).orElseGet(() -> settings = Settings.instance());
    }

    public Store getStoreSingleton() {
        return Optional.ofNullable(store).orElseGet(() -> store = Store.instance());
    }

    public EventsQue getEventsQueSingleton() {
        return Optional.ofNullable(que).orElseGet(() -> que = EventsQue.instance());
    }

    public Operations getOperationsSingleton() {
        return Optional.ofNullable(operations).orElseGet(() -> operations = Operations.instance());
    }

    public EmbeddedServer getEmbeddedServerSingleton() {
        return Optional.ofNullable(server).orElseGet(() -> server = EmbeddedServer.instance());
    }

    public TransferProcessor getTransferProcessor() {
        return processor;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public TransferController getTransferControllerPrototype() {
        return new TransferController();
    }

    public JsonProcessor getJsonProcessorPrototype() {
        return new JsonProcessor();
    }

}
