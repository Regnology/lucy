package net.regnology.lucy.service.upload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import net.regnology.lucy.domain.File;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.domain.enumeration.LibraryType;
import net.regnology.lucy.service.exceptions.UploadException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdditionalLibrariesLoader implements AssetLoader<Library> {

    private final Logger log = LoggerFactory.getLogger(LibraryCsvLoader.class);

    @Override
    public Set<Library> load(File file) throws UploadException {
        Set<Library> libraries = new HashSet<>(16);

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
