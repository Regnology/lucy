package net.regnology.lucy.service.exceptions;

import net.regnology.lucy.domain.Library;

public class LibraryAlreadyExistException extends LibraryException {

    public LibraryAlreadyExistException(String message) {
        super(message);
    }

    public LibraryAlreadyExistException(String message, Library library) {
        super(message, library);
    }
}
