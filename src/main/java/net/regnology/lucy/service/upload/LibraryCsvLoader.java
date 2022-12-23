package net.regnology.lucy.service.upload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.regnology.lucy.domain.File;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.domain.License;
import net.regnology.lucy.domain.User;
import net.regnology.lucy.domain.enumeration.LibraryType;
import net.regnology.lucy.service.exceptions.UploadException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryCsvLoader implements AssetLoader<Library> {

    private final Logger log = LoggerFactory.getLogger(LibraryCsvLoader.class);

    @Override
    public Set<Library> load(File file) throws UploadException {
        Set<Library> libraries = new HashSet<>(256);

        try {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withDelimiter(';')
                .withFirstRecordAsHeader()
                /* BOMInputStream because CSV saved from Excel contains special bytes at the beginning of the file.
                Encoding is UTF-8-BOM not UTF-8 only.
                First column of the CSV can't be read properly with normal InputStream.
                 */
                .parse(new InputStreamReader(new BOMInputStream(new ByteArrayInputStream(file.getFile())), StandardCharsets.UTF_8));

            for (CSVRecord record : records) {
                try {
                    Library library = new Library();

                    // Mandatory columns. Exception if not in CSV
                    library.setArtifactId(record.get("ArtifactId"));
                    library.setVersion(record.get("Version"));

                    // Optional columns
                    if (record.isMapped("GroupId")) library.setGroupId(record.get("GroupId"));
                    if (record.isMapped("Type")) library.setType(LibraryType.getLibraryTypeByValue(record.get("Type")));
                    if (record.isMapped("License")) library.setOriginalLicense(record.get("License"));
                    if (record.isMapped("OriginalLicense")) library.setOriginalLicense(record.get("OriginalLicense"));
                    if (record.isMapped("LicenseUrl")) library.setLicenseUrl(record.get("LicenseUrl"));
                    if (record.isMapped("SourceCodeUrl")) library.setSourceCodeUrl(record.get("SourceCodeUrl"));
                    if (record.isMapped("Copyright")) library.setCopyright(record.get("Copyright"));
                    if (record.isMapped("LicenseText")) library.setLicenseText(record.get("LicenseText"));
                    if (record.isMapped("purl")) library.setpUrl(record.get("purl"));
                    if (record.isMapped("Comment")) library.setComment(record.get("Comment"));
                    if (record.isMapped("Compliance")) library.setCompliance(record.get("Compliance"));
                    if (record.isMapped("ComplianceComment")) library.setComplianceComment(record.get("ComplianceComment"));

                    if (record.isMapped("HideForPublishing")) {
                        library.setHideForPublishing(record.get("HideForPublishing").equalsIgnoreCase("x"));
                    }

                    if (record.isMapped("CreatedDate") && !StringUtils.isBlank(record.get("CreatedDate"))) {
                        try {
                            library.setCreatedDate(LocalDate.parse(record.get("CreatedDate"), DateTimeFormatter.ISO_LOCAL_DATE));
                        } catch (DateTimeParseException e) {
                            log.info("CreatedDate is not valid : {}", record.get("CreatedDate"));
                        }
                    }

                    if (record.isMapped("Reviewed")) {
                        library.setReviewed(record.get("Reviewed").equalsIgnoreCase("x"));
                        library.setLastReviewedDate(LocalDate.now());
                    }
                    if (record.isMapped("LastReviewedDate") && !StringUtils.isBlank(record.get("LastReviewedDate"))) {
                        try {
                            library.setLastReviewedDate(LocalDate.parse(record.get("LastReviewedDate"), DateTimeFormatter.ISO_LOCAL_DATE));
                        } catch (DateTimeParseException e) {
                            log.info("LastReviewedDate is not valid : {}", record.get("LastReviewedDate"));
                        }
                    }
                    if (record.isMapped("LastReviewedBy") && !record.get("LastReviewedBy").isEmpty()) {
                        User user = new User();
                        user.setLogin(record.get("LastReviewedBy"));
                        library.setLastReviewedBy(user);
                    }

                    /* The license information is stored in the "FullName" attribute of a license object,
                    so that it can be read and processed in the UploadService later.
                    (Multiple licenses in one license object)
                    */
                    /*if (record.isMapped("Licenses") && !record.get("Licenses").isEmpty()) {
                        License license = new License();
                        license.setFullName(record.get("Licenses"));
                        library.setLicenses(Collections.singleton(license));
                    }*/
                    if (record.isMapped("LicenseToPublish") && !record.get("Licenses").isEmpty()) {
                        License license = new License();
                        license.setFullName(record.get("LicenseToPublish"));
                        library.setLicenseToPublishes(Collections.singleton(license));
                    }
                    if (record.isMapped("LicenseOfFiles") && !record.get("LicenseOfFiles").isEmpty()) {
                        License license = new License();
                        license.setFullName(record.get("LicenseOfFiles"));
                        library.setLicenseOfFiles(Collections.singleton(license));
                    }

                    libraries.add(library);
                } catch (IllegalArgumentException e) {
                    log.error("File does not match any known CSV format : {}", e.getMessage());
                    throw new UploadException("File does not match any known CSV format");
                }
            }
        } catch (IOException e) {
            log.error("Error parsing CSV file : {}", e.getMessage());
            throw new UploadException("CSV can't be read");
        }

        return libraries;
    }
}
