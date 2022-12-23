package net.regnology.lucy.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;
import net.regnology.lucy.domain.LibraryPerProduct;
import net.regnology.lucy.repository.LibraryPerProductRepository;
import net.regnology.lucy.service.LibraryPerProductQueryService;
import net.regnology.lucy.service.LibraryPerProductService;
import net.regnology.lucy.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link LibraryPerProduct}.
 */
@RestController
@RequestMapping("/api")
public class LibraryPerProductResource {

    private final Logger log = LoggerFactory.getLogger(LibraryPerProductResource.class);

    private static final String ENTITY_NAME = "libraryPerProduct";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LibraryPerProductService libraryPerProductService;

    private final LibraryPerProductRepository libraryPerProductRepository;

    private final LibraryPerProductQueryService libraryPerProductQueryService;

    public LibraryPerProductResource(
        LibraryPerProductService libraryPerProductService,
        LibraryPerProductRepository libraryPerProductRepository,
        LibraryPerProductQueryService libraryPerProductQueryService
    ) {
        this.libraryPerProductService = libraryPerProductService;
        this.libraryPerProductRepository = libraryPerProductRepository;
        this.libraryPerProductQueryService = libraryPerProductQueryService;
    }

    /**
     * {@code POST  /library-per-products} : Create a new libraryPerProduct.
     *
     * @param libraryPerProduct the libraryPerProduct to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new libraryPerProduct, or with status {@code 400 (Bad Request)} if the libraryPerProduct has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/library-per-products")
    public ResponseEntity<LibraryPerProduct> createLibraryPerProduct(@RequestBody LibraryPerProduct libraryPerProduct)
        throws URISyntaxException {
        log.debug("REST request to save LibraryPerProduct : {}", libraryPerProduct);
        if (libraryPerProduct.getId() != null) {
            throw new BadRequestAlertException("A new libraryPerProduct cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LibraryPerProduct result = libraryPerProductService.save(libraryPerProduct);
        return ResponseEntity
            .created(new URI("/api/library-per-products/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /library-per-products/:id} : Updates an existing libraryPerProduct.
     *
     * @param id the id of the libraryPerProduct to save.
     * @param libraryPerProduct the libraryPerProduct to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated libraryPerProduct,
     * or with status {@code 400 (Bad Request)} if the libraryPerProduct is not valid,
     * or with status {@code 500 (Internal Server Error)} if the libraryPerProduct couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/library-per-products/{id}")
    public ResponseEntity<LibraryPerProduct> updateLibraryPerProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LibraryPerProduct libraryPerProduct
    ) throws URISyntaxException {
        log.debug("REST request to update LibraryPerProduct : {}, {}", id, libraryPerProduct);
        if (libraryPerProduct.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, libraryPerProduct.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!libraryPerProductRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LibraryPerProduct result = libraryPerProductService.save(libraryPerProduct);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, libraryPerProduct.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /library-per-products/:id} : Partial updates given fields of an existing libraryPerProduct, field will ignore if it is null
     *
     * @param id the id of the libraryPerProduct to save.
     * @param libraryPerProduct the libraryPerProduct to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated libraryPerProduct,
     * or with status {@code 400 (Bad Request)} if the libraryPerProduct is not valid,
     * or with status {@code 404 (Not Found)} if the libraryPerProduct is not found,
     * or with status {@code 500 (Internal Server Error)} if the libraryPerProduct couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/library-per-products/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LibraryPerProduct> partialUpdateLibraryPerProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LibraryPerProduct libraryPerProduct
    ) throws URISyntaxException {
        log.debug("REST request to partial update LibraryPerProduct partially : {}, {}", id, libraryPerProduct);
        if (libraryPerProduct.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, libraryPerProduct.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!libraryPerProductRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LibraryPerProduct> result = libraryPerProductService.partialUpdate(libraryPerProduct);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, libraryPerProduct.getId().toString())
        );
    }

    /**
     * {@code GET  /library-per-products/:id} : get the "id" libraryPerProduct.
     *
     * @param id the id of the libraryPerProduct to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the libraryPerProduct, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/library-per-products/{id}")
    public ResponseEntity<LibraryPerProduct> getLibraryPerProduct(@PathVariable Long id) {
        log.debug("REST request to get LibraryPerProduct : {}", id);
        Optional<LibraryPerProduct> libraryPerProduct = libraryPerProductService.findOne(id);
        return ResponseUtil.wrapOrNotFound(libraryPerProduct);
    }

    /**
     * {@code DELETE  /library-per-products/:id} : delete the "id" libraryPerProduct.
     *
     * @param id the id of the libraryPerProduct to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/library-per-products/{id}")
    public ResponseEntity<Void> deleteLibraryPerProduct(@PathVariable Long id) {
        log.debug("REST request to delete LibraryPerProduct : {}", id);
        libraryPerProductService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
