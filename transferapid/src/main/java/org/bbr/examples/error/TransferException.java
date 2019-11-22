package org.bbr.examples.error;

public class TransferException extends Exception {

    private static final long serialVersionUID = 1L;

    public TransferException(String message, Exception cause) {
        super(message, cause);
    }
}
