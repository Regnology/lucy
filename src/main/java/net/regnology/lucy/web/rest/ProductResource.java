package net.regnology.lucy.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.domain.Product;
import net.regnology.lucy.domain.enumeration.UploadState;
import net.regnology.lucy.domain.helper.Upload;
import net.regnology.lucy.repository.ProductRepository;
import net.regnology.lucy.service.ProductCustomService;
import net.regnology.lucy.service.ProductQueryService;
import net.regnology.lucy.service.ProductService;
import net.regnology.lucy.service.criteria.ProductCriteria;
import net.regnology.lucy.service.exceptions.ProductException;
import net.regnology.lucy.service.exceptions.UploadException;
import net.regnology.lucy.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link Product}.
 */
@RestController
@RequestMapping("/api")
public class ProductResource {

    private final Logger log = LoggerFactory.getLogger(ProductResource.class);

    private static final String ENTITY_NAME = "product";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductService productService;
    private final ProductCustomService productCustomService;

    private final ProductRepository productRepository;

    private final ProductQueryService productQueryService;

    public ProductResource(
        ProductService productService,
        ProductRepository productRepository,
        ProductQueryService productQueryService,
        ProductCustomService productCustomService
    ) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.productQueryService = productQueryService;
        this.productCustomService = productCustomService;
    }

    /**
     * {@code POST  /products} : Create a new product.
     *
     * @param product the product to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new product, or with status {@code 400 (Bad Request)} if the product has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) throws URISyntaxException {
        log.debug("REST request to save Product : {}", product);
        if (product.getId() != null) {
            throw new BadRequestAlertException("A new product cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Product result = productService.save(product);
        return ResponseEntity
            .created(new URI("/api/products/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /products/:id} : Updates an existing product.
     *
     * @param id the id of the product to save.
     * @param product the product to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated product,
     * or with status {@code 400 (Bad Request)} if the product is not valid,
     * or with status {@code 500 (Internal Server Error)} if the product couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Product product
    ) throws URISyntaxException {
        log.debug("REST request to update Product : {}, {}", id, product);
        if (product.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, product.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Product result = productService.save(product);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, product.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /products/:id} : Partial updates given fields of an existing product, field will ignore if it is null
     *
     * @param id the id of the product to save.
     * @param product the product to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated product,
     * or with status {@code 400 (Bad Request)} if the product is not valid,
     * or with status {@code 404 (Not Found)} if the product is not found,
     * or with status {@code 500 (Internal Server Error)} if the product couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/products/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Product> partialUpdateProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Product product
    ) throws URISyntaxException {
        log.debug("REST request to partial update Product partially : {}, {}", id, product);
        if (product.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, product.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Product> result = productService.partialUpdate(product);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, product.getId().toString())
        );
    }

    /**
     * {@code GET  /products} : get all the products.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of products in body.
     */
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts(
        ProductCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Products by criteria: {}", criteria);
        Page<Product> page = productQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /products/count} : count all the products.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/products/count")
    public ResponseEntity<Long> countProducts(ProductCriteria criteria) {
        log.debug("REST request to count Products by criteria: {}", criteria);
        return ResponseEntity.ok().body(productQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /products/:id} : get the "id" product.
     *
     * @param id the id of the product to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the product, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        log.debug("REST request to get Product : {}", id);
        Optional<Product> product = productService.findOne(id);
        return ResponseUtil.wrapOrNotFound(product);
    }

    /**
     * {@code DELETE  /products/:id} : delete the "id" product.
     *
     * @param id the id of the product to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.debug("REST request to delete Product : {}", id);
        productService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /* Legacy code. Necessary for the Jenkins Lucy plugin */

    /**
     * {@code POST  /products/:id/add-libraries} : Add manually libraries to a product.
     *
     * @param id        ID of the product
     * @param libraries list of libraries to be added
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)},
     * or with status {@code 400 (Bad Request)} if the product does not exist.
     */
    @PostMapping("/products/{id}/add-libraries")
    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    public ResponseEntity<Void> addLibraries(@PathVariable Long id, @RequestBody List<Library> libraries) {
        log.debug("REST request to add libraries to product : {}", id);
        Optional<Product> optionalProduct = productService.findOne(id);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            productCustomService.addLibraries(product, libraries);

            return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createAlert(applicationName, "Libraries have been added successfully", ENTITY_NAME))
                .build();
        } else {
            throw new BadRequestAlertException("Cannot find Product ID", ENTITY_NAME, "idnotfound");
        }
    }

    /**
     * {@code POST  /products/:id/upload} : Upload of a BOM or archive to a product.
     *
     * @param id     ID of the product
     * @param delete true if the libraries from a previous upload should be deleted, or
     *               false if the libraries from the new upload should be added to the previous results
     * @param upload The Upload object with libraries
     * @return The {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}, if the processing is successfully started,
     * or with status {@code 400 (Bad Request)} if an error occurred during processing or the product does not exist.
     */
    @PostMapping("/products/{id}/upload")
    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    public ResponseEntity<Void> upload(
        @PathVariable Long id,
        @RequestParam(value = "delete", defaultValue = "true") boolean delete,
        @RequestBody Upload upload
    ) {
        log.debug("REST request with upload to product : {}", id);
        if (!productCustomService.existsById(id)) {
            throw new BadRequestAlertException("Product not found", ENTITY_NAME, "idnotfound");
        }

        Product product;
        Optional<Product> optionalProduct = productService.findOne(id);

        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();

            product.setUploadState(UploadState.PROCESSING);
            productCustomService.saveAndFlush(product);
        } else {
            throw new BadRequestAlertException("Cannot find Product ID", ENTITY_NAME, "idnotfound");
        }

        try {
            productCustomService.processUpload(product, upload, delete);
        } catch (UploadException e) {
            log.error("Error while processing the upload : {}", e.getMessage());
        }

        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "Upload was successful", ENTITY_NAME)).build();
    }

    /**
     * {@code GET  /products/:id/in-development-product} : Get the product that is "In Development".
     * @param id ID of the product
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the product,
     * or with status {@code 400 (Bad Request)} if the product is not valid or
     * no unique product which is "In Development" could be found.
     */
    @GetMapping("/products/{id}/in-development-product")
    public ResponseEntity<Product> getInDevelopmentProductById(@PathVariable Long id) {
        Optional<Product> optionalProduct = productService.findOne(id);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();

            try {
                return ResponseEntity.ok().body(productCustomService.getInDevelopmentProduct(product));
            } catch (ProductException e) {
                throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "noproduct");
            }
        } else {
            throw new BadRequestAlertException("Cannot find Product ID", ENTITY_NAME, "idnotfound");
        }
    }
}
