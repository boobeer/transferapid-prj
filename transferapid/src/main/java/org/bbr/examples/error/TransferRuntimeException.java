package org.bbr.examples.error;

public class TransferRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TransferRuntimeException(String message) {
        super(message);
    }

    public TransferRuntimeException(String message, Exception cause) {
        super(message, cause);
    }
}
