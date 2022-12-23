package net.regnology.lucy.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import net.regnology.lucy.domain.LicenseNamingMapping;
import net.regnology.lucy.repository.LicenseNamingMappingRepository;
import net.regnology.lucy.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link LicenseNamingMapping}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LicenseNamingMappingResource {

    private final Logger log = LoggerFactory.getLogger(LicenseNamingMappingResource.class);

    private static final String ENTITY_NAME = "licenseNamingMapping";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LicenseNamingMappingRepository licenseNamingMappingRepository;

    public LicenseNamingMappingResource(LicenseNamingMappingRepository licenseNamingMappingRepository) {
        this.licenseNamingMappingRepository = licenseNamingMappingRepository;
    }

    /**
     * {@code POST  /license-naming-mappings} : Create a new licenseNamingMapping.
     *
     * @param licenseNamingMapping the licenseNamingMapping to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new licenseNamingMapping, or with status {@code 400 (Bad Request)} if the licenseNamingMapping has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/license-naming-mappings")
    public ResponseEntity<LicenseNamingMapping> createLicenseNamingMapping(@Valid @RequestBody LicenseNamingMapping licenseNamingMapping)
        throws URISyntaxException {
        log.debug("REST request to save LicenseNamingMapping : {}", licenseNamingMapping);
        if (licenseNamingMapping.getId() != null) {
            throw new BadRequestAlertException("A new licenseNamingMapping cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LicenseNamingMapping result = licenseNamingMappingRepository.save(licenseNamingMapping);
        return ResponseEntity
            .created(new URI("/api/license-naming-mappings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /license-naming-mappings/:id} : Updates an existing licenseNamingMapping.
     *
     * @param id the id of the licenseNamingMapping to save.
     * @param licenseNamingMapping the licenseNamingMapping to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated licenseNamingMapping,
     * or with status {@code 400 (Bad Request)} if the licenseNamingMapping is not valid,
     * or with status {@code 500 (Internal Server Error)} if the licenseNamingMapping couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/license-naming-mappings/{id}")
    public ResponseEntity<LicenseNamingMapping> updateLicenseNamingMapping(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LicenseNamingMapping licenseNamingMapping
    ) throws URISyntaxException {
        log.debug("REST request to update LicenseNamingMapping : {}, {}", id, licenseNamingMapping);
        if (licenseNamingMapping.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, licenseNamingMapping.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!licenseNamingMappingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LicenseNamingMapping result = licenseNamingMappingRepository.save(licenseNamingMapping);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, licenseNamingMapping.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /license-naming-mappings/:id} : Partial updates given fields of an existing licenseNamingMapping, field will ignore if it is null
     *
     * @param id the id of the licenseNamingMapping to save.
     * @param licenseNamingMapping the licenseNamingMapping to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated licenseNamingMapping,
     * or with status {@code 400 (Bad Request)} if the licenseNamingMapping is not valid,
     * or with status {@code 404 (Not Found)} if the licenseNamingMapping is not found,
     * or with status {@code 500 (Internal Server Error)} if the licenseNamingMapping couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/license-naming-mappings/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LicenseNamingMapping> partialUpdateLicenseNamingMapping(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LicenseNamingMapping licenseNamingMapping
    ) throws URISyntaxException {
        log.debug("REST request to partial update LicenseNamingMapping partially : {}, {}", id, licenseNamingMapping);
        if (licenseNamingMapping.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, licenseNamingMapping.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!licenseNamingMappingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LicenseNamingMapping> result = licenseNamingMappingRepository
            .findById(licenseNamingMapping.getId())
            .map(existingLicenseNamingMapping -> {
                if (licenseNamingMapping.getRegex() != null) {
                    existingLicenseNamingMapping.setRegex(licenseNamingMapping.getRegex());
                }
                if (licenseNamingMapping.getUniformShortIdentifier() != null) {
                    existingLicenseNamingMapping.setUniformShortIdentifier(licenseNamingMapping.getUniformShortIdentifier());
                }

                return existingLicenseNamingMapping;
            })
            .map(licenseNamingMappingRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, licenseNamingMapping.getId().toString())
        );
    }

    /**
     * {@code GET  /license-naming-mappings} : get all the licenseNamingMappings.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of licenseNamingMappings in body.
     */
    @GetMapping("/license-naming-mappings")
    public ResponseEntity<List<LicenseNamingMapping>> getAllLicenseNamingMappings(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of LicenseNamingMappings");
        Page<LicenseNamingMapping> page = licenseNamingMappingRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /license-naming-mappings/:id} : get the "id" licenseNamingMapping.
     *
     * @param id the id of the licenseNamingMapping to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the licenseNamingMapping, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/license-naming-mappings/{id}")
    public ResponseEntity<LicenseNamingMapping> getLicenseNamingMapping(@PathVariable Long id) {
        log.debug("REST request to get LicenseNamingMapping : {}", id);
        Optional<LicenseNamingMapping> licenseNamingMapping = licenseNamingMappingRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(licenseNamingMapping);
    }

    /**
     * {@code DELETE  /license-naming-mappings/:id} : delete the "id" licenseNamingMapping.
     *
     * @param id the id of the licenseNamingMapping to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/license-naming-mappings/{id}")
    public ResponseEntity<Void> deleteLicenseNamingMapping(@PathVariable Long id) {
        log.debug("REST request to delete LicenseNamingMapping : {}", id);
        licenseNamingMappingRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
