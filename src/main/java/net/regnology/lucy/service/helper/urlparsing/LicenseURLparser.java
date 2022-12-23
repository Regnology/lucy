package net.regnology.lucy.service.helper.urlparsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.regnology.lucy.config.Constants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenseURLparser implements URLparser {

    private final Logger log = LoggerFactory.getLogger(LicenseURLparser.class);

    //Constants
    private static final String GITHUB = "github";
    private static final String VERSION = "version";
    private static final String[] FILENAMES = new String[] {
        "LICENSE",
        "LICENSE.md",
        "LICENSE.txt",
        "COPYRIGHT",
        "License",
        "license",
        "License.txt",
        "license.txt",
        "License.md",
        "license.md",
        "LICENSE.rst",
        "License.rst",
        "COPYING",
        "Copyright.txt",
        "Copyright.md",
        "copyright.txt",
        "copyright.md",
    };
    private static final URLparserHelper helper = new URLparserHelper();

    //Map containing the logic for each repository host with the host's name as key
    private static final Map<String, ParserCommand> commands = new HashMap<>();

    /**
     * Method initializing the map with the commands (as lambdas) for each host repository
     */
    public void initCommands() {
        //Apache packages are redirected to Maven, almost all of them are hosted there
        commands.put("apache", (groupID, artifactID, version) -> commands.get("maven").parse(groupID, artifactID, version));

        //GitHub packages have a number of special properties and are "outsourced"
        commands.put(
            "github",
            (groupID, artifactID, version) -> {
                String url = "https://github.com/" + groupID + "/" + artifactID;
                if (helper.checkURL(url)) {
                    return githubURL(url, version);
                }
                return Constants.NO_URL;
            }
        );

        //Golang packages are redirected to GitHub, as almost all of them are hosted there
        commands.put("golang", (groupID, artifactID, version) -> commands.get("github").parse(groupID, artifactID, version));

        //Maven packages do not bring any license information,
        //It's checked if the package is available on GitHub
        commands.put("maven", (groupID, artifactID, version) -> commands.get("github").parse(groupID, artifactID, version));

        //NPM packages can be traced with the NPM command line tool by running "npm view PACKAGE",
        //returning information about the package, such as repository or homepage
        //Almost all of them are hosted via GitHub
        commands.put(
            "npm",
            (groupID, artifactID, version) -> {
                var url = "";

                //Identifier for the NPM package is constructed
                String npmIdentifier = groupID.equals("") ? artifactID + "/" + version : groupID + "/" + artifactID + "/" + version;
                String npmURL = "https://registry.npmjs.org/" + npmIdentifier;

                //JSON data is requested from the NPM API
                var npmBody = helper.downloadFileToString(npmURL);

                //JSONparser is initialized for the reading of the string generated with the NPM command
                var jsonparser = new JSONParser();
                JSONObject npmResponse;
                npmResponse = (JSONObject) jsonparser.parse(npmBody);

                //If the field repository is defined, the url of this repository is used
                //Else, the homepage field is used instead (if available)
                JSONObject repository = (JSONObject) npmResponse.get("repository");
                url = repository == null ? (String) npmResponse.get("homepage") : (String) repository.get("url");

                //If the package is hosted via GitHub, the respective method is called
                if (url != null && url.contains(GITHUB)) {
                    return githubURL(url, version);
                } else if (url != null) {
                    return url; // Return url even if it not a github URL
                }
                return Constants.NO_URL;
            }
        );

        //Nuget packages can be traced via the official website using a scraper
        //Almost all of them are hosted via GitHub
        commands.put(
            "nuget",
            (groupID, artifactID, version) -> {
                //URL is parsed from the website
                String url = helper.scrapWebsite(
                    "https://www.nuget.org/packages/" + artifactID,
                    "a[data-track=outbound-repository-url]",
                    "Source repository",
                    "href"
                );

                //If the package is hosted via GitHub, the respective method is called
                if (url != null && url.contains(GITHUB)) {
                    return githubURL(url, version);
                } else if (url != null) {
                    return url; // Return url even if it not a github URL
                }
                return Constants.NO_URL;
            }
        );

        //PyPI packages can be traced via the official website using a scraper
        //Almost all of them are hosted via GitHub
        commands.put(
            "pypi",
            (groupID, artifactID, version) -> {
                //URL is parsed from the website
                String rawURL = helper.scrapWebsite(
                    "https://pypi.org/project/" + artifactID,
                    "div[class^=\"github-repo-info\"]",
                    "GitHub statistics:",
                    "data-url"
                );

                //If the package is hosted via GitHub, the respective method is called
                if (rawURL != null && rawURL.contains(GITHUB)) {
                    String[] urlData = rawURL.replaceAll(".*github.com/repos/", "").split("/");
                    String url = "https://github.com/" + urlData[0] + "/" + urlData[1];
                    return githubURL(url, version);
                } else if (rawURL != null) {
                    return rawURL; // Return url even if it not a github URL
                }
                return Constants.NO_URL;
            }
        );
    }

    /**
     * Method calling the respective command with the necessary parameters
     *
     * @param type
     * @param groupID
     * @param artifactID
     * @param version
     * @return
     * @throws IOException
     * @throws ParseException
     * @throws ClassCastException
     */
    public String getURL(String type, String groupID, String artifactID, String version)
        throws IOException, ParseException, ClassCastException, InterruptedException {
        if (!commands.containsKey(type)) return null;

        return commands.get(type).parse(groupID, artifactID, version);
    }

    /**
     * Overload; Method parsing the groupID, artifactID and version from the PURL
     *
     * @param purl
     * @return
     * @throws IOException
     * @throws ParseException
     * @throws ClassCastException
     */
    public String getURL(String purl) throws IOException, ParseException, ClassCastException, InterruptedException {
        String group;
        String artifact;

        //"pkg:" and any type information (e.g. "?type=jar") are removed
        String cleanPurl = purl.replaceAll("^pkg:", "").replaceAll("\\?.*", "");

        // Split purl into [Type/GroupId/ArtifactId] and [version]
        String[] splittedPurl = cleanPurl.split("@");

        if (splittedPurl.length < 2) {
            return null;
        }

        //Version is parsed from the PURL
        String version = splittedPurl[1];

        //Version is removed from the PURL and the PURL is splitted (by "/")
        String[] purlData = splittedPurl[0].split("/");

        //Type is the first identifier
        String type = purlData[0];

        //Depending on the length of the PURL, the groupID and artifactID are parsed
        switch (purlData.length) {
            case 2:
                group = "";
                artifact = purlData[1];
                break;
            case 3:
                group = purlData[1];
                artifact = purlData[2];
                break;
            case 4:
                group = purlData[2];
                artifact = purlData[3];
                break;
            default:
                return "Could not read purl";
        }
        return getURL(type, group, artifact, version);
    }

    /**
     * Method handling GitHub packages
     *
     * @param url
     * @param version
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public String githubURL(String url, String version) throws IOException, ParseException, InterruptedException {
        String correctVersion = null;

        //Versioning on GitHub can differ: prefixes are used or trailing zeros are dropped
        List<String> endings = new ArrayList<>();
        endings.add(version);
        endings.add("v" + version);
        endings.add("v." + version);
        endings.add(VERSION + version);
        if (version.matches(".*0$")) {
            String shortversion = version.replaceAll("\\.0$", "");
            endings.add(shortversion);
            endings.add("v" + shortversion);
            endings.add("v." + shortversion);
            endings.add(VERSION + shortversion);
        }

        //Protocol is set to https and eventual .git TLD is dropped
        String baseURL = url.replaceAll(".*github\\.com", "https://github\\.com").replace(".git", "");

        //Each versioning is tested
        for (String ending : endings) {
            String treeURL = baseURL + "/tree/" + ending;
            if (helper.checkURL(treeURL)) {
                correctVersion = ending;
                break;
            }
        }

        //Each filename is tested for the correct versioning
        if (correctVersion != null) {
            String repoURL = baseURL + "/blob/" + correctVersion;
            for (String filename : FILENAMES) {
                String gitHubURL = repoURL + "/" + filename;
                if (helper.checkURL(gitHubURL)) {
                    return gitHubURL;
                }
            }
        }
        log.debug("License could not be found for a specific version");

        //If the version is not available as tag, the default branch may correspond to the this version
        //If a default branch with the requested version is found, it is used instead of the versioned URL
        String defaultBranch = helper.checkLatest(baseURL, version);
        if (defaultBranch == null) {
            return Constants.NO_URL;
        }

        String defaultURL = baseURL + "/blob/" + defaultBranch;
        log.debug("DefaultURL : {}", defaultURL);
        //Each filename is tested for the default branch
        for (String filename : FILENAMES) {
            String gitHubURL = defaultURL + "/" + filename;
            if (helper.checkURL(gitHubURL)) {
                return gitHubURL;
            }
        }
        return Constants.NO_URL;
    }
}
