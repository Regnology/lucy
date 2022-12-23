package net.regnology.lucy.service.helper.sourceCode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.PostConstruct;
import net.regnology.lucy.config.ApplicationProperties;
import net.regnology.lucy.config.Constants;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.domain.License;
import net.regnology.lucy.domain.Requirement;
import net.regnology.lucy.service.helper.net.HttpHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

@Component
public class SourceCodeHelper {

    private static final Logger log = LoggerFactory.getLogger(SourceCodeHelper.class);

    public static final String LOCAL_STORAGE_URI = "classpath:filesArchive/sourceCodeDelivery/";

    public static boolean withRemoteIndexFile = false;

    private final ApplicationProperties applicationProperties;

    private String remoteIndexFilePath;
    private String remoteIndexFileName;
    private String remoteIndexPath;
    private String remoteArchivePath;

    public SourceCodeHelper(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @PostConstruct
    public void init() {
        this.remoteIndexFilePath = applicationProperties.getSourceCodeArchive().getRemoteIndex();
        String[] remoteIndexPaths = remoteIndexFilePath.split("/");
        this.remoteIndexFileName = remoteIndexPaths[(remoteIndexPaths.length) - 1];
        this.remoteIndexPath = remoteIndexFilePath.replaceAll(remoteIndexFileName + "$", "");
        this.remoteArchivePath = applicationProperties.getSourceCodeArchive().getRemoteArchive();

        initIndexFile();
    }

    /**
     * Check if the library has the requirement to share the source code.
     *
     * @param library a library object
     * @return true if the requirement is set, otherwise false
     */
    public static boolean isCodeSharingRequired(Library library) {
        //Iterates through all the licenses of the library
        Set<License> licenses = library.getLicenseToPublishes();
        for (License license : licenses) {
            //Iterates through all the requirements of the library
            Set<Requirement> requirements = license.getRequirements();
            for (Requirement requirement : requirements) {
                //If Requirement 4 ("Share Source") is found, the library needs to be contained in the archive
                if (Long.valueOf(4).equals(requirement.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Searches the local index file for the library, returns full identifier with file extension
     *
     * @param indexFile Index file
     * @param label     name of the library to search for. GroupId_ArtifactId_Version
     * @return full identifier with file extension or null if not in index file
     * @throws IOException if the index file cannot be found or read
     */
    public static String checkRepository(java.io.File indexFile, String label) throws IOException {
        log.info("Searching for library in index file : {}", label);
        //File ending needs to be all letters or bz2 or 7z
        //Other numbers are not allowed in the file ending as they could be interpreted as part of the version

        //var regexLabel = "^" + label + "\\.([A-Za-z]*)|\\.bz2|\\.7z";

        try (Stream<String> stream = Files.lines(Paths.get(indexFile.getPath()))) {
            // Split line by ';' and check if the first part matches the label
            return stream.filter(line -> line.split(";")[0].matches(label)).findFirst().map(s -> s.split(";")[1]).orElse(null);
        }
        /*try (var scanner = new Scanner(indexFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.matches(regexLabel)) {
                    return line;
                }
            }
        }
        return null;*/
    }

    /**
     * Searches the local index file for the library, returns full identifier with file extension
     *
     * @param indexFile Index file
     * @param label     name of the library to search for. GroupId_ArtifactId_Version
     * @return full identifier with file extension or null if not in index file
     * @throws IOException if the index file cannot be found or read
     */
    public static String checkRepositoryWithFuzzySearch(java.io.File indexFile, String label) throws IOException {
        try (Stream<String> fuzzyStream = Files.lines(Paths.get(indexFile.getPath()))) {
            List<Pair<String, Float>> fuzzyResults = FuzzySearch.search(label, fuzzyStream);
            if (fuzzyResults.size() > 0) {
                Optional<Pair<String, Float>> optionalBestResult = fuzzyResults
                    .stream()
                    .min((result1, result2) -> Float.compare(result1.getSecond(), result2.getSecond()));

                Pair<String, Float> bestResult = optionalBestResult.get();
                log.info("Source Code Archive found with Fuzzy Search : {} - {}", bestResult.getFirst(), bestResult.getSecond());
                return bestResult.getFirst().split(";")[1];
            } else {
                log.info("No entry was found with the FuzzySearch : {}", label);
            }
        }

        return null;
    }

    /**
     * Traces a library and updates the remote repository if library is available
     *
     * @param label             name of the library. GroupId_ArtifactId_Version
     * @param library           a library entity
     * @param indexFile         the local index file
     * @param localArchivePath  path to the local archive
     * @param remoteArchivePath URL of the remote archive
     * @throws IOException if the source code URL from the library cannot be downloaded,
     *                     the transfer of the downloaded file fails or it cannot be written to the local index file.
     */
    public static void updateRepository(
        String label,
        Library library,
        java.io.File indexFile,
        String localArchivePath,
        String remoteArchivePath
    ) throws IOException {
        //Checks if the source code URL exists
        if (!library.sourceCodeUrlIsValid()) {
            throw new IOException("Source Code Url invalid.");
        }

        //Parses the filename and downloads the file from the official repository
        String fileExtension = library.getSourceCodeUrl().replaceAll("^.*\\.", ".");
        String fileName = label + fileExtension;
        var libraryPackage = new java.io.File(localArchivePath + fileName);
        HttpHelper.downloadResource(library.getSourceCodeUrl(), libraryPackage);

        //Uploads the library to the external repository
        HttpHelper.transferToTarget(remoteArchivePath, Files.readAllBytes(libraryPackage.toPath()), fileName);

        //Updates the local index file
        try (var writer = new FileWriter(indexFile, StandardCharsets.UTF_8, true)) {
            writer.write(label + ";" + fileName);
            writer.write(System.lineSeparator());
        }
    }

    /**
     * Create a ZIP archive from a folder.
     *
     * @param localArchiveFolder folder as a File object
     * @return the zipped folder
     * @throws IOException if the folder is not readable or the ZIP archive cannot be created
     */
    public static java.io.File createZip(java.io.File localArchiveFolder) throws IOException {
        String zipName = localArchiveFolder.toString() + ".zip";
        var zipFile = new java.io.File(zipName);

        //Iterates through the individual packages, creates a new entry in the ZIP and adds the file content to the entry
        try (var zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (var currentPackage : Objects.requireNonNull(localArchiveFolder.listFiles())) {
                var packageName = currentPackage.getName();
                var zipEntry = new ZipEntry(packageName);
                //Stream the file content is written into
                zipOutputStream.putNextEntry(zipEntry);

                //Content of the file is read
                byte[] data = Files.readAllBytes(currentPackage.toPath());

                //Content is saved via stream
                zipOutputStream.write(data, 0, data.length);
                zipOutputStream.closeEntry();
            }
            return zipFile;
        }
    }

    /**
     * Create a label from a library. Uses the GroupId, ArtifactId and Version.
     * If the GroupId is empty only the ArtifactId and Version is used.
     *
     * @param library a library entity
     * @return the created label
     */
    public static String createLabel(Library library) {
        return StringUtils.isBlank(library.getGroupId())
            ? library.getArtifactId() + "_" + library.getVersion()
            : library.getGroupId() + "_" + library.getArtifactId() + "_" + library.getVersion();
    }

    /**
     * Get the local index.csv file.
     *
     * @return the index.csv file
     * @throws FileNotFoundException if the index file cannot be found
     */
    public static File getLocalIndexFile() throws FileNotFoundException {
        var localStorageDirectory = ResourceUtils.getFile(SourceCodeHelper.LOCAL_STORAGE_URI);
        var localStoragePath = localStorageDirectory.getPath() + Constants.FILE_SEPARATOR;

        return new java.io.File(localStoragePath + Constants.INDEX);
    }

    private void initIndexFile() {
        //Sets the local paths and files
        try {
            var localStorageDirectory = ResourceUtils.getFile(LOCAL_STORAGE_URI);
            var localStoragePath = localStorageDirectory.getPath() + Constants.FILE_SEPARATOR;
            var localIndexFile = new java.io.File(localStoragePath + Constants.INDEX);

            //Deletes local, outdated index.csv file if existing
            try {
                FileUtils.forceDelete(localIndexFile);
            } catch (FileNotFoundException e) {
                log.info("Index.csv is not existing in local storage : {}", localStoragePath);
            } catch (IOException e) {
                log.error("Could not delete index.csv in local storage {} : {}", localStoragePath, e.getMessage());
            }

            //Downloads the up-to-date index.csv file of the remote 3rd-party repository, throws a RemoteRepositoryException if the file is not available
            try {
                HttpHelper.downloadResource(remoteIndexFilePath, localIndexFile);
            } catch (IOException e) {
                log.error("Could not download index.csv file from {} : {}", remoteIndexFilePath, e.getMessage());
                return;
            }
        } catch (FileNotFoundException e) {
            log.error("Could not find directory for the index.csv file : {}", LOCAL_STORAGE_URI);
            return;
        }

        withRemoteIndexFile = true;
    }

    /* Getter */

    public String getRemoteIndexFilePath() {
        return remoteIndexFilePath;
    }

    public String getRemoteIndexFileName() {
        return remoteIndexFileName;
    }

    public String getRemoteIndexPath() {
        return remoteIndexPath;
    }

    public String getRemoteArchivePath() {
        return remoteArchivePath;
    }
}
