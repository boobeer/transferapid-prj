package org.bbr.examples.service.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerAdaptor {

    private Logger implementation;

    public LoggerAdaptor(Class<?> clazz) {
        this.implementation = LogManager.getLogger(clazz);
    }

    public void info(String message) {
        implementation.info(message);
    }

    public void error(String message) {
        implementation.error(message);
    }

    public void error(String message, Exception exception) {
        implementation.error(message, exception);
    }
}
