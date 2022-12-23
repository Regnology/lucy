package net.regnology.lucy.service.upload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.regnology.lucy.domain.File;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.service.exceptions.UploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class BomLoader implements AssetLoader<Library> {

    private final Logger log = LoggerFactory.getLogger(BomLoader.class);

    @Override
    public Set<Library> load(File file) throws UploadException {
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {
            /* Secure against XXE */
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            SAXParser parser = factory.newSAXParser();
            BomHandler handler = new BomHandler();
            parser.parse(new ByteArrayInputStream(file.getFile()), handler);

            return handler.getLibraries();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            log.error("Error while parsing XML file : {}", e.getMessage());
            throw new UploadException("XML file can't be parsed");
        }
    }
}
