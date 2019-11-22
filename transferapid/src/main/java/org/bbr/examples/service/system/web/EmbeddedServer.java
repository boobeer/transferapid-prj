package org.bbr.examples.service.system.web;

import org.bbr.examples.config.Settings;
import org.bbr.examples.error.TransferException;
import org.bbr.examples.ioc.Factory;
import org.bbr.examples.service.system.LoggerAdaptor;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * Jetty implementation of an embedded server
 */
public class EmbeddedServer {

    static final String TRANSFER_API_URI = "/transfer-api";
    private static final int DEFAULT_PORT = 8181;

    private static LoggerAdaptor getLogger() {
        return Factory.of(EmbeddedServer.class).getLogger();
    }

    private Settings getSettings() {
        return Factory.instance().getSettingsSingleton();
    }

    private Server getServer() {
        if (server == null) {
            synchronized (this) {
                if (server == null) {
                    server = new Server();
                }
            }
        }
        return server;
    }

    private static class EmbeddedServerSingleton {
        private static final EmbeddedServer instance = new EmbeddedServer();
    }

    public static EmbeddedServer instance() {
        return EmbeddedServerSingleton.instance;
    }

    private Server server;


    public void start() throws TransferException {
        try {
            server = getServer();
            ServerConnector connector = new ServerConnector(server);
            connector.setPort(getSettings().getIntProperty("SERVER_PORT", DEFAULT_PORT));
            server.setConnectors(new Connector[]{connector});
            ServletHandler servletHandler = new ServletHandler();
            servletHandler.addServletWithMapping(TransferServlet.class, TRANSFER_API_URI);
            server.setHandler(servletHandler);
            server.start();
        } catch (Exception e) {
            getLogger().error("Error while starting server!", e);
            throw new TransferException("Error while starting server!", e);
        }
    }

    public void stop() throws TransferException {
        try {
            server = getServer();
            server.stop();
        } catch (Exception e) {
            getLogger().error("Error while stopping server!", e);
            throw new TransferException("Error while stopping server!", e);
        }
    }

}
