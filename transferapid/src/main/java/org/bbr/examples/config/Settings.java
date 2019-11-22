package org.bbr.examples.config;

import org.bbr.examples.error.TransferRuntimeException;
import org.bbr.examples.ioc.Factory;
import org.bbr.examples.service.system.LoggerAdaptor;
import org.bbr.examples.service.system.io.fs.FileStore;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class Settings {

    private static final String PROPERTIES_FILE = "application.properties";

    private LoggerAdaptor getLogger() {
        return Factory.of(Settings.class).getLogger();
    }

    private FileStore getFileStore() {
        return Factory.instance().getFileStoreSingleton();
    }

    private volatile Properties properties;

    private static class SettingsSingleton {
        private static final Settings instance = new Settings();
    }

    private Settings() {
    }

    public static Settings instance() {
        return SettingsSingleton.instance;
    }

    private Properties getProperties() {
        try {
            if (properties == null) {
                synchronized (this) {
                    if (properties == null) {
                        properties = new Properties();
                        try (InputStream input = getFileStore().input(PROPERTIES_FILE)) {
                            properties.load(input);
                        }
                    }
                }
            }
            return properties;
        } catch (IOException e) {
            getLogger().error("Error while reading settings!");
            throw new TransferRuntimeException("Error while reading settings!", e);
        }
    }

    public int getIntProperty(String name, int defaultValue) {
        return Optional.ofNullable(getProperties().get(name))
                .map(p -> Integer.valueOf((String) p)).orElse(defaultValue);
    }
}
