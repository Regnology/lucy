package net.regnology.lucy.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import net.regnology.lucy.domain.Requirement;
import net.regnology.lucy.repository.RequirementRepository;
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
 * REST controller for managing {@link Requirement}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class RequirementResource {

    private final Logger log = LoggerFactory.getLogger(RequirementResource.class);

    private static final String ENTITY_NAME = "requirement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RequirementRepository requirementRepository;

    public RequirementResource(RequirementRepository requirementRepository) {
        this.requirementRepository = requirementRepository;
    }

    /**
     * {@code POST  /requirements} : Create a new requirement.
     *
     * @param requirement the requirement to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new requirement, or with status {@code 400 (Bad Request)} if the requirement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/requirements")
    public ResponseEntity<Requirement> createRequirement(@Valid @RequestBody Requirement requirement) throws URISyntaxException {
        log.debug("REST request to save Requirement : {}", requirement);
        if (requirement.getId() != null) {
            throw new BadRequestAlertException("A new requirement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Requirement result = requirementRepository.save(requirement);
        return ResponseEntity
            .created(new URI("/api/requirements/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /requirements/:id} : Updates an existing requirement.
     *
     * @param id the id of the requirement to save.
     * @param requirement the requirement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated requirement,
     * or with status {@code 400 (Bad Request)} if the requirement is not valid,
     * or with status {@code 500 (Internal Server Error)} if the requirement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/requirements/{id}")
    public ResponseEntity<Requirement> updateRequirement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Requirement requirement
    ) throws URISyntaxException {
        log.debug("REST request to update Requirement : {}, {}", id, requirement);
        if (requirement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, requirement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!requirementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Requirement result = requirementRepository.save(requirement);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, requirement.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /requirements/:id} : Partial updates given fields of an existing requirement, field will ignore if it is null
     *
     * @param id the id of the requirement to save.
     * @param requirement the requirement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated requirement,
     * or with status {@code 400 (Bad Request)} if the requirement is not valid,
     * or with status {@code 404 (Not Found)} if the requirement is not found,
     * or with status {@code 500 (Internal Server Error)} if the requirement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/requirements/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Requirement> partialUpdateRequirement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Requirement requirement
    ) throws URISyntaxException {
        log.debug("REST request to partial update Requirement partially : {}, {}", id, requirement);
        if (requirement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, requirement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!requirementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Requirement> result = requirementRepository
            .findById(requirement.getId())
            .map(existingRequirement -> {
                if (requirement.getShortText() != null) {
                    existingRequirement.setShortText(requirement.getShortText());
                }
                if (requirement.getDescription() != null) {
                    existingRequirement.setDescription(requirement.getDescription());
                }

                return existingRequirement;
            })
            .map(requirementRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, requirement.getId().toString())
        );
    }

    /**
     * {@code GET  /requirements} : get all the requirements.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of requirements in body.
     */
    @GetMapping("/requirements")
    public ResponseEntity<List<Requirement>> getAllRequirements(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Requirements");
        Page<Requirement> page = requirementRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /requirements/:id} : get the "id" requirement.
     *
     * @param id the id of the requirement to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the requirement, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/requirements/{id}")
    public ResponseEntity<Requirement> getRequirement(@PathVariable Long id) {
        log.debug("REST request to get Requirement : {}", id);
        Optional<Requirement> requirement = requirementRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(requirement);
    }

    /**
     * {@code DELETE  /requirements/:id} : delete the "id" requirement.
     *
     * @param id the id of the requirement to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/requirements/{id}")
    public ResponseEntity<Void> deleteRequirement(@PathVariable Long id) {
        log.debug("REST request to delete Requirement : {}", id);
        requirementRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
