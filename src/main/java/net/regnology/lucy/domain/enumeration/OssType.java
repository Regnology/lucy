package net.regnology.lucy.domain.enumeration;

/**
 * OSS content types that can be used:
 * <ul>
 * <li>{@link #DEFAULT}</li>
 * <li>{@link #PUBLISH}</li>
 * <li>{@link #REQUIREMENT}</li>
 * </ul>
 */
public enum OssType {
    DEFAULT("default"),
    PUBLISH("publish"),
    REQUIREMENT("requirement");

    private final String value;

    OssType(String value) {
        this.value = value;
    }

    public static OssType getOssTypeByValue(String value) {
        for (OssType exportFormat : OssType.values()) {
            if (exportFormat.getValue().equalsIgnoreCase(value)) return exportFormat;
        }
        return null;
    }

    public String getValue() {
        return value;
    }
}
