package net.regnology.lucy.service.exceptions;

public class LibraryWithoutIdException extends LibraryException {

    public LibraryWithoutIdException() {
        super("Can't update a Library without ID");
    }
}
