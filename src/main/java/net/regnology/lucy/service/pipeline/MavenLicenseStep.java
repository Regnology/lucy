package net.regnology.lucy.service.pipeline;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.StringJoiner;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.domain.enumeration.LibraryType;
import net.regnology.lucy.service.helper.net.HttpHelper;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Execute the MavenLicenseStep if the library has the type MAVEN and the original license is empty.
 */
public class MavenLicenseStep implements Step<Library, Library> {

    private static final Logger log = LoggerFactory.getLogger(MavenLicenseStep.class);

    private static final String MAVEN_CENTRAL_BASE = "https://central.sonatype.dev/artifact";

    @Override
    public Library process(Library input) throws StepException {
        if (!input.getType().equals(LibraryType.MAVEN) || !StringUtils.isBlank(input.getOriginalLicense())) {
            return input;
        }

        log.info(
            "Searching for the license of the Maven library : {} - {} - {}",
            input.getGroupId(),
            input.getArtifactId(),
            input.getVersion()
        );
        try {
            input.setOriginalLicense(scrapeCentralSonatype(input));
        } catch (IOException | InterruptedException | URISyntaxException e) {
            log.info(
                "The license for library [ {} - {} - {} ] could not be scraped from {} : {}",
                input.getGroupId(),
                input.getArtifactId(),
                input.getVersion(),
                MAVEN_CENTRAL_BASE,
                e.getMessage()
            );
        }

        return input;
    }

    /**
     * Scrapes the license information from the central.sonatype.dev page.
     *
     * @param library Library entity
     * @return The license information from the central.sonatype.dev page (can be empty).
     * @throws IOException          if an I/O error occurs when sending or receiving to central.sonatype.dev
     * @throws InterruptedException if the request to central.sonatype.dev is interrupted
     * @throws URISyntaxException   If the URI for the request to central.sonatype.dev, which is constructed from groupId,
     *                              artifactId and version, violates RFC 2396
     */
    private String scrapeCentralSonatype(Library library) throws IOException, InterruptedException, URISyntaxException {
        final String pathToArtifact = MAVEN_CENTRAL_BASE + "/{0}/{1}/{2}";

        HttpResponse<String> response = HttpHelper.httpGetRequest(
            MessageFormat.format(pathToArtifact, library.getGroupId(), library.getArtifactId(), library.getVersion())
        );

        Document sonatypePageDocument = Jsoup.parse(response.body());

        Elements licenseElements = sonatypePageDocument.select(".Overview_licensesContainer__gj_BZ > li");
        StringJoiner license = new StringJoiner(" / ");
        for (Element element : licenseElements) {
            license.add(element.ownText());
        }
        if (license.length() > 0) log.debug(
            "License found for library [ {} - {} - {} ] : {}",
            library.getGroupId(),
            library.getArtifactId(),
            library.getVersion(),
            license
        );

        return license.toString();
    }

    /**
     * Scrapes the license information from the mvnrepository.com page.
     * It uses Selenium and the Google Chrome driver to request the page because it is protected by a Cloudflare server.<br>
     * NOTICE: It's not working in Docker as long the 'headless' option is false but if it's true the Cloudflare
     * protection prevents the request. It looks like that the 'headless' option is not working in a cli-only environment.<br>
     * Needs the chrome driver saved under <i>/app/cache/selenium/chromedriver/chromedriver</i>
     *
     * @param library Library entity
     * @return The license information from the mvnrepository.com page (can be empty).
     */
    private String scrapeMvnrepository(Library library) {
        final String base_url = "https://mvnrepository.com/artifact";
        final String artifactPath = "/{0}/{1}/{2}";

        System.setProperty("webdriver.chrome.driver", "/app/cache/selenium/chromedriver/chromedriver");
        //WebDriverManager.chromedriver().cachePath("/app/cache/").resolutionCachePath("/app/cache/").setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setHeadless(true);
        chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
        chromeOptions.addArguments("--enable-javascript");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--disable-browser-side-navigation");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("enable-automation");
        chromeOptions.addArguments("start-maximized");
        //chromeOptions.addArguments("--verbose");
        chromeOptions.addArguments("disable-gpu");
        ChromeDriver driver = new ChromeDriver(chromeOptions);
        driver.get(base_url + MessageFormat.format(artifactPath, library.getGroupId(), library.getArtifactId(), library.getVersion()));

        Document mavenPageDocument = Jsoup.parse(driver.getPageSource());
        Element licenseRow = mavenPageDocument.selectFirst("main > .content > table tr");

        StringJoiner license = new StringJoiner(" / ");
        if (licenseRow != null) {
            Elements licenseTd = licenseRow.getElementsByClass("b lic");

            for (Element element : licenseTd) {
                license.add(element.ownText());
            }
        } else {
            log.info(
                "The license field could not be found on mvnrepository.com. This may indicate that the design of the website has changed."
            );
        }

        driver.quit();

        return license.toString();
    }
}
