package net.regnology.lucy.service.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import net.regnology.lucy.config.Constants;
import net.regnology.lucy.domain.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

public class OssListHelper {

    static class RiskColor {

        static final String PERMISSIVE = "#DAF7A688";
        static final String LIMITED_COPYLEFT = "#FFC30066";
        static final String STRONG_COPYLEFT = "#FF573366";
        static final String COMMERCIAL = "#20a8d866";
        static final String FORBIDDEN = "#c7000066";
        static final String PROPRIETARY_FREE = "#9933ff66";
        static final String UNKNOWN = "#eeeeee88";

        static String getColorByRiskName(String risk) {
            switch (risk) {
                case "Permissive":
                    return PERMISSIVE;
                case "Limited Copyleft":
                    return LIMITED_COPYLEFT;
                case "Strong Copyleft":
                    return STRONG_COPYLEFT;
                case "Commercial":
                    return COMMERCIAL;
                case "Forbidden":
                    return FORBIDDEN;
                case "Proprietary Free":
                    return PROPRIETARY_FREE;
                default:
                    return UNKNOWN;
            }
        }
    }

    private final Logger log = LoggerFactory.getLogger(OssListHelper.class);

    private static final String DEFAULT_HTML_TEMPLATE = "classpath:templates/ossList/default.html";
    private static final String NAME_DELIMITER = "_";

    private final Map<String, List<Library>> libraries;

    private File htmlTemplate;
    private Product product;
    private String html;
    private String tableHeader = "";
    private boolean withGroupId = false;

    public OssListHelper(List<Library> libraries) {
        long numberOfLibrariesWithGroupId = libraries.stream().filter(library -> !library.getGroupId().isEmpty()).count();
        if (numberOfLibrariesWithGroupId > 0) this.withGroupId = true;
        this.libraries = groupLibraryDuplicates(libraries);
    }

    public OssListHelper(Product product, List<Library> libraries, boolean distinct) throws FileNotFoundException {
        this.product = product;

        long numberOfLibrariesWithGroupId = libraries.stream().filter(library -> !library.getGroupId().isEmpty()).count();
        if (numberOfLibrariesWithGroupId > 0) this.withGroupId = true;

        if (distinct) {
            this.libraries = groupLibraryDuplicates(libraries);
        } else {
            Map<String, List<Library>> libraryMap = new LinkedHashMap<>(128);

            // TODO add library.type to libraryName to have a unique key name
            libraries.forEach(library -> {
                String libraryName = !library.getGroupId().isEmpty()
                    ? library.getGroupId() + NAME_DELIMITER + library.getArtifactId() + NAME_DELIMITER + library.getVersion()
                    : library.getArtifactId() + NAME_DELIMITER + library.getVersion();

                libraryName = libraryName.replaceAll(" ", NAME_DELIMITER);
                List<Library> newLibraryList = new ArrayList<>();
                newLibraryList.add(library);
                libraryMap.put(libraryName, newLibraryList);
            });
            this.libraries = libraryMap;
        }
        this.htmlTemplate = ResourceUtils.getFile(DEFAULT_HTML_TEMPLATE);
    }

    public OssListHelper(Product product, List<Library> libraries, String htmlTemplate) throws FileNotFoundException {
        this.product = product;
        this.libraries = groupLibraryDuplicates(libraries);
        this.htmlTemplate = ResourceUtils.getFile(htmlTemplate);
    }

