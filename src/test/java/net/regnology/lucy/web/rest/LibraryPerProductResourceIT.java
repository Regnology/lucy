package net.regnology.lucy.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import net.regnology.lucy.IntegrationTest;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.domain.LibraryPerProduct;
import net.regnology.lucy.domain.Product;
import net.regnology.lucy.repository.LibraryPerProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link LibraryPerProductResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LibraryPerProductResourceIT {

    private static final LocalDate DEFAULT_ADDED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ADDED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_ADDED_DATE = LocalDate.ofEpochDay(-1L);

    private static final Boolean DEFAULT_ADDED_MANUALLY = false;
    private static final Boolean UPDATED_ADDED_MANUALLY = true;

    private static final Boolean DEFAULT_HIDE_FOR_PUBLISHING = false;
    private static final Boolean UPDATED_HIDE_FOR_PUBLISHING = true;

    private static final String ENTITY_API_URL = "/api/library-per-products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LibraryPerProductRepository libraryPerProductRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLibraryPerProductMockMvc;

    private LibraryPerProduct libraryPerProduct;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LibraryPerProduct createEntity(EntityManager em) {
        LibraryPerProduct libraryPerProduct = new LibraryPerProduct()
            .addedDate(DEFAULT_ADDED_DATE)
            .addedManually(DEFAULT_ADDED_MANUALLY)
            .hideForPublishing(DEFAULT_HIDE_FOR_PUBLISHING);
        return libraryPerProduct;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LibraryPerProduct createUpdatedEntity(EntityManager em) {
        LibraryPerProduct libraryPerProduct = new LibraryPerProduct()
            .addedDate(UPDATED_ADDED_DATE)
            .addedManually(UPDATED_ADDED_MANUALLY)
            .hideForPublishing(UPDATED_HIDE_FOR_PUBLISHING);
        return libraryPerProduct;
    }

    @BeforeEach
    public void initTest() {
        libraryPerProduct = createEntity(em);
    }

    @Test
    @Transactional
    void createLibraryPerProduct() throws Exception {
        int databaseSizeBeforeCreate = libraryPerProductRepository.findAll().size();
        // Create the LibraryPerProduct
        restLibraryPerProductMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(libraryPerProduct))
            )
            .andExpect(status().isCreated());

        // Validate the LibraryPerProduct in the database
        List<LibraryPerProduct> libraryPerProductList = libraryPerProductRepository.findAll();
        assertThat(libraryPerProductList).hasSize(databaseSizeBeforeCreate + 1);
        LibraryPerProduct testLibraryPerProduct = libraryPerProductList.get(libraryPerProductList.size() - 1);
        assertThat(testLibraryPerProduct.getAddedDate()).isEqualTo(DEFAULT_ADDED_DATE);
        assertThat(testLibraryPerProduct.getAddedManually()).isEqualTo(DEFAULT_ADDED_MANUALLY);
        assertThat(testLibraryPerProduct.getHideForPublishing()).isEqualTo(DEFAULT_HIDE_FOR_PUBLISHING);
    }

    @Test
    @Transactional
    void createLibraryPerProductWithExistingId() throws Exception {
        // Create the LibraryPerProduct with an existing ID
        libraryPerProduct.setId(1L);

        int databaseSizeBeforeCreate = libraryPerProductRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLibraryPerProductMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(libraryPerProduct))
            )
            .andExpect(status().isBadRequest());

        // Validate the LibraryPerProduct in the database
        List<LibraryPerProduct> libraryPerProductList = libraryPerProductRepository.findAll();
        assertThat(libraryPerProductList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllLibraryPerProducts() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        // Get all the libraryPerProductList
        restLibraryPerProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(libraryPerProduct.getId().intValue())))
            .andExpect(jsonPath("$.[*].addedDate").value(hasItem(DEFAULT_ADDED_DATE.toString())))
            .andExpect(jsonPath("$.[*].addedManually").value(hasItem(DEFAULT_ADDED_MANUALLY.booleanValue())))
            .andExpect(jsonPath("$.[*].hideForPublishing").value(hasItem(DEFAULT_HIDE_FOR_PUBLISHING.booleanValue())));
    }

    @Test
    @Transactional
    void getLibraryPerProduct() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        // Get the libraryPerProduct
        restLibraryPerProductMockMvc
            .perform(get(ENTITY_API_URL_ID, libraryPerProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(libraryPerProduct.getId().intValue()))
            .andExpect(jsonPath("$.addedDate").value(DEFAULT_ADDED_DATE.toString()))
            .andExpect(jsonPath("$.addedManually").value(DEFAULT_ADDED_MANUALLY.booleanValue()))
            .andExpect(jsonPath("$.hideForPublishing").value(DEFAULT_HIDE_FOR_PUBLISHING.booleanValue()));
    }

    @Test
    @Transactional
    void getLibraryPerProductsByIdFiltering() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        Long id = libraryPerProduct.getId();

        defaultLibraryPerProductShouldBeFound("id.equals=" + id);
        defaultLibraryPerProductShouldNotBeFound("id.notEquals=" + id);

        defaultLibraryPerProductShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultLibraryPerProductShouldNotBeFound("id.greaterThan=" + id);

        defaultLibraryPerProductShouldBeFound("id.lessThanOrEqual=" + id);
        defaultLibraryPerProductShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLibraryPerProductsByAddedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        // Get all the libraryPerProductList where addedDate equals to DEFAULT_ADDED_DATE
        defaultLibraryPerProductShouldBeFound("addedDate.equals=" + DEFAULT_ADDED_DATE);

        // Get all the libraryPerProductList where addedDate equals to UPDATED_ADDED_DATE
        defaultLibraryPerProductShouldNotBeFound("addedDate.equals=" + UPDATED_ADDED_DATE);
    }

    @Test
    @Transactional
    void getAllLibraryPerProductsByAddedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        // Get all the libraryPerProductList where addedDate not equals to DEFAULT_ADDED_DATE
        defaultLibraryPerProductShouldNotBeFound("addedDate.notEquals=" + DEFAULT_ADDED_DATE);

        // Get all the libraryPerProductList where addedDate not equals to UPDATED_ADDED_DATE
        defaultLibraryPerProductShouldBeFound("addedDate.notEquals=" + UPDATED_ADDED_DATE);
    }

    @Test
    @Transactional
    void getAllLibraryPerProductsByAddedDateIsInShouldWork() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        // Get all the libraryPerProductList where addedDate in DEFAULT_ADDED_DATE or UPDATED_ADDED_DATE
        defaultLibraryPerProductShouldBeFound("addedDate.in=" + DEFAULT_ADDED_DATE + "," + UPDATED_ADDED_DATE);

        // Get all the libraryPerProductList where addedDate equals to UPDATED_ADDED_DATE
        defaultLibraryPerProductShouldNotBeFound("addedDate.in=" + UPDATED_ADDED_DATE);
    }

    @Test
    @Transactional
    void getAllLibraryPerProductsByAddedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        // Get all the libraryPerProductList where addedDate is not null
        defaultLibraryPerProductShouldBeFound("addedDate.specified=true");

        // Get all the libraryPerProductList where addedDate is null
        defaultLibraryPerProductShouldNotBeFound("addedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllLibraryPerProductsByAddedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        // Get all the libraryPerProductList where addedDate is greater than or equal to DEFAULT_ADDED_DATE
        defaultLibraryPerProductShouldBeFound("addedDate.greaterThanOrEqual=" + DEFAULT_ADDED_DATE);

        // Get all the libraryPerProductList where addedDate is greater than or equal to UPDATED_ADDED_DATE
        defaultLibraryPerProductShouldNotBeFound("addedDate.greaterThanOrEqual=" + UPDATED_ADDED_DATE);
    }

    @Test
    @Transactional
    void getAllLibraryPerProductsByAddedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        // Get all the libraryPerProductList where addedDate is less than or equal to DEFAULT_ADDED_DATE
        defaultLibraryPerProductShouldBeFound("addedDate.lessThanOrEqual=" + DEFAULT_ADDED_DATE);

        // Get all the libraryPerProductList where addedDate is less than or equal to SMALLER_ADDED_DATE
        defaultLibraryPerProductShouldNotBeFound("addedDate.lessThanOrEqual=" + SMALLER_ADDED_DATE);
    }

    @Test
    @Transactional
    void getAllLibraryPerProductsByAddedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        // Get all the libraryPerProductList where addedDate is less than DEFAULT_ADDED_DATE
        defaultLibraryPerProductShouldNotBeFound("addedDate.lessThan=" + DEFAULT_ADDED_DATE);

        // Get all the libraryPerProductList where addedDate is less than UPDATED_ADDED_DATE
        defaultLibraryPerProductShouldBeFound("addedDate.lessThan=" + UPDATED_ADDED_DATE);
    }

    @Test
    @Transactional
    void getAllLibraryPerProductsByAddedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        // Get all the libraryPerProductList where addedDate is greater than DEFAULT_ADDED_DATE
        defaultLibraryPerProductShouldNotBeFound("addedDate.greaterThan=" + DEFAULT_ADDED_DATE);

        // Get all the libraryPerProductList where addedDate is greater than SMALLER_ADDED_DATE
        defaultLibraryPerProductShouldBeFound("addedDate.greaterThan=" + SMALLER_ADDED_DATE);
    }

    @Test
    @Transactional
    void getAllLibraryPerProductsByAddedManuallyIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        // Get all the libraryPerProductList where addedManually equals to DEFAULT_ADDED_MANUALLY
        defaultLibraryPerProductShouldBeFound("addedManually.equals=" + DEFAULT_ADDED_MANUALLY);

        // Get all the libraryPerProductList where addedManually equals to UPDATED_ADDED_MANUALLY
        defaultLibraryPerProductShouldNotBeFound("addedManually.equals=" + UPDATED_ADDED_MANUALLY);
    }

    @Test
    @Transactional
    void getAllLibraryPerProductsByAddedManuallyIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        // Get all the libraryPerProductList where addedManually not equals to DEFAULT_ADDED_MANUALLY
        defaultLibraryPerProductShouldNotBeFound("addedManually.notEquals=" + DEFAULT_ADDED_MANUALLY);

        // Get all the libraryPerProductList where addedManually not equals to UPDATED_ADDED_MANUALLY
        defaultLibraryPerProductShouldBeFound("addedManually.notEquals=" + UPDATED_ADDED_MANUALLY);
    }

    @Test
    @Transactional
    void getAllLibraryPerProductsByAddedManuallyIsInShouldWork() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        // Get all the libraryPerProductList where addedManually in DEFAULT_ADDED_MANUALLY or UPDATED_ADDED_MANUALLY
        defaultLibraryPerProductShouldBeFound("addedManually.in=" + DEFAULT_ADDED_MANUALLY + "," + UPDATED_ADDED_MANUALLY);

        // Get all the libraryPerProductList where addedManually equals to UPDATED_ADDED_MANUALLY
        defaultLibraryPerProductShouldNotBeFound("addedManually.in=" + UPDATED_ADDED_MANUALLY);
    }

    @Test
    @Transactional
    void getAllLibraryPerProductsByAddedManuallyIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        // Get all the libraryPerProductList where addedManually is not null
        defaultLibraryPerProductShouldBeFound("addedManually.specified=true");

        // Get all the libraryPerProductList where addedManually is null
        defaultLibraryPerProductShouldNotBeFound("addedManually.specified=false");
    }

    @Test
    @Transactional
    void getAllLibraryPerProductsByHideForPublishingIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        // Get all the libraryPerProductList where hideForPublishing equals to DEFAULT_HIDE_FOR_PUBLISHING
        defaultLibraryPerProductShouldBeFound("hideForPublishing.equals=" + DEFAULT_HIDE_FOR_PUBLISHING);

        // Get all the libraryPerProductList where hideForPublishing equals to UPDATED_HIDE_FOR_PUBLISHING
        defaultLibraryPerProductShouldNotBeFound("hideForPublishing.equals=" + UPDATED_HIDE_FOR_PUBLISHING);
    }

    @Test
    @Transactional
    void getAllLibraryPerProductsByHideForPublishingIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        // Get all the libraryPerProductList where hideForPublishing not equals to DEFAULT_HIDE_FOR_PUBLISHING
        defaultLibraryPerProductShouldNotBeFound("hideForPublishing.notEquals=" + DEFAULT_HIDE_FOR_PUBLISHING);

        // Get all the libraryPerProductList where hideForPublishing not equals to UPDATED_HIDE_FOR_PUBLISHING
        defaultLibraryPerProductShouldBeFound("hideForPublishing.notEquals=" + UPDATED_HIDE_FOR_PUBLISHING);
    }

    @Test
    @Transactional
    void getAllLibraryPerProductsByHideForPublishingIsInShouldWork() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        // Get all the libraryPerProductList where hideForPublishing in DEFAULT_HIDE_FOR_PUBLISHING or UPDATED_HIDE_FOR_PUBLISHING
        defaultLibraryPerProductShouldBeFound("hideForPublishing.in=" + DEFAULT_HIDE_FOR_PUBLISHING + "," + UPDATED_HIDE_FOR_PUBLISHING);

        // Get all the libraryPerProductList where hideForPublishing equals to UPDATED_HIDE_FOR_PUBLISHING
        defaultLibraryPerProductShouldNotBeFound("hideForPublishing.in=" + UPDATED_HIDE_FOR_PUBLISHING);
    }

    @Test
    @Transactional
    void getAllLibraryPerProductsByHideForPublishingIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        // Get all the libraryPerProductList where hideForPublishing is not null
        defaultLibraryPerProductShouldBeFound("hideForPublishing.specified=true");

        // Get all the libraryPerProductList where hideForPublishing is null
        defaultLibraryPerProductShouldNotBeFound("hideForPublishing.specified=false");
    }

    @Test
    @Transactional
    void getAllLibraryPerProductsByLibraryIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);
        Library library;
        if (TestUtil.findAll(em, Library.class).isEmpty()) {
            library = LibraryResourceIT.createEntity(em);
            em.persist(library);
            em.flush();
        } else {
            library = TestUtil.findAll(em, Library.class).get(0);
        }
        em.persist(library);
        em.flush();
        libraryPerProduct.setLibrary(library);
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);
        Long libraryId = library.getId();

        // Get all the libraryPerProductList where library equals to libraryId
        defaultLibraryPerProductShouldBeFound("libraryId.equals=" + libraryId);

        // Get all the libraryPerProductList where library equals to (libraryId + 1)
        defaultLibraryPerProductShouldNotBeFound("libraryId.equals=" + (libraryId + 1));
    }

    @Test
    @Transactional
    void getAllLibraryPerProductsByProductIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        em.persist(product);
        em.flush();
        libraryPerProduct.setProduct(product);
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);
        Long productId = product.getId();

        // Get all the libraryPerProductList where product equals to productId
        defaultLibraryPerProductShouldBeFound("productId.equals=" + productId);

        // Get all the libraryPerProductList where product equals to (productId + 1)
        defaultLibraryPerProductShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLibraryPerProductShouldBeFound(String filter) throws Exception {
        restLibraryPerProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(libraryPerProduct.getId().intValue())))
            .andExpect(jsonPath("$.[*].addedDate").value(hasItem(DEFAULT_ADDED_DATE.toString())))
            .andExpect(jsonPath("$.[*].addedManually").value(hasItem(DEFAULT_ADDED_MANUALLY.booleanValue())))
            .andExpect(jsonPath("$.[*].hideForPublishing").value(hasItem(DEFAULT_HIDE_FOR_PUBLISHING.booleanValue())));

        // Check, that the count call also returns 1
        restLibraryPerProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLibraryPerProductShouldNotBeFound(String filter) throws Exception {
        restLibraryPerProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLibraryPerProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLibraryPerProduct() throws Exception {
        // Get the libraryPerProduct
        restLibraryPerProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLibraryPerProduct() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        int databaseSizeBeforeUpdate = libraryPerProductRepository.findAll().size();

        // Update the libraryPerProduct
        LibraryPerProduct updatedLibraryPerProduct = libraryPerProductRepository.findById(libraryPerProduct.getId()).get();
        // Disconnect from session so that the updates on updatedLibraryPerProduct are not directly saved in db
        em.detach(updatedLibraryPerProduct);
        updatedLibraryPerProduct
            .addedDate(UPDATED_ADDED_DATE)
            .addedManually(UPDATED_ADDED_MANUALLY)
            .hideForPublishing(UPDATED_HIDE_FOR_PUBLISHING);

        restLibraryPerProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLibraryPerProduct.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLibraryPerProduct))
            )
            .andExpect(status().isOk());

        // Validate the LibraryPerProduct in the database
        List<LibraryPerProduct> libraryPerProductList = libraryPerProductRepository.findAll();
        assertThat(libraryPerProductList).hasSize(databaseSizeBeforeUpdate);
        LibraryPerProduct testLibraryPerProduct = libraryPerProductList.get(libraryPerProductList.size() - 1);
        assertThat(testLibraryPerProduct.getAddedDate()).isEqualTo(UPDATED_ADDED_DATE);
        assertThat(testLibraryPerProduct.getAddedManually()).isEqualTo(UPDATED_ADDED_MANUALLY);
        assertThat(testLibraryPerProduct.getHideForPublishing()).isEqualTo(UPDATED_HIDE_FOR_PUBLISHING);
    }

    @Test
    @Transactional
    void putNonExistingLibraryPerProduct() throws Exception {
        int databaseSizeBeforeUpdate = libraryPerProductRepository.findAll().size();
        libraryPerProduct.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLibraryPerProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, libraryPerProduct.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(libraryPerProduct))
            )
            .andExpect(status().isBadRequest());

        // Validate the LibraryPerProduct in the database
        List<LibraryPerProduct> libraryPerProductList = libraryPerProductRepository.findAll();
        assertThat(libraryPerProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLibraryPerProduct() throws Exception {
        int databaseSizeBeforeUpdate = libraryPerProductRepository.findAll().size();
        libraryPerProduct.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryPerProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(libraryPerProduct))
            )
            .andExpect(status().isBadRequest());

        // Validate the LibraryPerProduct in the database
        List<LibraryPerProduct> libraryPerProductList = libraryPerProductRepository.findAll();
        assertThat(libraryPerProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLibraryPerProduct() throws Exception {
        int databaseSizeBeforeUpdate = libraryPerProductRepository.findAll().size();
        libraryPerProduct.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryPerProductMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(libraryPerProduct))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LibraryPerProduct in the database
        List<LibraryPerProduct> libraryPerProductList = libraryPerProductRepository.findAll();
        assertThat(libraryPerProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLibraryPerProductWithPatch() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        int databaseSizeBeforeUpdate = libraryPerProductRepository.findAll().size();

        // Update the libraryPerProduct using partial update
        LibraryPerProduct partialUpdatedLibraryPerProduct = new LibraryPerProduct();
        partialUpdatedLibraryPerProduct.setId(libraryPerProduct.getId());

        partialUpdatedLibraryPerProduct.addedDate(UPDATED_ADDED_DATE);

        restLibraryPerProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLibraryPerProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLibraryPerProduct))
            )
            .andExpect(status().isOk());

        // Validate the LibraryPerProduct in the database
        List<LibraryPerProduct> libraryPerProductList = libraryPerProductRepository.findAll();
        assertThat(libraryPerProductList).hasSize(databaseSizeBeforeUpdate);
        LibraryPerProduct testLibraryPerProduct = libraryPerProductList.get(libraryPerProductList.size() - 1);
        assertThat(testLibraryPerProduct.getAddedDate()).isEqualTo(UPDATED_ADDED_DATE);
        assertThat(testLibraryPerProduct.getAddedManually()).isEqualTo(DEFAULT_ADDED_MANUALLY);
        assertThat(testLibraryPerProduct.getHideForPublishing()).isEqualTo(DEFAULT_HIDE_FOR_PUBLISHING);
    }

    @Test
    @Transactional
    void fullUpdateLibraryPerProductWithPatch() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        int databaseSizeBeforeUpdate = libraryPerProductRepository.findAll().size();

        // Update the libraryPerProduct using partial update
        LibraryPerProduct partialUpdatedLibraryPerProduct = new LibraryPerProduct();
        partialUpdatedLibraryPerProduct.setId(libraryPerProduct.getId());

        partialUpdatedLibraryPerProduct
            .addedDate(UPDATED_ADDED_DATE)
            .addedManually(UPDATED_ADDED_MANUALLY)
            .hideForPublishing(UPDATED_HIDE_FOR_PUBLISHING);

        restLibraryPerProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLibraryPerProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLibraryPerProduct))
            )
            .andExpect(status().isOk());

        // Validate the LibraryPerProduct in the database
        List<LibraryPerProduct> libraryPerProductList = libraryPerProductRepository.findAll();
        assertThat(libraryPerProductList).hasSize(databaseSizeBeforeUpdate);
        LibraryPerProduct testLibraryPerProduct = libraryPerProductList.get(libraryPerProductList.size() - 1);
        assertThat(testLibraryPerProduct.getAddedDate()).isEqualTo(UPDATED_ADDED_DATE);
        assertThat(testLibraryPerProduct.getAddedManually()).isEqualTo(UPDATED_ADDED_MANUALLY);
        assertThat(testLibraryPerProduct.getHideForPublishing()).isEqualTo(UPDATED_HIDE_FOR_PUBLISHING);
    }

    @Test
    @Transactional
    void patchNonExistingLibraryPerProduct() throws Exception {
        int databaseSizeBeforeUpdate = libraryPerProductRepository.findAll().size();
        libraryPerProduct.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLibraryPerProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, libraryPerProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(libraryPerProduct))
            )
            .andExpect(status().isBadRequest());

        // Validate the LibraryPerProduct in the database
        List<LibraryPerProduct> libraryPerProductList = libraryPerProductRepository.findAll();
        assertThat(libraryPerProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLibraryPerProduct() throws Exception {
        int databaseSizeBeforeUpdate = libraryPerProductRepository.findAll().size();
        libraryPerProduct.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryPerProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(libraryPerProduct))
            )
            .andExpect(status().isBadRequest());

        // Validate the LibraryPerProduct in the database
        List<LibraryPerProduct> libraryPerProductList = libraryPerProductRepository.findAll();
        assertThat(libraryPerProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLibraryPerProduct() throws Exception {
        int databaseSizeBeforeUpdate = libraryPerProductRepository.findAll().size();
        libraryPerProduct.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryPerProductMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(libraryPerProduct))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LibraryPerProduct in the database
        List<LibraryPerProduct> libraryPerProductList = libraryPerProductRepository.findAll();
        assertThat(libraryPerProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLibraryPerProduct() throws Exception {
        // Initialize the database
        libraryPerProductRepository.saveAndFlush(libraryPerProduct);

        int databaseSizeBeforeDelete = libraryPerProductRepository.findAll().size();

        // Delete the libraryPerProduct
        restLibraryPerProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, libraryPerProduct.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LibraryPerProduct> libraryPerProductList = libraryPerProductRepository.findAll();
        assertThat(libraryPerProductList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
