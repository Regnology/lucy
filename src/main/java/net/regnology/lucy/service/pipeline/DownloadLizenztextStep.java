package net.regnology.lucy.service.pipeline;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Pattern;
import net.regnology.lucy.config.Constants;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.service.helper.net.HttpHelper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadLizenztextStep implements Step<Library, Library> {

    private final Logger log = LoggerFactory.getLogger(DownloadLizenztextStep.class);

    @Override
    public Library process(Library input) throws StepException {
        if (
            !StringUtils.isBlank(input.getLicenseUrl()) &&
            !input.getLicenseUrl().equals(Constants.NO_URL) &&
            StringUtils.isBlank(input.getLicenseText())
        ) {
            StringBuilder licenseTextBuilder = new StringBuilder();
            boolean multipleLicenseTexts = false;
            final String textSeparator = Constants.LICENSE_TEXT_SEPARATOR;

            String[] urls = input.getLicenseUrl().split(",");
            if (urls.length == 0) urls = new String[] { input.getLicenseUrl() };

            for (String url : urls) {
                url = url.trim();
                try {
                    String licenseText = downloadLicenseText(url);

                    if (!licenseText.isEmpty()) {
                        if (multipleLicenseTexts) {
                            licenseTextBuilder.append(textSeparator).append(licenseText);
                        } else {
                            licenseTextBuilder.append(licenseText);
                            multipleLicenseTexts = true;
                        }
                    }
                } catch (URISyntaxException e) {
                    log.info(
                        "License text URL is not valid for Library [ {} - {} ] : {}",
                        input.getId(),
                        input.getLicenseUrl(),
                        e.getMessage()
                    );
                } catch (IOException | InterruptedException e) {
                    log.info("License text cannot be downloaded for Library [ {} ] : {}", input.getId(), e.getMessage());
                }
            }
            input.setLicenseText(licenseTextBuilder.toString());
        }
        return input;
    }

    /**
     * Downloads the HTML page from an URL.
     *
     * @param url URL to the HTML page
     * @return Body of the HTML page
     * @throws URISyntaxException if the url is not valid
     * @throws IOException if an I/O error occurs while downloading the url
     * @throws InterruptedException if the download gets interrupted
     */
    private String downloadLicenseText(String url) throws URISyntaxException, IOException, InterruptedException {
        log.debug("Downloading URL : {}", url);

        if (url.contains("github.com") && url.contains("/blob/")) {
            url = url.replace("github.com", "raw.githubusercontent.com").replace("raw.raw.", "raw.").replace("/blob/", "/");
        }

        String licenseText = HttpHelper.httpGetRequest(url).body();

        if (!isHTML(licenseText)) {
            licenseText = "<pre>\n" + StringEscapeUtils.escapeHtml4(licenseText.stripTrailing()) + "\n</pre>";
        }

        Document document = Jsoup.parse(licenseText);

        return document.body().html();
    }

    /**
     * Checks if a text is a HTML page. Searches for the &lt;!DOCTYPE&gt; or &lt;html&gt; tag.
     * @param text text to check
     * @return true if it is a HTML page, otherwise false
     */
    private static boolean isHTML(String text) {
        final Pattern pattern = Pattern.compile("<!doctype html(>| [^>]*>)|<html(>| [^>]*>)", Pattern.CASE_INSENSITIVE);

        return pattern.matcher(text).find();
    }
}
