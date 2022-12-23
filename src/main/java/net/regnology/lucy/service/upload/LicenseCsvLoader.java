package net.regnology.lucy.service.upload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import net.regnology.lucy.domain.*;
import net.regnology.lucy.service.exceptions.UploadException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenseCsvLoader implements AssetLoader<License> {

    private final Logger log = LoggerFactory.getLogger(LicenseCsvLoader.class);

    @Override
    public Set<License> load(File file) throws UploadException {
        Set<License> licenses = new HashSet<>(64);

        try {
            CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader();

            CSVParser records = csvFormat.parse(
                new InputStreamReader(new BOMInputStream(new ByteArrayInputStream(file.getFile())), StandardCharsets.UTF_8)
            );

            List<String> headers = records.getHeaderNames();

            for (CSVRecord record : records) {
                try {
                    License license = new License();
                    license.setFullName(record.get("FullName"));
                    license.setShortIdentifier(record.get("ShortIdentifier"));

                    if (record.isMapped("SpdxIdentifier")) license.setSpdxIdentifier(record.get("SpdxIdentifier"));
                    if (record.isMapped("Url")) license.setUrl(record.get("Url"));
                    if (record.isMapped("LicenseText")) license.setGenericLicenseText(record.get("LicenseText"));
                    if (record.isMapped("Other")) license.setOther(record.get("Other"));

                    if (record.isMapped("Reviewed")) {
                        license.setReviewed(record.get("Reviewed").equalsIgnoreCase("x"));
                        license.setLastReviewedDate(LocalDate.now());
                    }
                    if (record.isMapped("LastReviewedDate") && !StringUtils.isBlank(record.get("LastReviewedDate"))) {
                        try {
                            license.setLastReviewedDate(LocalDate.parse(record.get("LastReviewedDate"), DateTimeFormatter.ISO_LOCAL_DATE));
                        } catch (DateTimeParseException e) {
                            log.debug("LastReviewedDate is not valid : {}", record.get("LastReviewedDate"));
                        }
                    }
                    if (record.isMapped("LastReviewedBy") && !record.get("LastReviewedBy").isEmpty()) {
                        User user = new User();
                        user.setLogin(record.get("LastReviewedBy"));
                        license.setLastReviewedBy(user);
                    }

                    if (record.isMapped("LicenseRisk") && !record.get("LicenseRisk").isEmpty()) {
                        LicenseRisk licenseRisk = new LicenseRisk();
                        licenseRisk.setName(record.get("LicenseRisk"));
                        license.setLicenseRisk(licenseRisk);
                    }

                    /* The requirement information is stored in the "ShortText" attribute of a requirement object,
                    so that it can be read and processed in the UploadService later.
                    (Multiple requirements in one requirement object)
                    */
                    StringJoiner requirementsBuilder = new StringJoiner(";");

                    int columnNo = 0;
                    for (String field : record) {
                        if (!headers.get(columnNo).equals("Reviewed") && field.equalsIgnoreCase("x")) {
                            String columnName = headers.get(columnNo);
                            requirementsBuilder.add(columnName);
                        }
                        columnNo++;
                    }
                    if (requirementsBuilder.length() > 0) {
                        Requirement requirement = new Requirement();
                        requirement.setShortText(requirementsBuilder.toString());
                        license.setRequirements(Collections.singleton(requirement));
                    }

                    licenses.add(license);
                } catch (IllegalArgumentException e) {
                    log.error("File does not match any known CSV format : {}", e.getMessage());
                    throw new UploadException("File does not match any known CSV format");
                }
            }
        } catch (IOException e) {
            log.error("Error while parsing CSV file : {}", e.getMessage());
            throw new UploadException("CSV can't be read");
        }

        return licenses;
    }
}
