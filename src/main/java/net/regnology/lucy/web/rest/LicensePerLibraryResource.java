package net.regnology.lucy.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.regnology.lucy.domain.LicensePerLibrary;
import net.regnology.lucy.repository.LicensePerLibraryRepository;
import net.regnology.lucy.service.LicensePerLibraryQueryService;
import net.regnology.lucy.service.LicensePerLibraryService;
import net.regnology.lucy.service.criteria.LicensePerLibraryCriteria;
import net.regnology.lucy.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link LicensePerLibrary}.
 */
@RestController
@RequestMapping("/api")
public class LicensePerLibraryResource {

    private final Logger log = LoggerFactory.getLogger(LicensePerLibraryResource.class);

    private static final String ENTITY_NAME = "licensePerLibrary";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LicensePerLibraryService licensePerLibraryService;

    private final LicensePerLibraryRepository licensePerLibraryRepository;

    private final LicensePerLibraryQueryService licensePerLibraryQueryService;

    public LicensePerLibraryResource(
        LicensePerLibraryService licensePerLibraryService,
        LicensePerLibraryRepository licensePerLibraryRepository,
        LicensePerLibraryQueryService licensePerLibraryQueryService
    ) {
        this.licensePerLibraryService = licensePerLibraryService;
        this.licensePerLibraryRepository = licensePerLibraryRepository;
        this.licensePerLibraryQueryService = licensePerLibraryQueryService;
    }

    /**
     * {@code POST  /license-per-libraries} : Create a new licensePerLibrary.
     *
     * @param licensePerLibrary the licensePerLibrary to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new licensePerLibrary, or with status {@code 400 (Bad Request)} if the licensePerLibrary has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/license-per-libraries")
    public ResponseEntity<LicensePerLibrary> createLicensePerLibrary(@RequestBody LicensePerLibrary licensePerLibrary)
        throws URISyntaxException {
        log.debug("REST request to save LicensePerLibrary : {}", licensePerLibrary);
        if (licensePerLibrary.getId() != null) {
            throw new BadRequestAlertException("A new licensePerLibrary cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LicensePerLibrary result = licensePerLibraryService.save(licensePerLibrary);
        return ResponseEntity
            .created(new URI("/api/license-per-libraries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /license-per-libraries/:id} : Updates an existing licensePerLibrary.
     *
     * @param id the id of the licensePerLibrary to save.
     * @param licensePerLibrary the licensePerLibrary to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated licensePerLibrary,
     * or with status {@code 400 (Bad Request)} if the licensePerLibrary is not valid,
     * or with status {@code 500 (Internal Server Error)} if the licensePerLibrary couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/license-per-libraries/{id}")
    public ResponseEntity<LicensePerLibrary> updateLicensePerLibrary(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LicensePerLibrary licensePerLibrary
    ) throws URISyntaxException {
        log.debug("REST request to update LicensePerLibrary : {}, {}", id, licensePerLibrary);
        if (licensePerLibrary.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, licensePerLibrary.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!licensePerLibraryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LicensePerLibrary result = licensePerLibraryService.save(licensePerLibrary);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, licensePerLibrary.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /license-per-libraries/:id} : Partial updates given fields of an existing licensePerLibrary, field will ignore if it is null
     *
     * @param id the id of the licensePerLibrary to save.
     * @param licensePerLibrary the licensePerLibrary to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated licensePerLibrary,
     * or with status {@code 400 (Bad Request)} if the licensePerLibrary is not valid,
     * or with status {@code 404 (Not Found)} if the licensePerLibrary is not found,
     * or with status {@code 500 (Internal Server Error)} if the licensePerLibrary couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/license-per-libraries/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LicensePerLibrary> partialUpdateLicensePerLibrary(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LicensePerLibrary licensePerLibrary
    ) throws URISyntaxException {
        log.debug("REST request to partial update LicensePerLibrary partially : {}, {}", id, licensePerLibrary);
        if (licensePerLibrary.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, licensePerLibrary.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!licensePerLibraryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LicensePerLibrary> result = licensePerLibraryService.partialUpdate(licensePerLibrary);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, licensePerLibrary.getId().toString())
        );
    }

    /**
     * {@code GET  /license-per-libraries} : get all the licensePerLibraries.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of licensePerLibraries in body.
     */
    @GetMapping("/license-per-libraries")
    public ResponseEntity<List<LicensePerLibrary>> getAllLicensePerLibraries(
        LicensePerLibraryCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get LicensePerLibraries by criteria: {}", criteria);
        Page<LicensePerLibrary> page = licensePerLibraryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /license-per-libraries/count} : count all the licensePerLibraries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/license-per-libraries/count")
    public ResponseEntity<Long> countLicensePerLibraries(LicensePerLibraryCriteria criteria) {
        log.debug("REST request to count LicensePerLibraries by criteria: {}", criteria);
        return ResponseEntity.ok().body(licensePerLibraryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /license-per-libraries/:id} : get the "id" licensePerLibrary.
     *
     * @param id the id of the licensePerLibrary to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the licensePerLibrary, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/license-per-libraries/{id}")
    public ResponseEntity<LicensePerLibrary> getLicensePerLibrary(@PathVariable Long id) {
        log.debug("REST request to get LicensePerLibrary : {}", id);
        Optional<LicensePerLibrary> licensePerLibrary = licensePerLibraryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(licensePerLibrary);
    }

    /**
     * {@code DELETE  /license-per-libraries/:id} : delete the "id" licensePerLibrary.
     *
     * @param id the id of the licensePerLibrary to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/license-per-libraries/{id}")
    public ResponseEntity<Void> deleteLicensePerLibrary(@PathVariable Long id) {
        log.debug("REST request to delete LicensePerLibrary : {}", id);
        licensePerLibraryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
