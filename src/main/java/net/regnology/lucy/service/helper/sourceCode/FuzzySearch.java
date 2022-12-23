package net.regnology.lucy.service.helper.sourceCode;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

public class FuzzySearch {

    private static final Logger log = LoggerFactory.getLogger(FuzzySearch.class);

    private static final float THRESHOLD = 0.4f;

    public static List<Pair<String, Float>> search(String libraryLabel, Stream<String> indexFile) {
        log.debug("Start Fuzzy Search..");

        return indexFile
            .map(line -> {
                String basename = line.split(";")[0].toLowerCase(Locale.ROOT);
                int longestString = Math.max(libraryLabel.length(), basename.length());
                int distance = new LevenshteinDistance().apply(libraryLabel, basename);
                float percent = 1.0f; // Helper stuff

                // Calculate similarity
                if (distance > 0) {
                    percent = (float) distance / longestString;
                }

                return Pair.of(line, percent);
            })
            .filter(similarity -> similarity.getSecond() <= THRESHOLD)
            .collect(Collectors.toList());
    }
}
