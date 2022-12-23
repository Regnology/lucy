package net.regnology.lucy.domain.enumeration;

/**
 * The LogStatus enumeration.
 */
public enum LogStatus {
    CLOSED("closed"),
    FIXED("fixed"),
    OPEN("open");

    private final String value;

    LogStatus(String value) {
        this.value = value;
    }

    public static LogStatus getLogStatusByValue(String value) {
        for (LogStatus logStatus : LogStatus.values()) {
            if (logStatus.getValue().equalsIgnoreCase(value)) return logStatus;
        }
        return null;
    }

    public String getValue() {
        return value;
    }
}
