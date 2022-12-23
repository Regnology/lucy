package net.regnology.lucy.domain.enumeration;

/**
 * Archive formats that can be used:
 * <ul>
 * <li>{@link #FULL}</li>
 * <li>{@link #DELIVERY}</li>
 * </ul>
 */
public enum ArchiveFormat {
    FULL("full"),
    DELIVERY("delivery");

    private final String value;

    ArchiveFormat(String value) {
        this.value = value;
    }

    public static ArchiveFormat getArchiveFormatByValue(String value) {
        for (ArchiveFormat archiveFormat : ArchiveFormat.values()) {
            if (archiveFormat.getValue().equalsIgnoreCase(value)) return archiveFormat;
        }
        return null;
    }

    public String getValue() {
        return value;
    }
}
