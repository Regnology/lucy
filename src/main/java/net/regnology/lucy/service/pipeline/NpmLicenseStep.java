package net.regnology.lucy.service.pipeline;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.domain.enumeration.LibraryType;
import net.regnology.lucy.service.helper.net.HttpHelper;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Execute the NpmLicenseStep if the library has the type NPM and the original license is empty.
 */
public class NpmLicenseStep implements Step<Library, Library> {

    private static final Logger log = LoggerFactory.getLogger(NpmLicenseStep.class);

    private static final String NPM_REGISTRY_BASE = "https://registry.npmjs.org/";

    @Override
    public Library process(Library input) throws StepException {
        if (!input.getType().equals(LibraryType.NPM) || !StringUtils.isBlank(input.getOriginalLicense())) {
            return input;
        }
        log.info("Searching for license of the npm library : {} - {} - {}", input.getGroupId(), input.getArtifactId(), input.getVersion());

        //Identifier for the NPM package is constructed
        final String npmIdentifier = StringUtils.isBlank(input.getGroupId())
            ? input.getArtifactId() + "/" + input.getVersion()
            : input.getGroupId() + "/" + input.getArtifactId() + "/" + input.getVersion();
        final String npmURL = NPM_REGISTRY_BASE + npmIdentifier;

        try {
            HttpResponse<String> response = HttpHelper.httpGetRequest(npmURL);

            if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                log.debug(
                    "Library [ {} - {} - {} ] not found on {}",
                    input.getGroupId(),
                    input.getArtifactId(),
                    input.getVersion(),
                    NPM_REGISTRY_BASE
                );
                return input;
            }

            //JSONparser is initialized for the reading of the string generated with the NPM command
            JSONParser npmJsonParser = new JSONParser();

            try {
                JSONObject npmResponse = (JSONObject) npmJsonParser.parse(response.body());

                Object licenseObj = npmResponse.get("license");

                String licenseValue;
                if (licenseObj == null) {
                    log.debug(
                        "License for library [ {} - {} - {} ] could not be found on {}.",
                        input.getGroupId(),
                        input.getArtifactId(),
                        input.getVersion(),
                        NPM_REGISTRY_BASE
                    );
                    return input;
                }

                if (licenseObj instanceof JSONObject) {
                    JSONObject license = (JSONObject) licenseObj;
                    licenseValue = (String) license.get("type");
                } else {
                    licenseValue = licenseObj.toString();
                }

                if (!StringUtils.isBlank(licenseValue)) {
                    licenseValue = StringUtils.strip(licenseValue, "()");
                    input.setOriginalLicense(licenseValue);
                    log.debug(
                        "License found for library [ {} - {} - {} ] : {}",
                        input.getGroupId(),
                        input.getArtifactId(),
                        input.getVersion(),
                        licenseValue
                    );
                }
            } catch (ParseException e) {
                log.error(
                    "License for library [ {} - {} - {} ] could not be scraped from {} : {}",
                    input.getGroupId(),
                    input.getArtifactId(),
                    input.getVersion(),
                    NPM_REGISTRY_BASE,
                    e.getMessage()
                );
            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            log.info(
                "License for library [ {} - {} - {} ] could not be scraped from {} : {}",
                input.getGroupId(),
                input.getArtifactId(),
                input.getVersion(),
                NPM_REGISTRY_BASE,
                e.getMessage()
            );
        }

        return input;
    }
}
