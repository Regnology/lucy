package net.regnology.lucy.service.upload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import net.regnology.lucy.domain.File;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.domain.enumeration.LibraryType;
import net.regnology.lucy.domain.helper.PackageInfo;
import net.regnology.lucy.service.LibraryCustomService;
import net.regnology.lucy.service.exceptions.UploadException;
import net.regnology.lucy.service.helper.hashing.ArchiveHasherV2;
import net.regnology.lucy.service.helper.net.HttpHelper;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchiveLoader implements AssetLoader<Library> {

    private final Logger log = LoggerFactory.getLogger(ArchiveLoader.class);
    private final LibraryCustomService libraryService;
    private final Set<Library> globalList = new HashSet<>(256);

    public ArchiveLoader(LibraryCustomService libraryService) {
        this.libraryService = libraryService;
    }

    @Override
    public Set<Library> load(File file) throws UploadException {
        try (
            ArchiveInputStream inputStream = (
                file.getFilestream() != null
                    ? ArchiveHasherV2.createArchiveInputStreamSimpleWithCompress(file.getFilestream())
                    : ArchiveHasherV2.createArchiveInputStreamSimpleWithCompress(new ByteArrayInputStream(file.getFile()))
            )
        ) {
            Set<PackageInfo> packagesFromFile = ArchiveHasherV2.processArchiveStream2(inputStream);

            System.gc();

            // Find libraries in database
            log.info("Searching for findings in database..");
            Set<PackageInfo> list = findPackagesInDatabase(packagesFromFile);
            // Find remaining libraries on Maven
            //List<String> packageList = findPackagesOnMaven(list);
            // Process findings and make them into Library objects
            //for (String s : list) {
            //    globalList.add(stringToLibrary(s));
            //}

        } catch (IOException | ArchiveException e) {
            log.error("Archive cannot be read : {}", e.getMessage());
            throw new UploadException("Unsupported or defect archive");
        }

        // Find remaining libraries on Maven
        //List<String> packageList = findPackagesOnMaven(list);
        // Process findings and make them into Library objects
        //for (String s : packageList) {
        //    globalList.add(stringToLibrary(s));
        //}

        /*try {
            ArchiveHasherV2 ah = new ArchiveHasherV2();
            CompletableFuture<Void> future;
            try (
                ArchiveInputStream ais = (
                    file.getFilestream() != null
                        ? ah.createArchiveInputStream(file.getFilestream())
                        : ah.createArchiveInputStream(file.getFile())
                )
            ) {
                future = ah.processArchiveStream(ais);
                future.get();
                log.info("Closing all streams..");
                ArchiveHasherV2.closeAllStreams();
            } catch (ArchiveException e) {
                log.error("Cannot read archive : {}", e.getMessage());
                return globalList;
            }

            //ah.start();
            //ah.join();

            // Find libraries in database
            log.info("Searching for findings in database..");
            Set<PackageInfo> list = findPackagesInDatabase(ah.getPackageList());
            // Find remaining libraries on Maven
            //List<String> packageList = findPackagesOnMaven(list);
            // Process findings and make them into Library objects
            //for (String s : packageList) {
            //    globalList.add(stringToLibrary(s));
            //}
        } catch (InterruptedException | IOException | ExecutionException e) {
            log.error("There was an exception while handling the file upload: {}", e.getMessage());
        }*/

        for (Library library : globalList) {
            log.debug("Library from HASHER : {} - {} - {}", library.getGroupId(), library.getArtifactId(), library.getVersion());
        }

        return globalList;
    }

    /**
     * Checks if libraries from a given Set are already present in the database.
     *
     * @param packageInfos a Set of libraries to check
     * @return a new Set of libraries containing only the ones that are not already in the database.
     */
    private Set<PackageInfo> findPackagesInDatabase(Set<PackageInfo> packageInfos) {
        HashSet<PackageInfo> newPackages = new HashSet<>();
        int counter = 1;
        for (PackageInfo pi : packageInfos) {
            log.debug("COUNTER : {}", counter);
            log.debug("PACKAGE : {}", pi);
            Optional<Library> libraryOptional = libraryService.findByHash(pi.getMd5Hash());
            if (libraryOptional.isEmpty()) {
                newPackages.add(pi);
                log.debug("Package not found in database: {}", pi.getFileName());
            } else {
                globalList.add(libraryOptional.get());
            }
            counter++;
        }
        return newPackages;
    }

    private List<String> findPackagesOnMaven(Set<PackageInfo> packageInfos) {
        ArrayList<String> list = new ArrayList<>();
        try {
            for (PackageInfo pi : packageInfos) {
                String searchURL = String.format("https://search.maven.org/solrsearch/select?q=1:%s&wt=json", pi.getSha1Hash());
                //JSON data is requested from the Maven search API
                var httpResponse = HttpHelper.httpGetRequest(searchURL);
                if (httpResponse.statusCode() != 200) {
                    log.error("Bad HTTP response while accessing Maven search: {}", httpResponse.statusCode());
                    break;
                }

                //JSONparser is initialized for reading the response
                JSONParser jsonparser = new JSONParser();
                JSONObject mvnResponse = (JSONObject) jsonparser.parse(httpResponse.body());
                // Reading JSON response
                JSONObject response = (JSONObject) mvnResponse.get("response");
                if ((Long) response.get("numFound") == 0) {
                    log.info("Object not found on Maven search! {}", pi.getFileName());
                    continue;
                }
                JSONArray docs = (JSONArray) response.get("docs");
                if (docs.size() == 0) {
                    log.info("Metadata for Object not found on Maven search! {}", pi.getFileName());
                    continue;
                }
                JSONObject index = (JSONObject) docs.get(0);
                // fullID contains group:artifact:version:md5:sha1
                String fullID = (String) index.get("id");
                list.add(fullID + ":" + pi.getMd5Hash() + ":" + pi.getSha1Hash());
            }
        } catch (InterruptedException e) {
            log.error("There was an Exception handling an Archive upload or processing! {}", e.getMessage());
        } catch (URISyntaxException | IOException e) {
            log.error("There was an error connecting to the Maven search API! {}", e.getMessage());
        } catch (ParseException e) {
            log.error("The Maven search response body couldn't be parsed as JSON!");
        }
        return list;
    }

    /**
     * @param s a String with the format groupID:artifactID:version:md5:sha1
     * @return a Library object filled with the given information.
     */
    private Library stringToLibrary(String s) {
        String[] splitString = s.split(":");
        log.debug(s);
        Library lib = new Library();
        lib.setType(LibraryType.MAVEN);
        lib.setGroupId(splitString[0]);
        lib.setArtifactId(splitString[1]);
        lib.setVersion(splitString[2]);
        lib.setMd5(splitString[3]);
        lib.setSha1(splitString[4]);
        return lib;
    }
}
