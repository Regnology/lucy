package net.regnology.lucy.service.exceptions;

public class InvalidEmailException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidEmailException(String msg) {
        super(msg);
    }
}
