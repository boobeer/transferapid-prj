package org.bbr.examples.service.system.io.fs;

import org.bbr.examples.error.TransferException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Provides operations on files.
 */
public class FileStore {

    private static class FileStoreSingleton {
        private static final FileStore instance = new FileStore();
    }

    private FileStore() {
    }

    public static FileStore instance() {
        return FileStoreSingleton.instance;
    }

    public InputStream input(String fileName) {
        return getClass().getClassLoader().getResourceAsStream(fileName);
    }

    String content(String fileName) throws TransferException {
        try {
            return String.join("", Files.readAllLines(Paths.get(resource(fileName))));
        } catch (IOException | URISyntaxException e) {
            throw new TransferException("File not found!", e);
        }
    }

    private URI resource(String name) throws URISyntaxException {
        return getClass().getClassLoader().getResource(name).toURI();
    }
}
