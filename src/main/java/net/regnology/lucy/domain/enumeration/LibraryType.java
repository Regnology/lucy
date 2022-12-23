package net.regnology.lucy.domain.enumeration;

/**
 * The LibraryType enumeration.
 */
public enum LibraryType {
    MAVEN("maven"),
    NPM("npm"),
    NUGET("nuget"),
    PYPI("pypi"),
    GOLANG("golang"),
    ALPINE("alpine"),
    APACHE("apache"),
    BITBUCKET("bitbucket"),
    CARGO("cargo"),
    COMPOSER("composer"),
    DEB("deb"),
    DOCKER("docker"),
    GEM("gem"),
    GENERIC("generic"),
    GITHUB("github"),
    GRADLE("gradle"),
    HEX("hex"),
    JAR("jar"),
    JAVA("java"),
    JS("js"),
    POM("pom"),
    RPM("rpm"),
    XSD("xsd"),
    ZIP("zip"),
    UNKNOWN("unknown");

    private final String value;

    LibraryType(String value) {
        this.value = value;
    }

    public static LibraryType getLibraryTypeByValue(String value) {
        for (LibraryType libraryType : LibraryType.values()) {
            if (libraryType.getValue().equalsIgnoreCase(value)) return libraryType;
        }
        return UNKNOWN;
    }

    public String getValue() {
        return value;
    }
}
