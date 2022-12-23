package net.regnology.lucy.service.helper.urlparsing;

import java.io.IOException;
import org.json.simple.parser.ParseException;

public interface ParserCommand {
    String parse(String groupID, String artifactID, String version) throws IOException, ParseException, InterruptedException;
}
