package net.regnology.lucy.service.helper.urlparsing;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import net.regnology.lucy.service.exceptions.GithubRateLimitException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URLparserHelper {

    public enum paserType {
        LICENSE,
        SOURCE,
    }

    private final Logger log = LoggerFactory.getLogger(URLparserHelper.class);

    //Constants
    SourceURLparser sourceParser = new SourceURLparser();
    LicenseURLparser licenseParser = new LicenseURLparser();
    HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).followRedirects(HttpClient.Redirect.ALWAYS).build();

    public URLparserHelper() {}

    /**
     * Method updating an existing GitHub URL
     *
     * @param url
     * @param version
     * @param type
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public String updateGitHubURL(String url, String version, paserType type) throws IOException, ParseException, InterruptedException {
        String[] urlData = url.replace("https://", "").replaceAll("#.*", "").split("/");

        if (urlData.length >= 3) {
            String newURL = "https://github.com/" + urlData[1] + "/" + urlData[2];

            switch (type) {
                case LICENSE:
                    return licenseParser.githubURL(newURL, version);
                case SOURCE:
                    return sourceParser.githubURL(newURL, version);
            }
        }

        return url;
    }

    /**
     * Method checking if an URL is reachable
     *
     * @param url URL
     * @return True if response code is 200. Otherwise false.
     */
    public boolean checkURL(String url) throws IOException {
        if (url == null) return false;

        Integer responseCode = 1;
        var parsedURL = new URL(url);
        HttpURLConnection huc = (HttpURLConnection) parsedURL.openConnection();
        huc.setRequestMethod("HEAD");

        responseCode = huc.getResponseCode();

        return responseCode.equals(200);
    }

    /**
     * Method returning the content of a website as String
     *
     * @param url
     * @return
     */
    public String downloadFileToString(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().timeout(Duration.ofSeconds(2)).uri(URI.create(url)).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    /**
     * Method scraping a website with JSOUP for a single piece of data
     *
     * @param url
     * @param target
     * @param check
     * @param attribute
     * @return
     * @throws IOException
     */
    public String scrapWebsite(String url, String target, String check, String attribute) throws IOException {
        String information = null;
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select(target);
        for (Element link : links) {
            String text = link.ownText();
            if (text.matches(check)) {
                information = link.attr(attribute);
            }
        }
        return information;
    }

    /**
     * Method reading the current version from the package.json file for the default branch of a GitHub repository,
     * comparing it to the requested version
     *
     * @param url
     * @param version
     * @return
     * @throws IOException
     * @throws ParseException
     */
    protected String checkLatest(String url, String version) throws IOException, ParseException, InterruptedException {
        //JSONparser is initialized for the reading of the package.json file
        var jsonparser = new JSONParser();

        //Default branch is retrieved
        String mainBranch = getDefaultBranch(url, jsonparser);

        String jsonURL = url.replace("github.com", "raw.githubusercontent.com") + "/" + mainBranch + "/package.json";
        if (checkURL(jsonURL)) {
            //Package.json file is stored in a string (if available)
            var githubBody = downloadFileToString(jsonURL);

            //Version field is read from the package.json string
            //If the versions match, the main branch is returned
            JSONObject packageResponse;
            packageResponse = (JSONObject) jsonparser.parse(githubBody);
            String latestversion = (String) packageResponse.get("version");
            if (latestversion != null && version.matches(latestversion)) {
                return mainBranch;
            }
        }
        return null;
    }

    /**
     * Method retrieving the default branch for a GitHub repository via the API
     * Limit of 60 requests per hour
     *
     * @param url
     * @param jsonparser
     * @return
     * @throws IOException
     * @throws ParseException
     */
    protected String getDefaultBranch(String url, JSONParser jsonparser) throws IOException, ParseException, InterruptedException {
        String apiURL = url.replace("github.com", "api.github.com/repos");
        var githubBody = "";
        try {
            //GitHub API response is stored in a string (if available)
            githubBody = downloadFileToString(apiURL);
        } catch (IOException | InterruptedException e) {
            String error = e.getMessage(); // Server returned HTTP response code: 403 for URL:
            if (error.contains("response code: 403")) {
                throw new GithubRateLimitException();
            } else {
                throw e;
            }
        }

        JSONObject githubResponse;
        githubResponse = (JSONObject) jsonparser.parse(githubBody);

        return (String) githubResponse.get("default_branch");
    }
}
