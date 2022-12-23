package net.regnology.lucy.service.helper.copyright;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CopyrightAnalyser {

    /*
    Pattern to extract copyright:
    - Notice starts in a line with the word "Copyright". Special characters can be before like a '*' or '#'
    - The copyright notice can still be the generic placeholder. This copyrights must be ignored.
    - The word "Copyright" can also appear in different places in the license text,
      so only the lines beginning with "Copyright" are checked.
    - We have the text "All rights reserved." often in the same or in the next line. This must be removed or not extracted.
    - One copyright notice can be longer than one line
    - A license text can have multiple copyright notices
    - A copyright notice ends after a empty new line. Empty line can have special characters like '*' or '#'
     */

    // Redundant
    // private final static String COPYRIGHT_PATTERN_1 = "(?i)(Copyright(?:(ed|s))?\\s+" + COPY_SYM + "\\s+.?\\d{2,}.*)$";

    public static final String COPY_SYM = "(?:(\\(c\\)|\u00A9|&copy;))";
    public static final String COPYRIGHT_PATTERN_1 = "(?i)(Copyright(?:(ed|s))?\\s+" + COPY_SYM + "\\s+.*)$";
    public static final String COPYRIGHT_PATTERN_2 = "(?i)(Copyright(?:(ed|s))?:?\\s+\\d{2,}.*)$";
    public static final String COPYRIGHT_EXCLUDE_PATTERN = "(?:(notices?|holders?|statements?|owners?|<year>|yyyy|name of author))";

    public static final String ALL_RIGHTS_TEXT = "all rights? reserved?";
    public static final String END_TAGS = "\\s*</p>|\\s*<br ?/?>|\\s*</span>|\\s*</[Cc]opyright>";
    public static final String LICENSED_LINE =
        "(and )?(licen[cs]ed|released) (under|on the)|see (the )?(license|copyright)|" +
        "This program and the accompanying materials|This source code is licensed under|" +
        "Permission to use|This software is provided|Permission is hereby granted|" +
        "License:|Everyone is permitted to copy and distribute";
    public static final String EMPTY_LINE_PATTERN = "^(" + END_TAGS + "|\\W*|\\s*)$|" + LICENSED_LINE;

    public static final int COPYRIGHT_LENGTH_LIMIT = 512;

    /**
     * Extracts the copyright information from a line.
     * Tries different patterns to find the copyright. If a copyright was found it will be checked against the exclude
     * pattern to optimize the finding.
     *
     * @param line a string from which the copyright info will be extracted.
     * @return null if no copyright was found, otherwise the extracted copyright
     */
    private static String extractCopyrightFromLine(String line) {
        Matcher matcher1 = Pattern.compile(COPYRIGHT_PATTERN_1).matcher(line);
        Matcher matcher2 = Pattern.compile(COPYRIGHT_PATTERN_2).matcher(line);

        String finding = null;

        if (matcher1.find()) {
            finding = matcher1.group(1);
        } else if (matcher2.find()) {
            finding = matcher2.group(1);
        }

        if (finding != null) {
            // if the exclude pattern matches with the found copyright, then return null
            if (Pattern.compile(COPYRIGHT_EXCLUDE_PATTERN, Pattern.CASE_INSENSITIVE).matcher(finding).find()) return null;

            return finding;
        }

        return null;
    }

    /**
     * Parse the copyright information from a stream.
     * The stream can be lines from a file or a text.<br>
     * Every line will be analysed for a copyright. If a copyright goes over several lines
     * than it will be concatenated to one String. A copyright ends when a {@link #EMPTY_LINE_PATTERN} or a new copyright starts.
     * <br>
     * Multiple copyrights are saved in a Set of Strings.
     *
     * @param content a Stream with Strings
     * @return a Set of copyrights
     */
    private static Set<String> parseCopyright(Stream<String> content) {
        Set<String> copyrights = new HashSet<>(2);
        Pattern emptyLinePattern = Pattern.compile(EMPTY_LINE_PATTERN, Pattern.CASE_INSENSITIVE);

        // A copyright can go over several lines. Every line will be saved in an array till an empty line is reached.
        List<String> lastCopyright = new ArrayList<>(2);

        content.forEach(e -> {
            String copyright = CopyrightAnalyser.extractCopyrightFromLine(e);

            // Check if a copyright was found
            if (copyright != null) {
                // If the lastCopyright is filled then concatenate all Strings and add it to the Set of copyrights.
                // A new copyright starts
                if (lastCopyright.size() > 0) {
                    copyrights.add(cleanseCopyright(String.join(" ", lastCopyright)));
                    lastCopyright.clear();
                }

                // Remove tags from the copyright and add it to the lastCopyright list
                lastCopyright.add(Pattern.compile("(" + END_TAGS + ")$", Pattern.CASE_INSENSITIVE).matcher(copyright).replaceAll(""));
                // Check if the current line is a empty line and a copyright was already found
            } else if (lastCopyright.size() > 0 && emptyLinePattern.matcher(e).find()) {
                copyrights.add(cleanseCopyright(String.join(" ", lastCopyright)));
                lastCopyright.clear();
                // If the first and second conditions fails than add the line to the lastCopyright list if the size is bigger 0.
                // That means this line still depends to the previous extracted copyright
            } else if (lastCopyright.size() > 0) {
                // Remove tags from the copyright and add it to the lastCopyright list
                lastCopyright.add(
                    Pattern.compile("(" + END_TAGS + ")$", Pattern.CASE_INSENSITIVE).matcher(e).replaceAll("").replaceAll("^\\W+", "")
                );
            }
        });

        if (lastCopyright.size() > 0) copyrights.add(cleanseCopyright(String.join(" ", lastCopyright)));

        return copyrights;
    }

    private static String cleanseCopyright(String copyright) {
        Pattern cleansePattern = Pattern.compile("(" + ALL_RIGHTS_TEXT + "|" + LICENSED_LINE + "|\u0000).*", Pattern.CASE_INSENSITIVE);
        final String specialChars = "\\s'|^*,;:\\-_+/\"\\\\";

        // Remove everything after "All Rights Reserved" and remove special characters at the end
        return cleansePattern.matcher(copyright).replaceAll("").replaceAll("[" + specialChars + "]*$", "").trim();
    }

    /**
     * Extract the copyright from a file.
     *
     * @param file file to extract copyright. Cannot be null
     * @return a Set of copyrights. If no copyright was found, a emtpy Set is returned
     * @throws IOException if the file is not readable
     */
    public static Set<String> extractCopyright(File file) throws IOException {
        if (file == null) throw new IllegalArgumentException("File is null");

        try (Stream<String> stream = Files.lines(Paths.get(file.getPath()))) {
            return parseCopyright(stream);
        }
    }

    /**
     * Extract the copyright from a text.
     *
     * @param text text to extract copyright. Cannot be null
     * @return a Set of copyrights. If no copyright was found, a emtpy Set is returned
     */
    public static Set<String> extractCopyright(String text) {
        if (text == null) throw new IllegalArgumentException("String is null");

        return parseCopyright(text.lines());
    }
}
