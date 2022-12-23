package net.regnology.lucy.service.exceptions;

public class LibraryIdDoesntExist extends LibraryException {

    public LibraryIdDoesntExist(Long id) {
        super("Can't find Library with the ID : " + id);
    }
}
