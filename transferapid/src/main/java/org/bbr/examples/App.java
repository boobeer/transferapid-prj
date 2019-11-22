package org.bbr.examples;

import org.bbr.examples.ioc.Factory;
import org.bbr.examples.service.system.LoggerAdaptor;
import org.bbr.examples.service.system.web.EmbeddedServer;

public class App {

    private static LoggerAdaptor logger;

    private static LoggerAdaptor getLogger() {
        return Factory.of(App.class).getLogger();
    }

    private static EmbeddedServer getServer() {
        return Factory.instance().getEmbeddedServerSingleton();
    }

    public static void start() {
        try {
            logger = getLogger();
            logger.info("Starting");
            EmbeddedServer server = getServer();
            server.start();
            logger.info("Started");
        } catch (Exception e) {
            if (logger != null) {
                logger.error("Error...", e);
            } else {
                System.err.println("Error..." + e.getLocalizedMessage());
            }
            stop();
        }
    }

    public static void stop() {
        try {
            logger.info("Stopping");
            EmbeddedServer server = getServer();
            if (server != null) {
                server.stop();
            }
            logger.info("Stopped");
        } catch (Exception e) {
            if (logger != null) {
                logger.error("Error...", e);
            } else {
                System.err.println("Error..." + e.getLocalizedMessage());
            }
        }
    }

    public static void main(String[] args) {
        start();
    }
}
