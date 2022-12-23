package net.regnology.lucy.service.helper.net;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

public class HttpHelper {

    private static final Logger log = LoggerFactory.getLogger(HttpHelper.class);
    private static final int CLIENT_TIMEOUT = 60;

    public static final int REQUEST_TIMEOUT = 30;

    /**
     * Transfer a file to a specific URL.
     *
     * @param url      target URL to which a file should be transferred
     * @param data     file that will be transferred. If data is null, then an empty file will be transferred
     * @param fileName this is the name under which the file is saved at the target
     */
    public static void transferToTarget(String url, byte[] data, String fileName) {
        log.debug("Transfer file to target : {} -> {}", fileName, url);

        // Create empty byte array for data if it is null, otherwise a NullPointerException would be thrown
        if (data == null) {
            data = new byte[] {};
        }

        url = url.endsWith("/") ? url : url + "/";

        HttpClient client = createDefaultHttpClient();

        try {
            HttpRequest request = HttpRequest
                .newBuilder()
                .timeout(Duration.ofSeconds(REQUEST_TIMEOUT))
                .uri(URI.create(url + fileName))
                .PUT(HttpRequest.BodyPublishers.ofByteArray(data))
                .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.error("Can't transfer file to URL : {} : {}", url, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("URL contains illegal characters : {} : {}", url, e.getMessage());
        }
    }

    /**
     * Transfer a ZIP file to a specific URL with credentials.
     *
     * @param remotePlatform URL of the target where the file will be transferred
     * @param user           username
     * @param password       password
     * @param zipFile        file to transfer
     * @throws IOException if the ZIP cannot be transferred to the remote platform
     */
    public static void transferZipToTargetWithCredentials(String remotePlatform, String user, String password, java.io.File zipFile)
        throws IOException {
        var fullUrl = remotePlatform + zipFile.getName();
        var url = new URL(fullUrl);

        //Parses the authentication header (base64-encoded)
        String auth = user + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authorizationHeader = "Basic " + new String(encodedAuth);

        //Creates new HTTP connection with chunked communication
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(CLIENT_TIMEOUT * 1000);
        connection.setReadTimeout(60 * 1000);
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setRequestProperty("Authorization", authorizationHeader);
        connection.setChunkedStreamingMode(65536);
        connection.setRequestProperty("Content-Type", "application/zip");
        connection.connect();

        //File content is read from the input-stream and written via the output-stream
        int status;
        byte[] bufferedByte;
        try (OutputStream output = connection.getOutputStream(); InputStream input = new FileInputStream(zipFile)) {
            do {
                bufferedByte = new byte[65536];
                status = input.read(bufferedByte);
                //While-Do loop creates unnecessary padding which makes the ZIP file unreadable by the Windows Explorer
                //If bufferedByte is completely empty, the loop is broken
                if (isAllEmpty(bufferedByte)) {
                    break;
                }
                output.write(bufferedByte);
            } while (status != -1);
            output.flush();
        }
    }

    /**
     * Download a file to the local storage system.
     *
     * @param source      URL to the source that will be downloaded
     * @param destination path to the file on the local system
     * @throws IOException if no connection can be established or the source cannot be saved
     */
    public static void downloadResource(String source, java.io.File destination) throws IOException {
        var url = new URL(source);

        //Creates new HTTP connection
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        int responseCode = huc.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            //Input stream the file is read into
            var inputStream = huc.getInputStream();

            //Output stream the file is saved into
            try (var outputStream = new FileOutputStream(destination)) {
                int bytesRead;
                var buffer = new byte[1024];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            inputStream.close();
            huc.disconnect();
        }
    }

    /**
     * Download a file to the local storage system. This method is more efficient because
     * depending on the underlying operating system, the data can be transferred directly from the filesystem cache
     * to our file without copying any bytes into the application memory.
     *
     * @param source      URL to the source that will be downloaded
     * @param destination folder where the source will be saved.
     *                    File will have the same name as the file on the source side
     * @throws IOException if some I/O error occurs while downloading or saving to the file system
     */
    public static void downloadResource(String source, String destination) throws IOException {
        try (ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(source).openStream())) {
            // Add trailing slash if missing
            destination = destination.endsWith("/") ? destination : destination + "/";

            FileOutputStream fileOutputStream = new FileOutputStream(destination + FilenameUtils.getName(source));
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

            fileOutputStream.close();
        }
    }

    /**
     * Download a file into memory as an input stream.
     *
     * @param source URL to the source that will be downloaded
     * @return the file as an input stream
     * @throws IOException if no connection can be established or other connection problems occur
     */
    public static InputStream downloadResource(String source) throws IOException {
        URL url = new URL(source);
        return new BufferedInputStream(url.openStream());
    }

    public static Pair<HttpURLConnection, InputStream> downloadResource2(String source) throws IOException {
        URL url = new URL(source);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        return Pair.of(urlConnection, new BufferedInputStream(urlConnection.getInputStream()));
    }

    /**
     * Download a file with basic authentication into memory as an input stream.
     *
     * @param source URL to the source that will be downloaded
     * @return the file as an input stream
     * @throws IOException if no connection can be established or other connection problems occur
     */
    public static InputStream downloadResourceWithAuthentication(String source, String username, String password) throws IOException {
        URL url = new URL(source);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Authorization", getBasicAuthenticationHeader(username, password));

        return new BufferedInputStream(urlConnection.getInputStream());
    }

