package net.regnology.lucy.domain.enumeration;

/**
 * The UploadState enumeration.
 */
public enum UploadState {
    SUCCESSFUL("Successful"),
    PROCESSING("Processing"),
    FAILURE("Failure");

    private final String value;

    UploadState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
