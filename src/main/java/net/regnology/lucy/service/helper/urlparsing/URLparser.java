package net.regnology.lucy.service.helper.urlparsing;

import java.io.IOException;
import org.json.simple.parser.ParseException;

public interface URLparser {
    void initCommands();

    String getURL(String type, String groupID, String artifactID, String version)
        throws IOException, ParseException, ClassCastException, InterruptedException;

    String getURL(String purl) throws IOException, ParseException, ClassCastException, InterruptedException;

    String githubURL(String url, String version) throws IOException, ParseException, InterruptedException;
}
