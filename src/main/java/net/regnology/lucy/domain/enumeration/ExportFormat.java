package net.regnology.lucy.domain.enumeration;

/**
 * Export formats that can be used:
 * <ul>
 * <li>{@link #JSON}</li>
 * <li>{@link #CSV}</li>
 * <li>{@link #HTML}</li>
 * </ul>
 */
public enum ExportFormat {
    JSON("json"),
    CSV("csv"),
    HTML("html");

    private final String value;

    ExportFormat(String value) {
        this.value = value;
    }

    public static ExportFormat getExportFormatByValue(String value) {
        for (ExportFormat exportFormat : ExportFormat.values()) {
            if (exportFormat.getValue().equalsIgnoreCase(value)) return exportFormat;
        }
        return null;
    }

    public String getValue() {
        return value;
    }
}
