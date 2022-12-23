package net.regnology.lucy.domain.enumeration;

/**
 * The EntityUploadChoice enumeration.
 */
public enum EntityUploadChoice {
    PRODUCT("Product"),
    LIBRARY("Library"),
    LICENSE("License");

    private final String value;

    EntityUploadChoice(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
