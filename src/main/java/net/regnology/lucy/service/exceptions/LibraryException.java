package net.regnology.lucy.service.exceptions;

import net.regnology.lucy.domain.Library;

public class LibraryException extends Exception {

    private static final long serialVersionUID = 1L;
    private Library library;

    public LibraryException(String message) {
        super(message);
    }

    public LibraryException(String message, Library library) {
        super(message);
        this.library = library;
    }

    public Library getLibrary() {
        return this.library;
    }
}
