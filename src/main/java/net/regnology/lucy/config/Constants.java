package net.regnology.lucy.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "en";

    // Regex to split by link types and return license names and link type.
    // "Apache-2.0 OR MIT AND GPL-2.0" -> ["Apache-2.0", "OR", "MIT", "AND", "GPL-2.0"]
    public static final String licenseSplitRegex =
        "(?=/)|(?<=/)|(?=\\s\\+\\s)|(?<=\\s\\+\\s)|" +
        "(?=\\s[oO][rR]\\s)|(?<=\\s[oO][rR]\\s)|" +
        "(?=\\s[aA][nN][dD]\\s)|(?<=\\s[aA][nN][dD]\\s)";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String NO_URL = "No URL found";
    public static final String GITHUB_LIMIT = "Github request limit reached";
    public static final String NO_COPYRIGHT = "No copyright found";

    public static final String LICENSE_TEXT_SEPARATOR =
        "\n<span>===================================================================</span><br><br>\n";

    public static final String OSS_DEFAULT_PREFIX = "Default";
    public static final String OSS_PUBLISH_PREFIX = "Publish";
    public static final String OSS_REQUIREMENT_PREFIX = "Requirements";
    public static final String OSS_ZIP_PREFIX = "Licenses";

    /* MIME Types */
    public static final String MIME_CSV = "text/csv";
    public static final String MIME_HTML = "text/html";
    public static final String MIME_ZIP = "application/zip";

    /* Source Code */
    public static final String INDEX = "index.csv";

    public static final String FILE_SEPARATOR = "/";

    /* Statistics */
    public static final int MAX_SERIES_LIMIT = 20;

    /* Default license / risk */
    public static final String UNKNOWN = "Unknown";
    public static final String NON_LICENSED = "Non-Licensed";

    private Constants() {}
}
