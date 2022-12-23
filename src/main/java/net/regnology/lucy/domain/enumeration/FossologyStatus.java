package net.regnology.lucy.domain.enumeration;

/**
 * The FossologyStatus enumeration.
 */
public enum FossologyStatus {
    NOT_STARTED("Not started"),
    UPLOAD_STARTED("Started upload"),
    UPLOAD_FINISHED("Finished upload"),
    SCAN_STARTED("Started scan"),
    SCAN_FINISHED("Finished scan"),
    FAILURE("Failure");

    private final String value;

    FossologyStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
