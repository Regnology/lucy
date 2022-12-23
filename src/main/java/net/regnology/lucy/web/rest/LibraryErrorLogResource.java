package net.regnology.lucy.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import net.regnology.lucy.domain.LibraryErrorLog;
import net.regnology.lucy.repository.LibraryErrorLogRepository;
import net.regnology.lucy.service.LibraryErrorLogQueryService;
import net.regnology.lucy.service.LibraryErrorLogService;
import net.regnology.lucy.service.criteria.LibraryErrorLogCriteria;
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
 * REST controller for managing {@link LibraryErrorLog}.
 */
@RestController
@RequestMapping("/api")
public class LibraryErrorLogResource {

    private final Logger log = LoggerFactory.getLogger(LibraryErrorLogResource.class);

    private static final String ENTITY_NAME = "libraryErrorLog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LibraryErrorLogService libraryErrorLogService;

    private final LibraryErrorLogRepository libraryErrorLogRepository;

    private final LibraryErrorLogQueryService libraryErrorLogQueryService;

    public LibraryErrorLogResource(
        LibraryErrorLogService libraryErrorLogService,
        LibraryErrorLogRepository libraryErrorLogRepository,
        LibraryErrorLogQueryService libraryErrorLogQueryService
    ) {
        this.libraryErrorLogService = libraryErrorLogService;
        this.libraryErrorLogRepository = libraryErrorLogRepository;
        this.libraryErrorLogQueryService = libraryErrorLogQueryService;
    }

    /**
     * {@code POST  /library-error-logs} : Create a new libraryErrorLog.
     *
     * @param libraryErrorLog the libraryErrorLog to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new libraryErrorLog, or with status {@code 400 (Bad Request)} if the libraryErrorLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/library-error-logs")
    public ResponseEntity<LibraryErrorLog> createLibraryErrorLog(@Valid @RequestBody LibraryErrorLog libraryErrorLog)
        throws URISyntaxException {
        log.debug("REST request to save LibraryErrorLog : {}", libraryErrorLog);
        if (libraryErrorLog.getId() != null) {
            throw new BadRequestAlertException("A new libraryErrorLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LibraryErrorLog result = libraryErrorLogService.save(libraryErrorLog);
        return ResponseEntity
            .created(new URI("/api/library-error-logs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /library-error-logs/:id} : Updates an existing libraryErrorLog.
     *
     * @param id the id of the libraryErrorLog to save.
     * @param libraryErrorLog the libraryErrorLog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated libraryErrorLog,
     * or with status {@code 400 (Bad Request)} if the libraryErrorLog is not valid,
     * or with status {@code 500 (Internal Server Error)} if the libraryErrorLog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/library-error-logs/{id}")
    public ResponseEntity<LibraryErrorLog> updateLibraryErrorLog(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LibraryErrorLog libraryErrorLog
    ) throws URISyntaxException {
        log.debug("REST request to update LibraryErrorLog : {}, {}", id, libraryErrorLog);
        if (libraryErrorLog.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, libraryErrorLog.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!libraryErrorLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LibraryErrorLog result = libraryErrorLogService.save(libraryErrorLog);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, libraryErrorLog.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /library-error-logs/:id} : Partial updates given fields of an existing libraryErrorLog, field will ignore if it is null
     *
     * @param id the id of the libraryErrorLog to save.
     * @param libraryErrorLog the libraryErrorLog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated libraryErrorLog,
     * or with status {@code 400 (Bad Request)} if the libraryErrorLog is not valid,
     * or with status {@code 404 (Not Found)} if the libraryErrorLog is not found,
     * or with status {@code 500 (Internal Server Error)} if the libraryErrorLog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/library-error-logs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LibraryErrorLog> partialUpdateLibraryErrorLog(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LibraryErrorLog libraryErrorLog
    ) throws URISyntaxException {
        log.debug("REST request to partial update LibraryErrorLog partially : {}, {}", id, libraryErrorLog);
        if (libraryErrorLog.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, libraryErrorLog.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!libraryErrorLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LibraryErrorLog> result = libraryErrorLogService.partialUpdate(libraryErrorLog);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, libraryErrorLog.getId().toString())
        );
    }

    /**
     * {@code GET  /library-error-logs} : get all the libraryErrorLogs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of libraryErrorLogs in body.
     */
    @GetMapping("/library-error-logs")
    public ResponseEntity<List<LibraryErrorLog>> getAllLibraryErrorLogs(
        LibraryErrorLogCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get LibraryErrorLogs by criteria: {}", criteria);
        Page<LibraryErrorLog> page = libraryErrorLogQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /library-error-logs/count} : count all the libraryErrorLogs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/library-error-logs/count")
    public ResponseEntity<Long> countLibraryErrorLogs(LibraryErrorLogCriteria criteria) {
        log.debug("REST request to count LibraryErrorLogs by criteria: {}", criteria);
        return ResponseEntity.ok().body(libraryErrorLogQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /library-error-logs/:id} : get the "id" libraryErrorLog.
     *
     * @param id the id of the libraryErrorLog to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the libraryErrorLog, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/library-error-logs/{id}")
    public ResponseEntity<LibraryErrorLog> getLibraryErrorLog(@PathVariable Long id) {
        log.debug("REST request to get LibraryErrorLog : {}", id);
        Optional<LibraryErrorLog> libraryErrorLog = libraryErrorLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(libraryErrorLog);
    }

    /**
     * {@code DELETE  /library-error-logs/:id} : delete the "id" libraryErrorLog.
     *
     * @param id the id of the libraryErrorLog to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/library-error-logs/{id}")
    public ResponseEntity<Void> deleteLibraryErrorLog(@PathVariable Long id) {
        log.debug("REST request to delete LibraryErrorLog : {}", id);
        libraryErrorLogService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
