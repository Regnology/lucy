package net.regnology.lucy.service.helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.domain.License;
import net.regnology.lucy.domain.Product;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

/**
 * Helper class to create a license ZIP package per product.
 */
public class LicenseZipHelper {

    private final Logger log = LoggerFactory.getLogger(LicenseZipHelper.class);

    private static final String FILES_DIR = "Files/";
    private static final String LICENSE_TEXT_DIR = "License_Texts/";
    private static final String COPYRIGHT_DIR = "Copyrights/";
    private static final String DEFAULT_CSS_TEMPLATE = "classpath:templates/licenseZip/style.css";
    private static final String DEFAULT_HTML_TEMPLATE = "classpath:templates/licenseZip/overview.html";
    private static final String LICENSE_TEXT_NAME_DELIMITER = "_";
    private static final String DEFAULT_LICENSE_CSS_TEMPLATE = "classpath:templates/licenseZip/license.css";

    private final File cssTemplate;
    private final File cssLicenseTemplate;
    private final File htmlTemplate;
    private final Product product;
    private final Map<String, List<Library>> libraries;

    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private final ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

    private String htmlOverview;
    private String tableHeader = "";
    private String disclaimer = "";
    private boolean withGroupId = false;

    public LicenseZipHelper(Product product, List<Library> libraries) throws FileNotFoundException {
        this.product = product;
        long numberOfLibrariesWithGroupId = libraries.stream().filter(library -> !library.getGroupId().isEmpty()).count();
        if (numberOfLibrariesWithGroupId > 0) this.withGroupId = true;
        this.libraries = groupLibraryDuplicates(libraries);
        this.htmlTemplate = ResourceUtils.getFile(DEFAULT_HTML_TEMPLATE);
        this.cssTemplate = ResourceUtils.getFile(DEFAULT_CSS_TEMPLATE);
        this.cssLicenseTemplate = ResourceUtils.getFile(DEFAULT_LICENSE_CSS_TEMPLATE);
    }

    public LicenseZipHelper(Product product, List<Library> libraries, String htmlTemplate, String cssTemplate)
        throws FileNotFoundException {
        this.product = product;
        long numberOfLibrariesWithGroupId = libraries.stream().filter(library -> !library.getGroupId().isEmpty()).count();
        if (numberOfLibrariesWithGroupId > 0) this.withGroupId = true;
        this.libraries = groupLibraryDuplicates(libraries);
        this.htmlTemplate = ResourceUtils.getFile(htmlTemplate);
        this.cssTemplate = ResourceUtils.getFile(cssTemplate);
        this.cssLicenseTemplate = ResourceUtils.getFile(DEFAULT_LICENSE_CSS_TEMPLATE);
    }

    private Map<String, List<Library>> groupLibraryDuplicates(List<Library> libraries) {
        Map<String, List<Library>> libraryMap = new LinkedHashMap<>(128);

        // TODO add library.type to libraryName to have a unique key name
        libraries.forEach(library -> {
            String libraryName = !library.getGroupId().isEmpty()
                ? library.getGroupId() + LICENSE_TEXT_NAME_DELIMITER + library.getArtifactId()
                : library.getArtifactId();

            libraryName = libraryName.replaceAll(" ", "_");

            if (libraryMap.containsKey(libraryName)) {
                libraryMap.get(libraryName).add(library);
            } else {
                List<Library> newLibraryList = new ArrayList<>();
                newLibraryList.add(library);
                libraryMap.put(libraryName, newLibraryList);
            }
        });

        return libraryMap;
    }

