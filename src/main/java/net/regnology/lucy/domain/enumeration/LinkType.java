package net.regnology.lucy.domain.enumeration;

import java.util.*;

/**
 * The LinkType enumeration.
 */
public enum LinkType {
    AND("AND"),
    OR("OR");

    private final String value;

    private static final Set<String> orMapping = new HashSet<>(Arrays.asList("/", "+", "or"));
    private static final Set<String> andMapping = new HashSet<>(Collections.singletonList("and"));

    LinkType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Get a LinkType object by string.
     * Possible links (case insensitive):<br>
     * OR = "/", "+", "or"<br>
     * AND = "and"
     *
     * @param linkType linkType string.
     * @return a linkType object or null if the linkType value is unknown.
     */
    public static LinkType getLinkType(String linkType) {
        if (orMapping.contains(linkType.toLowerCase())) {
            return OR;
        } else if (andMapping.contains(linkType.toLowerCase())) {
            return AND;
        }

        return null;
    }
}
