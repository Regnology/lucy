package net.regnology.lucy.service.upload;

import java.util.*;
import net.regnology.lucy.domain.Library;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BomHandler extends DefaultHandler {

    private final Logger log = LoggerFactory.getLogger(BomHandler.class);

    private final String[] referenceTypes = { "vcs", "website", "issue-tracker", "distribution" }; // Sorting by priority

    private final Set<Library> libraries = new HashSet<>(128);
    private final List<String> licenses = new ArrayList<>(2);
    private final Map<String, String> sourceCodeUrl = new HashMap<>(4);
    private final Map<String, String> hashAlg = new HashMap<>(2);

    private Library lastLibrary;
    private String lastHashAlg = "";
    private String lastReferenceType = "";
    private String purl = "";

    private boolean bComponent;
    private boolean bGroup;
    private boolean bName;
    private boolean bVersion;
    private boolean bLicenses;
    private boolean bLicenseName;
    private boolean bLicenseId;
    private boolean bLicenseUrl;
    private boolean bPurl;
    private boolean bHashes;
    private boolean bHash;
    private boolean bExternalReferences;
    private boolean bExternalReferencesReference;
    private boolean bExternalReferencesUrl;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        if (qName.equalsIgnoreCase("component")) {
            bComponent = true;
            lastLibrary = new Library();
        } else if (bComponent && qName.equalsIgnoreCase("group")) {
            bGroup = true;
        } else if (bComponent && !bLicenses && qName.equalsIgnoreCase("name")) {
            bName = true;
        } else if (bComponent && qName.equalsIgnoreCase("version")) {
            bVersion = true;
        } else if (bComponent && qName.equalsIgnoreCase("hashes")) {
            bHashes = true;
        } else if (bComponent && bHashes && qName.equalsIgnoreCase("hash")) {
            bHash = true;
            lastHashAlg = attributes.getValue("alg");
        } else if (bComponent && qName.equalsIgnoreCase("licenses")) {
            bLicenses = true;
        } else if (bComponent && bLicenses && qName.equalsIgnoreCase("name")) {
            bLicenseName = true;
        } else if (bComponent && bLicenses && qName.equalsIgnoreCase("id")) {
            bLicenseId = true;
        } else if (bComponent && bLicenses && qName.equalsIgnoreCase("url")) {
            bLicenseUrl = true;
        } else if (bComponent && qName.equalsIgnoreCase("purl")) {
            bPurl = true;
        } else if (bComponent && qName.equalsIgnoreCase("externalReferences")) {
            bExternalReferences = true;
        } else if (bComponent && bExternalReferences && qName.equalsIgnoreCase("reference")) {
            bExternalReferencesReference = true;
            lastReferenceType = attributes.getValue("type");
        } else if (bComponent && bExternalReferences && qName.equalsIgnoreCase("url")) {
            bExternalReferencesUrl = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if (qName.equalsIgnoreCase("component")) {
            bComponent = false;

            lastLibrary.setMd5(hashAlg.get("MD5"));
            lastLibrary.setSha1(hashAlg.get("SHA-1"));

            lastLibrary.setOriginalLicense(String.join(" / ", licenses));

            if (sourceCodeUrl.size() == 1) {
                Map.Entry<String, String> entry = sourceCodeUrl.entrySet().iterator().next();
                String value = entry.getValue();
                lastLibrary.setSourceCodeUrl(value);
            } else {
                for (String type : referenceTypes) {
                    if (sourceCodeUrl.containsKey(type)) {
                        lastLibrary.setSourceCodeUrl(sourceCodeUrl.get(type));
                        break;
                    }
                }
            }

            libraries.add(lastLibrary);
            licenses.clear();
            hashAlg.clear();
            sourceCodeUrl.clear();
        } else if (bComponent && qName.equalsIgnoreCase("group")) {
            bGroup = false;
        } else if (bComponent && !bLicenses && qName.equalsIgnoreCase("name")) {
            bName = false;
        } else if (bComponent && qName.equalsIgnoreCase("version")) {
            bVersion = false;
        } else if (bComponent && qName.equalsIgnoreCase("hashes")) {
            bHashes = false;
        } else if (bComponent && bHashes && qName.equalsIgnoreCase("hash")) {
            bHash = false;
            lastHashAlg = "";
        } else if (bComponent && qName.equalsIgnoreCase("licenses")) {
            bLicenses = false;
        } else if (bComponent && bLicenses && qName.equalsIgnoreCase("name")) {
            bLicenseName = false;
        } else if (bComponent && bLicenses && qName.equalsIgnoreCase("id")) {
            bLicenseId = false;
        } else if (bComponent && bLicenses && qName.equalsIgnoreCase("url")) {
            bLicenseUrl = false;
        } else if (bComponent && qName.equalsIgnoreCase("purl")) {
            lastLibrary.setpUrl(purl);
            lastLibrary.setType(lastLibrary.extractTypeFromPUrl(purl));

            purl = "";
            bPurl = false;
        } else if (bComponent && qName.equalsIgnoreCase("externalReferences")) {
            bExternalReferences = false;
        } else if (bComponent && bExternalReferences && qName.equalsIgnoreCase("reference")) {
            bExternalReferencesReference = false;
            lastReferenceType = "";
        } else if (bComponent && bExternalReferences && qName.equalsIgnoreCase("url")) {
            bExternalReferencesUrl = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        String content = new String(ch, start, length);

        if (bGroup) {
            lastLibrary.setGroupId(content);
        } else if (bName) {
            lastLibrary.setArtifactId(content);
        } else if (bVersion) {
            lastLibrary.setVersion(content);
        } else if (bHash) {
            hashAlg.put(lastHashAlg, content);
        } else if (bLicenseName) {
            licenses.add(content);
        } else if (bLicenseId) {
            licenses.add(content);
        } else if (bLicenseUrl) {
            // Disabled because this license url is not very accurate
            // lastLibrary.setLicenseUrl(content);
        } else if (bPurl) {
            // Concatenate content because SAX parser splits the content on &amp ("&" sign)
            purl += content;
        } else if (bExternalReferencesUrl) {
            // Disabled because this source code url is not very accurate (another field like repository would fit better)
            // content = content.replaceFirst("^git\\+", "").replaceFirst("\\.git$", "");
            // sourceCodeUrl.put(lastReferenceType, content);
        }
    }

    public Set<Library> getLibraries() {
        return libraries;
    }
}