    public void addLicenseTexts() {
        addDirectoryToZip(LICENSE_TEXT_DIR);
        addDirectoryToZip(FILES_DIR);
        try {
            addEntryToZip("license", "css", Files.readAllBytes(cssLicenseTemplate.toPath()), FILES_DIR);
        } catch (IOException e) {
            log.error("Error while reading LicenseText CSS file : {}", e.getMessage());
        }

        libraries.forEach((key, value) -> {
            int counter = 1;

            for (Library library : value) {
                String baseName = key + LICENSE_TEXT_NAME_DELIMITER + counter;
                if (library.getLicenseText() != null && !library.getLicenseText().isEmpty()) {
                    addEntryToZip(
                        baseName,
                        "html",
                        addStylesheetToText(library.getLicenseText()).getBytes(StandardCharsets.UTF_8),
                        LICENSE_TEXT_DIR
                    );
                } else {
                    for (License license : library.getLicenseToPublishes()) {
                        if (license.getGenericLicenseText() != null && !license.getGenericLicenseText().isEmpty()) {
                            addEntryToZip(
                                baseName + LICENSE_TEXT_NAME_DELIMITER + license.getShortIdentifier(),
                                "html",
                                addStylesheetToText(license.getGenericLicenseText()).getBytes(StandardCharsets.UTF_8),
                                LICENSE_TEXT_DIR
                            );
                        }
                    }
                }

                counter++;
            }
        });
    }

    public void addCopyrights() {
        final String emptyCopyrightText = "The authors";
        final String style = "style=\"white-space: pre-line;\"";

        addDirectoryToZip(COPYRIGHT_DIR);
        //addDirectoryToZip(FILES_DIR);

        libraries.forEach((key, value) -> {
            int counter = 1;

            for (Library library : value) {
                String baseName = key + LICENSE_TEXT_NAME_DELIMITER + counter;
                if (!StringUtils.isBlank(library.getCopyright()) && !library.getCopyright().equalsIgnoreCase("No Copyright found")) {
                    addEntryToZip(
                        baseName,
                        "html",
                        addStylesheetToText(wrapIntoDiv(library.getCopyright(), style)).getBytes(StandardCharsets.UTF_8),
                        COPYRIGHT_DIR
                    );
                } else {
                    addEntryToZip(
                        baseName,
                        "html",
                        addStylesheetToText(emptyCopyrightText).getBytes(StandardCharsets.UTF_8),
                        COPYRIGHT_DIR
                    );
                }

                counter++;
            }
        });
    }

    /**
     * Wrap a text in a div tag.
     *
     * @param text Text to be wrapped
     * @param tagStyle Add specific style to the tag
     * @return The wrapped text
     */
    private String wrapIntoDiv(String text, String tagStyle) {
        return "<div " + tagStyle + ">" + StringEscapeUtils.escapeHtml4(text.strip()) + "</div>";
    }

    private String addStylesheetToText(String text) {
        Document document = Jsoup.parse(text);
        Document.OutputSettings outputSettings = new Document.OutputSettings();
        outputSettings.prettyPrint(false);
        document.outputSettings(outputSettings);
        Element head = document.head();
        head.append("<link rel=\"stylesheet\" href=\"../Files/license.css\">");
        return document.html();
    }

    public void addDirectoryToZip(String dirName) {
        try {
            zipOutputStream.putNextEntry(new ZipEntry(dirName));
        } catch (IOException e) {
            log.error("Error while adding \"{}\" to ZIP : {}", dirName, e.getMessage());
        }
    }

    /**
     * Add new entry to ZIP.
     * "/", "\" and "space" are replaced by "_".
     *
     * @param fileName      the file name for the new ZIP entry
     * @param fileExtension the file extension for the file name
     * @param data          the file data to add to ZIP
     * @param toSubDir      the path to a subdirectory
     */
    private void addEntryToZip(String fileName, String fileExtension, byte[] data, String toSubDir) {
        fileExtension = fileExtension.startsWith(".") ? fileExtension : "." + fileExtension;
        fileName = fileName.replace("/", "_").replace("\\", "_").replaceAll(" ", "_");

        toSubDir = toSubDir != null ? toSubDir : "";

        ZipEntry zipEntry = new ZipEntry(toSubDir + fileName + fileExtension);
        zipEntry.setSize(data.length);

        try {
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(data);
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            log.error("Error while adding \"{}.{}\" to the ZIP : {}", fileName, fileExtension, e.getMessage());
        }
    }

