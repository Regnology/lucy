package net.regnology.lucy.service.exceptions;

import net.regnology.lucy.domain.License;

public class LicenseAlreadyExistException extends LicenseException {

    private License license = null;

    public LicenseAlreadyExistException(String message) {
        super(message);
    }

    public LicenseAlreadyExistException(String message, License license) {
        super(message);
        this.license = license;
    }

    public License getLicense() {
        return this.license;
    }
}
