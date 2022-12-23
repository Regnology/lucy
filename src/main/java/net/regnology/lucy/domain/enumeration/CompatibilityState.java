package net.regnology.lucy.domain.enumeration;

/**
 * The CompatibilityState enumeration.
 */
public enum CompatibilityState {
    Compatible("Compatible"),
    Incompatible("Incompatible"),
    Unknown("Unknown");

    private final String value;

    CompatibilityState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