    public void addHtmlOverview(String htmlFileName, String cssFileName) {
        htmlFileName = htmlFileName.replace("/", "_").replaceAll(" ", "_");

        try {
            htmlOverview = new String(Files.readAllBytes(htmlTemplate.toPath()));
        } catch (IOException e) {
            log.error("Error while reading HTML template : {}", e.getMessage());
        }

        htmlOverview = htmlOverview.replace("%{disclaimer}%", disclaimer);
        htmlOverview = htmlOverview.replace("%{product}%", product.getName() + " " + product.getVersion());
        htmlOverview =
            htmlOverview.replace(
                "%{date}%",
                LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).localizedBy(Locale.ENGLISH))
            );
        htmlOverview = addTableContent(createTableContent());
        htmlOverview = htmlOverview.replace("%{tableHeader}%", tableHeader);

        addEntryToZip(htmlFileName, "html", htmlOverview.getBytes(StandardCharsets.UTF_8), null);
        try {
            addEntryToZip(cssFileName, "css", Files.readAllBytes(cssTemplate.toPath()), FILES_DIR);
        } catch (IOException e) {
            log.error("Error while reading CSS template : {}", e.getMessage());
        }
    }

    public void addDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer == null ? "" : disclaimer;
    }

    private String addTableContent(String content) {
        return htmlOverview.replace("%{tableContent}%", content);
    }

    // TODO remove license link duplicates
    public String createTableContent() {
        StringBuilder tableContent = new StringBuilder();

        libraries.forEach((key, value) -> {
            int counter = 1;

            String groupId = value.get(0).getGroupId();
            String artifactId = value.get(0).getArtifactId();
            List<String> licenses = new ArrayList<>(4);
            Map<String, List<String>> licenseTextLink = new LinkedHashMap<>(4);
            StringBuilder sourceCodeLink = new StringBuilder();
            StringBuilder copyrightLink = new StringBuilder();

            for (Library library : value) {
                String baseName = key + LICENSE_TEXT_NAME_DELIMITER + counter;

                String currentlicenses = library
                    .getLicenseToPublishes()
                    .stream()
                    .map(License::getShortIdentifier)
                    .collect(Collectors.joining(" AND "));
                if (!licenses.contains(currentlicenses)) {
                    licenses.add(" <span class=\"enumeration\">" + counter + "</span> ");
                    licenses.add(currentlicenses);
                }

                if (value.size() > 1) {
                    sourceCodeLink.append("<span class=\"enumeration\">").append(counter).append("</span> ");
                    copyrightLink.append("<span class=\"enumeration\">").append(counter).append("</span> ");
                }
                String sourceCodeUrl = library.getSourceCodeUrl() != null && !library.getSourceCodeUrl().isEmpty()
                    ? library.getSourceCodeUrl()
                    : "https://www.google.com/search?q=" + library.getArtifactId();
                sourceCodeLink.append("<a href=\"").append(sourceCodeUrl).append("\">Source</a>");

                copyrightLink.append("<a href=\"" + COPYRIGHT_DIR + baseName + ".html" + "\">Copyright</a>");

                if (library.getLicenseText() != null && !library.getLicenseText().isEmpty()) {
                    licenseTextLink.put(
                        library.getVersion(),
                        Arrays.asList("<a href=\"" + LICENSE_TEXT_DIR + baseName + ".html" + "\">License</a>")
                    );
                } else {
                    for (License license : library.getLicenseToPublishes()) {
                        if (license.getGenericLicenseText() != null && !license.getGenericLicenseText().isEmpty()) {
                            String link =
                                "<a href=\"" +
                                LICENSE_TEXT_DIR +
                                baseName +
                                LICENSE_TEXT_NAME_DELIMITER +
                                license.getShortIdentifier().replaceAll(" ", "_") +
                                ".html\">License</a>";
                            if (licenseTextLink.size() > 1) {
                                Map.Entry<String, List<String>> entry = licenseTextLink.entrySet().iterator().next();

                                if (library.getLicenseToPublishes().size() == 1 && entry.getValue().contains(link)) {
                                    break;
                                }

                                if (licenseTextLink.containsKey(library.getVersion())) {
                                    licenseTextLink.get(library.getVersion()).add(link);
                                } else {
                                    List<String> links = new ArrayList<>();
                                    links.add(link);
                                    licenseTextLink.put(library.getVersion(), links);
                                }
                            } else {
                                if (licenseTextLink.containsKey(library.getVersion())) {
                                    licenseTextLink.get(library.getVersion()).add(link);
                                } else {
                                    List<String> links = new ArrayList<>();
                                    links.add(link);
                                    licenseTextLink.put(library.getVersion(), links);
                                }
                            }
                        }
                    }
                }

                sourceCodeLink.append(" ");
                copyrightLink.append(" ");
                counter++;
            }

            if (licenses.size() < 3) licenses.remove(0);

            addTableHeader(withGroupId);

            int linkCounter = 1;
            String finalLink = "";

            if (licenseTextLink.size() > 1) {
                for (Map.Entry<String, List<String>> entry : licenseTextLink.entrySet()) {
                    String enumeration = " <span class=\"enumeration\">" + linkCounter + "</span> ";
                    finalLink += enumeration + String.join(", ", entry.getValue());
                    linkCounter++;
                }
            } else if (licenseTextLink.size() == 1) {
                Map.Entry<String, List<String>> entry = licenseTextLink.entrySet().iterator().next();
                finalLink = String.join(", ", entry.getValue());
            }

            tableContent.append(
                addTableRow(
                    groupId,
                    artifactId,
                    String.join("", licenses),
                    finalLink,
                    sourceCodeLink.toString(),
                    copyrightLink.toString(),
                    withGroupId
                )
            );
        });

        return tableContent.toString();
    }

    private void addTableHeader(boolean withGroupId) {
        if (withGroupId) {
            this.tableHeader =
                "<tr>\n" +
                "<th style=\"width: 20%\">GroupId</th>\n" +
                "<th>ArtifactId</th>\n" +
                "<th>License</th>\n" +
                "<th>License Text</th>\n" +
                "<th>Copyright</th>\n" +
                //"<th>Source Code</th>\n" +
                "</tr>";
        } else {
            this.tableHeader =
                "<tr>\n" +
                "<th>ArtifactId</th>\n" +
                "<th>License</th>\n" +
                "<th>License Text</th>\n" +
                "<th>Copyright</th>\n" +
                //"<th>Source Code</th>\n" +
                "</tr>";
        }
    }

    private String addTableRow(
        String groupId,
        String artifactId,
        String licenses,
        String licenseText,
        String sourceCodeUrl,
        String copyright,
        boolean withGroupId
    ) {
        String template;

        if (withGroupId) {
            template =
                "<tr>\n" +
                "<td>%{groupId}%</td>\n" +
                "<td>%{artifactId}%</td>\n" +
                "<td>%{licenses}%</td>\n" +
                "<td>%{licenseText}%</td>\n" +
                "<td>%{copyright}%</td>\n" +
                //"<td>%{sourceCodeUrl}%</td>\n" +
                "</tr>";
        } else {
            template =
                "<tr>\n" +
                "<td>%{artifactId}%</td>\n" +
                "<td>%{licenses}%</td>\n" +
                "<td>%{licenseText}%</td>\n" +
                "<td>%{copyright}%</td>\n" +
                //"<td>%{sourceCodeUrl}%</td>\n" +
                "</tr>";
        }

        return template
            .replace("%{groupId}%", groupId)
            .replace("%{artifactId}%", artifactId)
            .replace("%{licenses}%", licenses)
            .replace("%{licenseText}%", licenseText)
            .replace("%{copyright}%", copyright)
            .replace("%{sourceCodeUrl}%", sourceCodeUrl);
    }

    public byte[] getLicenseZip() {
        try {
            zipOutputStream.close();
        } catch (IOException e) {
            log.error("Error while saving license ZIP : {}", e.getMessage());
        }

        return byteArrayOutputStream.toByteArray();
    }
}
