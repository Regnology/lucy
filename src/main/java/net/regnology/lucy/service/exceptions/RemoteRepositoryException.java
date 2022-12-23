package net.regnology.lucy.service.exceptions;

public class RemoteRepositoryException extends Exception {

    private static final long serialVersionUID = 1L;
    private final String message;

    public RemoteRepositoryException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