    /**
     * Send a HEAD request to the specified URI.
     * Creates a new HTTP client.
     *
     * @param uri the URI to send the GET request
     * @return a HTTP response
     * @throws URISyntaxException   if the given string violates RFC 2396
     * @throws IOException          if an I/O error occurs when sending or receiving
     * @throws InterruptedException if the GET request gets interrupted
     */
    public static HttpResponse<String> httpHeadRequest(String uri) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest
            .newBuilder()
            .timeout(Duration.ofSeconds(REQUEST_TIMEOUT))
            .uri(new URI(uri))
            .method("HEAD", HttpRequest.BodyPublishers.noBody())
            .build();

        return createDefaultHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Send a HEAD request to the specified URI with an authorization.
     * Creates a new HTTP client.
     *
     * @param uri the URI to send the GET request
     * @return a HTTP response
     * @throws URISyntaxException   if the given string violates RFC 2396
     * @throws IOException          if an I/O error occurs when sending or receiving
     * @throws InterruptedException if the GET request gets interrupted
     */
    public static HttpResponse<String> httpHeadRequestWithAuthorization(String uri, String username, String password)
        throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest
            .newBuilder()
            .timeout(Duration.ofSeconds(REQUEST_TIMEOUT))
            .uri(new URI(uri))
            .header("Authorization", getBasicAuthenticationHeader(username, password))
            .method("HEAD", HttpRequest.BodyPublishers.noBody())
            .build();

        return createDefaultHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Send a GET request to the specified URI. Creates a new HTTP client.
     *
     * @param uri the URI to send the GET request
     * @return a HTTP response
     * @throws URISyntaxException   if the given string violates RFC 2396
     * @throws IOException          if an I/O error occurs when sending or receiving
     * @throws InterruptedException if the GET request gets interrupted
     */
    public static HttpResponse<String> httpGetRequest(String uri) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().timeout(Duration.ofSeconds(REQUEST_TIMEOUT)).uri(new URI(uri)).GET().build();

        return createDefaultHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Send a GET request to the specified URI with an authorization.
     * Creates a new HTTP client.
     *
     * @param uri the URI to send the GET request
     * @return a HTTP response
     * @throws URISyntaxException   if the given string violates RFC 2396
     * @throws IOException          if an I/O error occurs when sending or receiving
     * @throws InterruptedException if the GET request gets interrupted
     */
    public static HttpResponse<String> httpGetRequestWithAuthorization(String uri, String username, String password)
        throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest
            .newBuilder()
            .timeout(Duration.ofSeconds(REQUEST_TIMEOUT))
            .uri(new URI(uri))
            .GET()
            .header("Content-Type", "application/json")
            .header("Authorization", getBasicAuthenticationHeader(username, password))
            .build();

        return createDefaultHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Send a GET request asynchronously to the specified URI. Creates a new HTTP client
     *
     * @param uri             the URI to send the GET request
     * @param responseHandler handler for the response
     * @return a CompletableFuture<Void>
     * @throws URISyntaxException if the given uri violates RFC 2396
     */
    public static CompletableFuture<Void> asyncHttpGetRequest(String uri, Consumer<? super HttpResponse<String>> responseHandler)
        throws URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().timeout(Duration.ofSeconds(REQUEST_TIMEOUT)).uri(new URI(uri)).GET().build();

        return createDefaultHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(responseHandler);
    }

    /**
     * Send a GET request to the specified URI. The passed HTTP client will be used. This way can be more efficient
     * if multiple requests are send over the same client.
     *
     * @param uri    the URI to send the GET request
     * @param client a HTTP client
     * @return a HTTP response
     * @throws URISyntaxException   if the given string violates RFC 2396
     * @throws IOException          if an I/O error occurs when sending or receiving
     * @throws InterruptedException if the GET request is interrupted
     */
    public static HttpResponse<String> httpGetRequest(String uri, HttpClient client)
        throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().timeout(Duration.ofSeconds(REQUEST_TIMEOUT)).uri(new URI(uri)).GET().build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Create a default HTTP client with the following configuration: <br>
     * Connection timeout: <b>60 seconds</b><br>
     * Follow redirects: <b>ALWAYS</b>
     *
     * @return a HTTP client
     */
    public static HttpClient createDefaultHttpClient() {
        return HttpClient
            .newBuilder()
            .connectTimeout(Duration.ofSeconds(CLIENT_TIMEOUT))
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();
    }

    /**
     * Create a default HTTP client with a custom connection timeout and the following configuration: <br>
     * Follow redirects: <b>ALWAYS</b>
     *
     * @param connectionTimeout timeout in seconds
     * @return a HTTP client
     */
    public static HttpClient createDefaultHttpClient(int connectionTimeout) {
        return HttpClient
            .newBuilder()
            .connectTimeout(Duration.ofSeconds(connectionTimeout))
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();
    }

    protected static String getBasicAuthenticationHeader(String username, String password) {
        String userPass = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(userPass.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Checks if all entries in a byte array are zero
     *
     * @param bytes byte array
     * @return true if all bytes are zero, otherwise false
     */
    private static boolean isAllEmpty(byte[] bytes) {
        for (var entry : bytes) {
            if (entry != 0) {
                return false;
            }
        }
        return true;
    }
}
