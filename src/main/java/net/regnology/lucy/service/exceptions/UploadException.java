package net.regnology.lucy.service.exceptions;

public class UploadException extends Exception {

    private static final long serialVersionUID = 1L;
    private final String message;

    public UploadException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