    public Map<String, List<Library>> groupLibraryDuplicates(List<Library> libraries) {
        Map<String, List<Library>> libraryMap = new LinkedHashMap<>(128);

        // TODO add library.type to libraryName to have a unique key name
        libraries.forEach(library -> {
            String libraryName = !library.getGroupId().isEmpty()
                ? library.getGroupId() + NAME_DELIMITER + library.getArtifactId()
                : library.getArtifactId();

            libraryName = libraryName.replaceAll(" ", NAME_DELIMITER);

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

    public byte[] createPublishCsv() throws IOException {
        Deque<String> headers = new ArrayDeque<>(3);
        if (withGroupId) {
            headers.add("GroupId");
        }
        headers.add("ArtifactId");
        headers.add("License");


        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setDelimiter(';')
            .setHeader(headers.toArray(new String[0]))
            .build();

        StringBuilder csvBuilder = new StringBuilder(32768);
        try (CSVPrinter csvPrinter = new CSVPrinter(csvBuilder, csvFormat)) {
            for (Map.Entry<String, List<Library>> entry : libraries.entrySet()) {
                List<Library> value = entry.getValue();
                int counter = 1;
                List<String> licenses = new ArrayList<>(4);
                for (Library library : value) {
                    String currentLicenses = library
                        .getLicenseToPublishes()
                        .stream()
                        .map(License::getShortIdentifier)
                        .collect(Collectors.joining(" AND "));
                    if (!licenses.contains(currentLicenses)) {
                        licenses.add(" (" + counter + ")");
                        licenses.add(currentLicenses);
                    }
                    counter++;
                }

                // remove first element (enumeration) if library has only one license
                if (licenses.size() <= 2) licenses.remove(0);

                if (withGroupId) {
                    csvPrinter.printRecord(value.get(0).getGroupId(), value.get(0).getArtifactId(), String.join("", licenses).trim());
                } else {
                    csvPrinter.printRecord(value.get(0).getArtifactId(), String.join("", licenses));
                }

                csvPrinter.flush();
            }
        }

        return csvBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

    public void createHml(boolean distinct) {
        try {
            html = new String(Files.readAllBytes(htmlTemplate.toPath()));
        } catch (IOException e) {
            log.error("Error while reading HTML template : {}", e.getMessage());
        }

        html = html.replace("%{product}%", product.getName() + " " + product.getVersion());
        html = addTableContent(createTableContent(distinct));
        html = html.replace("%{tableHeader}%", tableHeader);
    }

    private String addTableContent(String content) {
        return html.replace("%{tableContent}%", content);
    }

    public String createTableContent(boolean distinct) {
        StringBuilder tableContent = new StringBuilder();

        libraries.forEach((key, value) -> {
            int counter = 1;

            String groupId = value.get(0).getGroupId();
            String artifactId = value.get(0).getArtifactId();
            List<String> licenses = new ArrayList<>(4);
            LicenseRisk licenseRisk = value.get(0).getLicenseRisk(value.get(0).getLicenseToPublishes());
            String risk = licenseRisk != null ? licenseRisk.getName() : "Unknown";

            for (Library library : value) {
                if (distinct) {
                    String currentlicenses = library
                        .getLicenseToPublishes()
                        .stream()
                        .map(License::getShortIdentifier)
                        .collect(Collectors.joining(" AND "));
                    if (!licenses.contains(currentlicenses)) {
                        licenses.add(" <span class=\"enumeration\">" + counter + "</span> ");
                        licenses.add(currentlicenses);
                    }
                    counter++;
                }
            }

            if (distinct) {
                // remove first element (enumeration) if library has only one license
                if (licenses.size() <= 2) licenses.remove(0);
                addTableHeader(withGroupId);
                tableContent.append(addTableRow(groupId, artifactId, String.join("", licenses), withGroupId));
            } else {
                addTableHeaderDefault(withGroupId);
                tableContent.append(
                    addTableRowDefault(
                        groupId,
                        artifactId,
                        value.get(0).getVersion() != null ? value.get(0).getVersion() : "",
                        value.get(0).getType() != null ? value.get(0).getType().getValue() : "",
                        //value.get(0).getLicenseToPublishes().stream().map(License::getShortIdentifier).collect(Collectors.joining(" AND ")),
                        value.get(0).getLicenses() != null ? value.get(0).printLinkedLicenses() : "",
                        risk,
                        value.get(0).getLicenseUrl() != null &&
                            !StringUtils.isBlank(value.get(0).getLicenseUrl()) &&
                            !value.get(0).getLicenseUrl().equalsIgnoreCase(Constants.NO_URL)
                            ? value.get(0).getLicenseUrl()
                            : value.get(0).getLicenseToPublishes().stream().map(License::getUrl).collect(Collectors.joining(", ")),
                        value.get(0).getSourceCodeUrl() != null ? value.get(0).getSourceCodeUrl() : "",
                        withGroupId
                    )
                );
            }
        });

        return tableContent.toString();
    }

    private void addTableHeader(boolean withGroupId) {
        if (withGroupId) {
            this.tableHeader = "<tr>\n" + "<th>GroupId</th>\n" + "<th>ArtifactId</th>\n" + "<th>License</th>\n" + "</tr>";
        } else {
            this.tableHeader = "<tr>\n" + "<th>ArtifactId</th>\n" + "<th>License</th>\n" + "</tr>";
        }
    }

    private void addTableHeaderDefault(boolean withGroupId) {
        if (withGroupId) {
            this.tableHeader =
                "<tr>\n" +
                "<th>GroupId</th>\n" +
                "<th>ArtifactId</th>\n" +
                "<th>Version</th>\n" +
                "<th>Type</th>\n" +
                "<th>License</th>\n" +
                "<th>LicenseRisk</th>\n" +
                "<th>LicenseUrl</th>\n" +
                "<th>SourceCodeUrl</th>\n" +
                "</tr>";
        } else {
            this.tableHeader =
                "<tr>\n" +
                "<th>ArtifactId</th>\n" +
                "<th>Version</th>\n" +
                "<th>Type</th>\n" +
                "<th>License</th>\n" +
                "<th>LicenseRisk</th>\n" +
                "<th>LicenseUrl</th>\n" +
                "<th>SourceCodeUrl</th>\n" +
                "</tr>";
        }
    }

    private String addTableRow(String groupId, String artifactId, String licenses, boolean withGroupId) {
        String template;

        if (withGroupId) {
            template = "<tr>\n" + "<td>%{groupId}%</td>\n" + "<td>%{artifactId}%</td>\n" + "<td>%{licenses}%</td>\n" + "</tr>";
        } else {
            template = "<tr>\n" + "<td>%{artifactId}%</td>\n" + "<td>%{licenses}%</td>\n" + "</tr>";
        }

        return template.replace("%{groupId}%", groupId).replace("%{artifactId}%", artifactId).replace("%{licenses}%", licenses);
    }

    private String addTableRowDefault(
        String groupId,
        String artifactId,
        String version,
        String type,
        String licenses,
        String licenseRisk,
        String licenseUrl,
        String sourceCodeUrl,
        boolean withGroupId
    ) {
        String template;

        if (withGroupId) {
            template =
                "<tr>\n" +
                "<td>%{groupId}%</td>\n" +
                "<td>%{artifactId}%</td>\n" +
                "<td>%{version}%</td>\n" +
                "<td>%{type}%</td>\n" +
                "<td style=\"background-color:" +
                RiskColor.getColorByRiskName(licenseRisk) +
                "\">%{licenses}%</td>\n" +
                "<td style=\"background-color:" +
                RiskColor.getColorByRiskName(licenseRisk) +
                "\">%{licenseRisk}%</td>\n" +
                "<td>%{licenseUrl}%</td>\n" +
                "<td>%{sourceCodeUrl}%</td>\n" +
                "</tr>";
        } else {
            template =
                "<tr>\n" +
                "<td>%{artifactId}%</td>\n" +
                "<td>%{version}%</td>\n" +
                "<td>%{type}%</td>\n" +
                "<td style=\"background-color:" +
                RiskColor.getColorByRiskName(licenseRisk) +
                "\">%{licenses}%</td>\n" +
                "<td style=\"background-color:" +
                RiskColor.getColorByRiskName(licenseRisk) +
                "\">%{licenseRisk}%</td>\n" +
                "<td>%{licenseUrl}%</td>\n" +
                "<td>%{sourceCodeUrl}%</td>\n" +
                "</tr>";
        }

        return template
            .replace("%{groupId}%", groupId)
            .replace("%{artifactId}%", artifactId)
            .replace("%{version}%", version)
            .replace("%{type}%", type)
            .replace("%{licenses}%", licenses)
            .replace("%{licenseRisk}%", licenseRisk)
            .replace("%{licenseUrl}%", licenseUrl)
            .replace("%{sourceCodeUrl}%", sourceCodeUrl);
    }

    public void createFullHtml(List<String> requirementsLookup) {
        List<List<String>> data = new ArrayList<>();

        List<String> header = new ArrayList<>(
            Arrays.asList("GroupId", "ArtifactId", "Version", "License", "LicenseRisk", "LicensesTotal", "Comment", "ComplianceComment")
        );

        header.addAll(requirementsLookup);
        Set<String> totalLicenses = new HashSet<>();

        libraries.forEach((key, value) -> {

            for (Library libraryPerProduct : value) {
                String groupId = libraryPerProduct.getGroupId();
                String artifactId = libraryPerProduct.getArtifactId();
                String version = libraryPerProduct.getVersion();

                // Changing the button value when is pressed is defined in the collapse JS function in the ossList/default.html
                String beforeData =
                    "<button type=\"button\" onclick=\"collapse(this)\" class=\"collapsible\">Show comment</button>\n" +
                    "<div class=\"content\">\n" +
                    "<p>";
                String afterData = "</p>\n" + "</div>";

                String comment = !StringUtils.isBlank(libraryPerProduct.getComment())
                    ? beforeData + libraryPerProduct.getComment() + afterData
                    : "No comment";

                String complianceComment = !StringUtils.isBlank(libraryPerProduct.getComplianceComment())
                    ? beforeData + libraryPerProduct.getComplianceComment() + afterData
                    : "No comment";

                String licenseRisk = libraryPerProduct.getLicenseRisk(libraryPerProduct.getLicenseToPublishes()).getName();
                List<String> requirements = new ArrayList<>(16);

                for (String ignored : requirementsLookup) {
                    requirements.add("");
                }

                libraryPerProduct.getLicenses().forEach(e -> totalLicenses.add(e.getLicense().getShortIdentifier()));

                if (libraryPerProduct.getLicenseToPublishes() != null && !libraryPerProduct.getLicenseToPublishes().isEmpty()) {
                    for (License licenseToPublish : libraryPerProduct.getLicenseToPublishes()) {
                        if (licenseToPublish.getRequirements() != null) {
                            for (Requirement requirement : licenseToPublish.getRequirements()) {
                                if (requirementsLookup.contains(requirement.getShortText())) {
                                    requirements.remove(requirementsLookup.indexOf(requirement.getShortText()));
                                    requirements.add(requirementsLookup.indexOf(requirement.getShortText()), "X");
                                }
                            }
                        }
                    }
                }

                List<String> resultList = new ArrayList<>();
                Collections.addAll(
                    resultList,
                    groupId,
                    artifactId,
                    "V" + version,
                    libraryPerProduct.printLinkedLicenses(),
                    licenseRisk,
                    String.valueOf(totalLicenses.size()),
                    comment,
                    complianceComment
                );

                resultList.addAll(requirements);

                data.add(resultList);
            }
        });

        data.forEach(e -> {});

        this.tableHeader += "<tr>\n";
        header.forEach(e -> this.tableHeader += "<th>" + e + "</th>\n");
        this.tableHeader += "</tr>";

        StringBuilder content = new StringBuilder();
        data.forEach(e -> {
            content.append("<tr>\n");

            for (String ele : e) {
                content.append("<td>").append(ele).append("</td>\n");
            }

            content.append("</tr>\n");
        });

        try {
            html = new String(Files.readAllBytes(htmlTemplate.toPath()));
        } catch (IOException e) {
            log.error("Error while reading HTML template : {}", e.getMessage());
        }

        html = html.replace("%{product}%", product.getName() + " " + product.getVersion());
        html = addTableContent(content.toString());
        html = html.replace("%{tableHeader}%", tableHeader);
    }

    public String getHtml() {
        return this.html;
    }
}
