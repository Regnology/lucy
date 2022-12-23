package net.regnology.lucy.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import net.regnology.lucy.domain.GenericLicenseUrl;
import net.regnology.lucy.repository.GenericLicenseUrlRepository;
import net.regnology.lucy.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link GenericLicenseUrl}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class GenericLicenseUrlResource {

    private final Logger log = LoggerFactory.getLogger(GenericLicenseUrlResource.class);

    private static final String ENTITY_NAME = "genericLicenseUrl";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GenericLicenseUrlRepository genericLicenseUrlRepository;

    public GenericLicenseUrlResource(GenericLicenseUrlRepository genericLicenseUrlRepository) {
        this.genericLicenseUrlRepository = genericLicenseUrlRepository;
    }

    /**
     * {@code POST  /generic-license-urls} : Create a new genericLicenseUrl.
     *
     * @param genericLicenseUrl the genericLicenseUrl to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new genericLicenseUrl, or with status {@code 400 (Bad Request)} if the genericLicenseUrl has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/generic-license-urls")
    public ResponseEntity<GenericLicenseUrl> createGenericLicenseUrl(@Valid @RequestBody GenericLicenseUrl genericLicenseUrl)
        throws URISyntaxException {
        log.debug("REST request to save GenericLicenseUrl : {}", genericLicenseUrl);
        if (genericLicenseUrl.getId() != null) {
            throw new BadRequestAlertException("A new genericLicenseUrl cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GenericLicenseUrl result = genericLicenseUrlRepository.save(genericLicenseUrl);
        return ResponseEntity
            .created(new URI("/api/generic-license-urls/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /generic-license-urls/:id} : Updates an existing genericLicenseUrl.
     *
     * @param id the id of the genericLicenseUrl to save.
     * @param genericLicenseUrl the genericLicenseUrl to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated genericLicenseUrl,
     * or with status {@code 400 (Bad Request)} if the genericLicenseUrl is not valid,
     * or with status {@code 500 (Internal Server Error)} if the genericLicenseUrl couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/generic-license-urls/{id}")
    public ResponseEntity<GenericLicenseUrl> updateGenericLicenseUrl(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GenericLicenseUrl genericLicenseUrl
    ) throws URISyntaxException {
        log.debug("REST request to update GenericLicenseUrl : {}, {}", id, genericLicenseUrl);
        if (genericLicenseUrl.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, genericLicenseUrl.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!genericLicenseUrlRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        GenericLicenseUrl result = genericLicenseUrlRepository.save(genericLicenseUrl);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, genericLicenseUrl.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /generic-license-urls/:id} : Partial updates given fields of an existing genericLicenseUrl, field will ignore if it is null
     *
     * @param id the id of the genericLicenseUrl to save.
     * @param genericLicenseUrl the genericLicenseUrl to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated genericLicenseUrl,
     * or with status {@code 400 (Bad Request)} if the genericLicenseUrl is not valid,
     * or with status {@code 404 (Not Found)} if the genericLicenseUrl is not found,
     * or with status {@code 500 (Internal Server Error)} if the genericLicenseUrl couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/generic-license-urls/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GenericLicenseUrl> partialUpdateGenericLicenseUrl(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GenericLicenseUrl genericLicenseUrl
    ) throws URISyntaxException {
        log.debug("REST request to partial update GenericLicenseUrl partially : {}, {}", id, genericLicenseUrl);
        if (genericLicenseUrl.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, genericLicenseUrl.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!genericLicenseUrlRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GenericLicenseUrl> result = genericLicenseUrlRepository
            .findById(genericLicenseUrl.getId())
            .map(existingGenericLicenseUrl -> {
                if (genericLicenseUrl.getUrl() != null) {
                    existingGenericLicenseUrl.setUrl(genericLicenseUrl.getUrl());
                }

                return existingGenericLicenseUrl;
            })
            .map(genericLicenseUrlRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, genericLicenseUrl.getId().toString())
        );
    }

    /**
     * {@code GET  /generic-license-urls} : get all the genericLicenseUrls.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of genericLicenseUrls in body.
     */
    @GetMapping("/generic-license-urls")
    public List<GenericLicenseUrl> getAllGenericLicenseUrls() {
        log.debug("REST request to get all GenericLicenseUrls");
        return genericLicenseUrlRepository.findAll();
    }

    /**
     * {@code GET  /generic-license-urls/:id} : get the "id" genericLicenseUrl.
     *
     * @param id the id of the genericLicenseUrl to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the genericLicenseUrl, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/generic-license-urls/{id}")
    public ResponseEntity<GenericLicenseUrl> getGenericLicenseUrl(@PathVariable Long id) {
        log.debug("REST request to get GenericLicenseUrl : {}", id);
        Optional<GenericLicenseUrl> genericLicenseUrl = genericLicenseUrlRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(genericLicenseUrl);
    }

    /**
     * {@code DELETE  /generic-license-urls/:id} : delete the "id" genericLicenseUrl.
     *
     * @param id the id of the genericLicenseUrl to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/generic-license-urls/{id}")
    public ResponseEntity<Void> deleteGenericLicenseUrl(@PathVariable Long id) {
        log.debug("REST request to delete GenericLicenseUrl : {}", id);
        genericLicenseUrlRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
