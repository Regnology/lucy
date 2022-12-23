package net.regnology.lucy.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Optional;
import net.regnology.lucy.config.ApplicationProperties;
import net.regnology.lucy.domain.Fossology;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.domain.enumeration.FossologyStatus;
import net.regnology.lucy.service.helper.net.HttpHelper;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
public class FossologyService {

    private final Logger log = LoggerFactory.getLogger(FossologyService.class);
    private static final String FOSSOLOGY_DIRECTORY = "classpath:filesArchive/fossology/";
    private static final String FOSSOLOGY_API_PART = "/api/v1";
    private static final String MESSAGE = "message";

    private static final String FOSSOLOGY_STATUS_COMPLETE = "Complete";
    private static final String FOSSOLOGY_STATUS_PROCESSING = "Processing";
    private static final String FOSSOLOGY_STATUS_QUEUED = "Queued";
    private static final String FOSSOLOGY_STATUS_FAILED = "Failed";

    private final ApplicationProperties applicationProperties;

    public FossologyService(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public void upload(InputStream file, String label) {
        // TODO: implement
    }

    @Async("fossologyTaskExecutor")
    public void upload(Library library, LibraryCustomService libraryService, String libraryUrl, String libraryLabel)
        throws IOException, ParseException {
        log.info("Start to analyse library {} with Fossology", library.getId());

        final String lucyEndpointForFileDownload = "/api/v1/fossology/scan?filename=";
        File fossologyDir = ResourceUtils.getFile(FOSSOLOGY_DIRECTORY);

        Fossology fossology;
        // Create Fossology entity if it doesn't exist, otherwise use the existing entity from the library
        if (library.getFossology() == null) {
            fossology = new Fossology();
            library.setFossology(fossology);
            library = libraryService.saveAndFlush(library);
        } else if (
            library.getFossology() != null &&
            (
                library.getFossology().getStatus().equals(FossologyStatus.SCAN_FINISHED) ||
                library.getFossology().getStatus().equals(FossologyStatus.FAILURE)
            )
        ) {
            fossology = new Fossology();
            library.setFossology(fossology);
            library = libraryService.saveAndFlush(library);
        }
        fossology = library.getFossology();

        try {
            HttpHelper.downloadResource(libraryUrl, new File(fossologyDir.getPath() + "/" + libraryLabel));
        } catch (IOException e) {
            fossology.setStatus(FossologyStatus.FAILURE);
            libraryService.saveAndFlush(library);
            log.error("Source code of Library {} cannot be downloaded : {}", library.getId(), e.getMessage());
            throw e;
        }
        ApplicationProperties.Fossology fossologyProperties = applicationProperties.getFossology();

        String lUrl;
        if (!StringUtils.isBlank(applicationProperties.getLucy().getDomain())) {
            lUrl = applicationProperties.getLucy().getDomain() + lucyEndpointForFileDownload + libraryLabel;
        } else {
            fossology.setStatus(FossologyStatus.FAILURE);
            libraryService.saveAndFlush(library);
            log.error(
                "The domain could not be determined. " +
                "In order for Fossology to scan a source code archive, an accessible domain is needed."
            );
            throw new UnknownHostException(
                "The domain could not be determined. In order for Fossology to scan a source code archive, an accessible domain is needed."
            );
        }

        String uploadId;
        try {
            uploadId = uploadToFossology(lUrl, fossologyProperties.getFolder(), libraryLabel, libraryLabel);
        } catch (IOException | ParseException e) {
            fossology.setStatus(FossologyStatus.FAILURE);
            libraryService.saveAndFlush(library);
            log.error("Upload of Library {} failed : {}", library.getId(), e.getMessage());
            throw e;
        }

        fossology.setUploadId(uploadId);
        fossology.setStatus(FossologyStatus.UPLOAD_STARTED);
        fossology.setLastScan(Instant.now());
        library.setFossology(fossology);

        library = libraryService.saveAndFlush(library);

        String jobStatus = FOSSOLOGY_STATUS_PROCESSING;
        do {
            try {
                Thread.sleep(4 * 1000);
                jobStatus = checkJobStatusByUploadId(uploadId);
                log.info("Fossology - Checking job status of uploadId {} : {}", uploadId, jobStatus);
            } catch (IOException | ParseException e) {
                fossology.setStatus(FossologyStatus.FAILURE);
                libraryService.saveAndFlush(library);
                log.error("An error occurred while connecting to Fossology : {}", e.getMessage());
                throw e;
            } catch (InterruptedException e) {
                log.error("Thread sleep error : {}", e.getMessage());
            }
        } while (jobStatus.equalsIgnoreCase("Processing") || jobStatus.equalsIgnoreCase("Queued"));

        if (jobStatus.equalsIgnoreCase("Completed")) {
            Optional<Library> libraryOptional = libraryService.findOne(library.getId());
            if (libraryOptional.isPresent()) library = libraryOptional.get();
            fossology = library.getFossology();
            fossology.setStatus(FossologyStatus.UPLOAD_FINISHED);
            library.setFossology(fossology);
            library = libraryService.saveAndFlush(library);
        } else {
            Optional<Library> libraryOptional = libraryService.findOne(library.getId());
            if (libraryOptional.isPresent()) library = libraryOptional.get();
            fossology = library.getFossology();
            fossology.setStatus(FossologyStatus.FAILURE);
            library.setFossology(fossology);
            libraryService.saveAndFlush(library);

            throw new UploadFailedException(uploadId);
        }

        String userSettings = getUserSettings();
        String folderId = getFolderId(fossologyProperties.getFolder());
        log.info("Fossology - Starting license analysis for uploadId : {}", uploadId);
        String jobId;
        try {
            jobId = startScan(folderId, uploadId, userSettings);
        } catch (IOException | ParseException e) {
            fossology.setStatus(FossologyStatus.FAILURE);
            libraryService.saveAndFlush(library);
            log.error("An error occurred while connecting to Fossology to start the license analysis : {}", e.getMessage());
            throw e;
        }

        Optional<Library> libraryOptional = libraryService.findOne(library.getId());
        if (libraryOptional.isPresent()) library = libraryOptional.get();
        fossology = library.getFossology();
        fossology.setStatus(FossologyStatus.SCAN_STARTED);
        fossology.setJobId(jobId);
        library = libraryService.saveAndFlush(library);

        do {
            try {
                Thread.sleep(5 * 1000);
                jobStatus = checkJob(jobId);
            } catch (InterruptedException e) {
                log.error("Thread sleep error : {}", e.getMessage());
            } catch (IOException | ParseException e) {
                fossology.setStatus(FossologyStatus.FAILURE);
                libraryService.saveAndFlush(library);
                log.error("An error occurred while connecting to Fossology to start the license analysis : {}", e.getMessage());
                throw e;
            }
            log.info("Fossology - Checking job status of scan for uploadId {}: {}", uploadId, jobStatus);
        } while (jobStatus.equalsIgnoreCase("Processing") || jobStatus.equalsIgnoreCase("Queued"));

        if (jobStatus.equalsIgnoreCase("Completed")) {
            libraryOptional = libraryService.findOne(library.getId());
            if (libraryOptional.isPresent()) library = libraryOptional.get();
            fossology = library.getFossology();
            fossology.setStatus(FossologyStatus.SCAN_FINISHED);
            library.setFossology(fossology);
            library = libraryService.saveAndFlush(library);
        } else {
            libraryOptional = libraryService.findOne(library.getId());
            if (libraryOptional.isPresent()) library = libraryOptional.get();
            fossology = library.getFossology();
            fossology.setStatus(FossologyStatus.FAILURE);
            library.setFossology(fossology);
            libraryService.saveAndFlush(library);

            throw new UploadFailedException(uploadId);
        }

        log.info("Finished analysis of library {} with Fossology", library.getId());
    }

    /**
     * Upload an archive to Fossology
     *
     * @param endpointLucy    open endpoint to download the archive from
     * @param folderName      folder in Fossology to upload the archive to
     * @param fileName        filename of the archive in Fossology
     * @param fileDescription description of the archive in Fossology (optional)
     */
    public String uploadToFossology(String endpointLucy, String folderName, String fileName, String fileDescription)
        throws IOException, ParseException {
        String folderId = getFolderId(folderName);
        return startRemoteDownload(endpointLucy, folderId, fileName, fileDescription);
    }

    /**
     * Initialize a connection to the Fossology API
     *
     * @param endpointAPI   open endpoint of the Fossology API
     * @param requestMethod HTTP request method (GET/POST)
     */
    public HttpURLConnection initConnection(String endpointAPI, String requestMethod) throws IOException {
        //Full URL is generated
        URL url = new URL(String.format("%s%s%s", applicationProperties.getFossology().getUrl(), FOSSOLOGY_API_PART, endpointAPI));

        //Connection is opened
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        //Basic settings are added, e.g. the request method and the authorization via Bearer token
        connection.setConnectTimeout(5 * 1000);
        connection.setReadTimeout(60 * 1000);
        connection.setRequestMethod(requestMethod);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Authorization", String.format("Bearer %s", applicationProperties.getFossology().getToken()));
        connection.setRequestProperty("Content-Type", "application/json");

        //Return of the established connection
        return connection;
    }

    /**
     * Transmit data to the Fossology API
     *
     * @param connection  established connection to the Fossology API
     * @param requestBody data to be transmitted
     */

    //Helper method to transmit variable request data via an existing connection to the Fossology API
    public static Integer writeOutputStream(HttpURLConnection connection, String requestBody) throws IOException {
        //Output stream is opened and the data written as bytes
        var outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(requestBody);
        outputStream.flush();
        outputStream.close();

        //Return of the HTTP response code of the transmission
        return connection.getResponseCode();
    }

    /**
     * Receive data from the Fossology API
     *
     * @param connection established connection to the Fossology API
     */

    //Helper method to receive the response body of an existing connection to the Fossology API
    public static String readInputStream(HttpURLConnection connection) throws IOException {
        //Input stream is opened and the data read via a buffer as long as new lines are available
        var inputStream = connection.getInputStream();
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = inputReader.readLine()) != null) {
            response.append(line);
        }
        inputStream.close();

