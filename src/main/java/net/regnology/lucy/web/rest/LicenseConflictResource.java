package net.regnology.lucy.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import net.regnology.lucy.domain.LicenseConflict;
import net.regnology.lucy.repository.LicenseConflictRepository;
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
 * REST controller for managing {@link LicenseConflict}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LicenseConflictResource {

    private final Logger log = LoggerFactory.getLogger(LicenseConflictResource.class);

    private static final String ENTITY_NAME = "licenseConflict";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LicenseConflictRepository licenseConflictRepository;

    public LicenseConflictResource(LicenseConflictRepository licenseConflictRepository) {
        this.licenseConflictRepository = licenseConflictRepository;
    }

    /**
     * {@code POST  /license-conflicts} : Create a new licenseConflict.
     *
     * @param licenseConflict the licenseConflict to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new licenseConflict, or with status {@code 400 (Bad Request)} if the licenseConflict has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/license-conflicts")
    public ResponseEntity<LicenseConflict> createLicenseConflict(@Valid @RequestBody LicenseConflict licenseConflict)
        throws URISyntaxException {
        log.debug("REST request to save LicenseConflict : {}", licenseConflict);
        if (licenseConflict.getId() != null) {
            throw new BadRequestAlertException("A new licenseConflict cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LicenseConflict result = licenseConflictRepository.save(licenseConflict);
        return ResponseEntity
            .created(new URI("/api/license-conflicts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /license-conflicts/:id} : Updates an existing licenseConflict.
     *
     * @param id the id of the licenseConflict to save.
     * @param licenseConflict the licenseConflict to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated licenseConflict,
     * or with status {@code 400 (Bad Request)} if the licenseConflict is not valid,
     * or with status {@code 500 (Internal Server Error)} if the licenseConflict couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/license-conflicts/{id}")
    public ResponseEntity<LicenseConflict> updateLicenseConflict(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LicenseConflict licenseConflict
    ) throws URISyntaxException {
        log.debug("REST request to update LicenseConflict : {}, {}", id, licenseConflict);
        if (licenseConflict.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, licenseConflict.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!licenseConflictRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LicenseConflict result = licenseConflictRepository.save(licenseConflict);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, licenseConflict.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /license-conflicts/:id} : Partial updates given fields of an existing licenseConflict, field will ignore if it is null
     *
     * @param id the id of the licenseConflict to save.
     * @param licenseConflict the licenseConflict to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated licenseConflict,
     * or with status {@code 400 (Bad Request)} if the licenseConflict is not valid,
     * or with status {@code 404 (Not Found)} if the licenseConflict is not found,
     * or with status {@code 500 (Internal Server Error)} if the licenseConflict couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/license-conflicts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LicenseConflict> partialUpdateLicenseConflict(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LicenseConflict licenseConflict
    ) throws URISyntaxException {
        log.debug("REST request to partial update LicenseConflict partially : {}, {}", id, licenseConflict);
        if (licenseConflict.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, licenseConflict.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!licenseConflictRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LicenseConflict> result = licenseConflictRepository
            .findById(licenseConflict.getId())
            .map(existingLicenseConflict -> {
                if (licenseConflict.getCompatibility() != null) {
                    existingLicenseConflict.setCompatibility(licenseConflict.getCompatibility());
                }
                if (licenseConflict.getComment() != null) {
                    existingLicenseConflict.setComment(licenseConflict.getComment());
                }

                return existingLicenseConflict;
            })
            .map(licenseConflictRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, licenseConflict.getId().toString())
        );
    }

    /**
     * {@code GET  /license-conflicts} : get all the licenseConflicts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of licenseConflicts in body.
     */
    @GetMapping("/license-conflicts")
    public List<LicenseConflict> getAllLicenseConflicts() {
        log.debug("REST request to get all LicenseConflicts");
        return licenseConflictRepository.findAll();
    }

    /**
     * {@code GET  /license-conflicts/:id} : get the "id" licenseConflict.
     *
     * @param id the id of the licenseConflict to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the licenseConflict, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/license-conflicts/{id}")
    public ResponseEntity<LicenseConflict> getLicenseConflict(@PathVariable Long id) {
        log.debug("REST request to get LicenseConflict : {}", id);
        Optional<LicenseConflict> licenseConflict = licenseConflictRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(licenseConflict);
    }

    /**
     * {@code DELETE  /license-conflicts/:id} : delete the "id" licenseConflict.
     *
     * @param id the id of the licenseConflict to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/license-conflicts/{id}")
    public ResponseEntity<Void> deleteLicenseConflict(@PathVariable Long id) {
        log.debug("REST request to delete LicenseConflict : {}", id);
        licenseConflictRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
