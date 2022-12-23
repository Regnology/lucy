package net.regnology.lucy.web.rest.v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import net.regnology.lucy.domain.Fossology;
import net.regnology.lucy.service.FossologyService;
import net.regnology.lucy.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for communication with Fossology.
 */
@RestController
@RequestMapping("/api/v1")
public class FossologyResource {

    private final Logger log = LoggerFactory.getLogger(FossologyResource.class);

    private static final String ENTITY_NAME = "fossology";

    private final FossologyService fossologyService;

    public FossologyResource(FossologyService fossologyService) {
        this.fossologyService = fossologyService;
    }

    /**
     * {@code GET  /fossology/scan} : Provide Fossology a file to download that will be scanned.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the file,
     * or with status {@code 400 (Bad Request)} if the file is not available.
     */
    @GetMapping("/fossology/scan")
    public ResponseEntity<Resource> provideFileUrl(@RequestParam(value = "filename") String filename) {
        log.debug("REST request to provide Fossology a file to scan");

        final String basePath = "classpath:filesArchive/fossology/";

        try {
            File testFile = ResourceUtils.getFile(basePath + filename);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(testFile));

            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

            return ResponseEntity
                .ok()
                .headers(header)
                .contentLength(testFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
        } catch (FileNotFoundException e) {
            throw new BadRequestAlertException("Cannot find file", ENTITY_NAME, "filenotfound");
        }
    }

    /**
     * {@code GET  /fossology/config} : Provide the configuration in Lucy of Fossology.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body configuration.
     */
    @GetMapping("/fossology/config")
    public ResponseEntity<Fossology.Config> getConfig() {
        log.debug("REST request to get the config for Fossology");
        return ResponseEntity.ok(fossologyService.getConfig());
    }
}