        //Return of the transmitted response as a string
        return response.toString();
    }

    /**
     * Start the download of an archive to Fossology
     *
     * @param endpointLucy    established connection to the Fossology API
     * @param folderId        ID of the folder in Fossology to upload the archive to
     * @param fileName        filename of the archive in Fossology
     * @param fileDescription description of the archive in Fossology (optional)
     */

    //Method to signal Fossology to download a file from a specified URL to the Folder identified with the ID
    public String startRemoteDownload(String endpointLucy, String folderId, String fileName, String fileDescription)
        throws IOException, ParseException {
        //Set-up of a new connection to send the request to the Fossology API
        //with the parameters uploadType, folderId and uploadDescription (optional)
        //and the URL of the file to be downloaded and final filename in Fossology in the request body
        HttpURLConnection connection = initConnection("/uploads", "POST");
        connection.setRequestProperty("uploadType", "url");
        connection.setRequestProperty("folderId", folderId);
        connection.setRequestProperty("uploadDescription", fileDescription);
        String body = String.format("{ \"url\": \"%s\", \"name\": \"%s\" }", endpointLucy, fileName);

        //Body is transmitted via the helper method
        int responseCode = writeOutputStream(connection, body);

        //Exception is thrown if the request could not be sent successfully
        if (!(200 <= responseCode && responseCode < 300)) {
            throw new DownloadNotStartedException(connection.getResponseMessage());
        }

        //Response is received via the helper method and the connection closed
        String response = readInputStream(connection);
        connection.disconnect();

        //Response is parsed to JSON and the uploadId returned if found
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(response);
        if (jsonObject.containsKey(MESSAGE)) {
            return jsonObject.get(MESSAGE).toString();
        }

        //Exception is thrown if no uploadId was found
        throw new DownloadNotStartedException(connection.getResponseMessage());
    }

    /**
     * Call for the folder ID from the Fossology API
     *
     * @param folderName name of the folder in Fossology to upload the archive to
     */

    //Method to translate a Fossology folder name to the respective ID
    public String getFolderId(String folderName) throws IOException, ParseException {
        //Set-up of a new connection to get all folders from the Fossology API
        HttpURLConnection connection = initConnection("/folders", "GET");

        //Response is received via the helper method and the connection closed
        String response = readInputStream(connection);
        connection.disconnect();

        //Response is parsed and the folder names read from the returned JSON
        JSONArray jsonResponse = (JSONArray) new JSONParser().parse(response);
        //Iterates through all folder names
        for (Object object : jsonResponse) {
            JSONObject folder = (JSONObject) object;
            //If the requested folder name is found, its id is returned
            if (folder.containsKey("name") && folder.get("name").toString().equals(folderName) && folder.containsKey("id")) {
                return folder.get("id").toString();
            }
        }

        //Exception is thrown if the folder was not found
        throw new FolderNotFoundException(folderName, applicationProperties.getFossology().getUrl());
    }

    /**
     * Receive the user settings from the Fossology API
     */

    //Method to copy the settings (regarding analysis agents) of the user identified by the Bearer token in use
    public String getUserSettings() throws IOException, ParseException {
        //Set-up of a new connection to get the profile from the Fossology API
        HttpURLConnection connection = initConnection("/users/self", "GET");

        //Response is received via the helper method and the connection closed
        String response = readInputStream(connection);
        connection.disconnect();

        //Response is parsed and the agents read returned if found
        JSONObject jsonResponse = (JSONObject) new JSONParser().parse(response);
        try {
            return jsonResponse.get("agents").toString();
        } catch (NullPointerException nullPointerException) { //Exception is thrown if the agents were not found
            throw new UserSettingsNotAvailableException();
        }
    }

    /**
     * Trace a specific upload to Fossology
     *
     * @param uploadId ID of the upload
     */

    //Method to check the current status of an upload in Fossology
    public void checkUpload(String uploadId) throws IOException {
        //Set-up of a new connection to request the upload from the Fossology API
        //with the parameter uploadId
        HttpURLConnection connection = initConnection(String.format("/uploads/%s", uploadId), "GET");

        //Response is received via the helper method
        try {
            readInputStream(connection);
        } catch (FileNotFoundException fileNotFoundException) { //If the response can not be read, the upload is not found
            connection.disconnect();
            throw new UploadNotFoundException(uploadId, applicationProperties.getFossology().getUrl());
        } catch (IOException ioException) { //If the response can not be received, the upload is failed
            connection.disconnect();
            throw new UploadFailedException(uploadId);
        }
        //Otherwise , the upload is either in progress or finished
        connection.disconnect();
    }

    /**
     * Check the status of a job by uploadId.
     *
     * @param uploadId ID of the upload
     * @return the job status.
     */
    public String checkJobStatusByUploadId(String uploadId) throws IOException, ParseException {
        //Set-up of a new connection to request the status from the Fossology API
        //with the parameter uploadId
        HttpURLConnection connection = initConnection(String.format("/jobs?upload=%s", uploadId), "GET");

        //Response is received via the helper method
        String response;
        try {
            response = readInputStream(connection);
        } catch (IOException ioException) {
            connection.disconnect();
            throw new UploadFailedException(uploadId);
        } finally {
            connection.disconnect();
        }

        //Response is parsed to JSON and the uploadId returned if found
        JSONArray jsonResponse = (JSONArray) new JSONParser().parse(response);

        for (Object object : jsonResponse) {
            JSONObject job = (JSONObject) object;
            //If the requested job is found, its status is returned
            if (job.containsKey("status")) {
                return job.get("status").toString();
            }
        }

        throw new UploadNotFoundException(uploadId, applicationProperties.getFossology().getUrl());
    }

    /**
     * Request a (re-)scan of an existing upload
     *
     * @param uploadId ID of the upload to check
     */

    //Method to schedule a (re-)scan of an uploaded archive with the standard agents set for the respective user
    public String startScan(String folderId, String uploadId, String userSettings) throws IOException, ParseException {
        //Set-up of a new connection to send the request to the Fossology API
        //with the parameters folderId and uploadId
        //and the selected agents of the scan in the request body
        HttpURLConnection connection = initConnection("/jobs", "POST");
        connection.setRequestProperty("folderId", folderId);
        connection.setRequestProperty("uploadId", uploadId);
        String body = String.format(
            "{\"analysis\":%s%s",
            userSettings,
            ",\"decider\":{\"nomos_monk\":true,\"bulk_reused\":true,\"new_scanner\":true,\"ojo_decider\":true},\"reuse\":{\"reuse_upload\":0,\"reuse_group\":\"string\",\"reuse_main \":true,\"reuse_enhanced\":true,\"reuse_report\":true,\"reuse_copyright\":true}}"
        );

        //Body is transmitted via the helper method
        int responseCode = writeOutputStream(connection, body);

        //Exception is thrown if the request could not be sent successfully
        if (!(200 <= responseCode && responseCode < 300)) {
            throw new ScanNotStartedException(connection.getResponseMessage());
        }

        //Response is received via the helper method and the connection closed
        String response = readInputStream(connection);
        connection.disconnect();

        //Response is parsed and the jobId returned if found
        JSONObject jsonResponse = (JSONObject) new JSONParser().parse(response);
        if (jsonResponse.containsKey(MESSAGE)) {
            return jsonResponse.get(MESSAGE).toString();
        }
        //Exception is thrown if the jobId was not found
        throw new ScanNotStartedException(connection.getResponseMessage());
    }

    /**
     * Trace a specific scheduled job
     *
     * @param jobId ID of the scan job to check
     */

    //Method to check the current status of a scan in Fossology
    public String checkJob(String jobId) throws IOException, ParseException {
        //Set-up of a new connection to send the request to the Fossology API
        //with the parameter jobId
        HttpURLConnection connection = initConnection(String.format("/jobs/%s", jobId), "GET");

        //Response is received via the helper method#
        String response;
        try {
            response = readInputStream(connection);
        } catch (IOException ioException) { //If the response can not be received, the job is not found
            connection.disconnect();
            throw new JobNotFoundException(jobId, applicationProperties.getFossology().getUrl());
        }

        connection.disconnect();

        //Response is parsed and the status of the job returned if found
        JSONObject jsonResponse = (JSONObject) new JSONParser().parse(response);
        if (jsonResponse.containsKey("status")) {
            return jsonResponse.get("status").toString();
        }
        //Exception is thrown if the status was not found
        throw new JobNotFoundException(connection.getResponseMessage(), applicationProperties.getFossology().getUrl());
    }

    public Fossology.Config getConfig() {
        return new Fossology.Config(applicationProperties.getFossology().isEnabled(), applicationProperties.getFossology().getUrl());
    }

    /**
     * Custom exceptions regrading the Fossology API
     */

    static class DownloadNotStartedException extends IOException {

        public DownloadNotStartedException(String responseMessage) {
            super(String.format("Download to Fossology could not be started: %s", responseMessage));
        }
    }

    static class FolderNotFoundException extends IOException {

        public FolderNotFoundException(String folderName, String fossologyUrl) {
            super(String.format("Folder %s not found in %s", folderName, fossologyUrl));
        }
    }

    static class UploadNotFoundException extends IOException {

        public UploadNotFoundException(String uploadId, String fossologyUrl) {
            super(String.format("Upload with ID %s not found in %s", uploadId, fossologyUrl));
        }
    }

    static class UploadFailedException extends IOException {

        public UploadFailedException(String uploadId) {
            super(String.format("Upload with ID %s failed", uploadId));
        }
    }

    static class UserSettingsNotAvailableException extends IOException {

        public UserSettingsNotAvailableException() {
            super("Could not load settings for user.");
        }
    }

    static class ScanNotStartedException extends IOException {

        public ScanNotStartedException(String responseMessage) {
            super(String.format("Scan could not be started: %s", responseMessage));
        }
    }

    static class JobNotFoundException extends IOException {

        public JobNotFoundException(String uploadId, String fossologyUrl) {
            super(String.format("Scan job with ID %s not found in %s", uploadId, fossologyUrl));
        }
    }
}
