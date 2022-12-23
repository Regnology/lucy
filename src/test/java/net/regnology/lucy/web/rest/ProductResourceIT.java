package net.regnology.lucy.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import net.regnology.lucy.IntegrationTest;
import net.regnology.lucy.domain.LibraryPerProduct;
import net.regnology.lucy.domain.Product;
import net.regnology.lucy.domain.enumeration.UploadState;
import net.regnology.lucy.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProductResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_IDENTIFIER = "AAAAAAAAAA";
    private static final String UPDATED_IDENTIFIER = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CREATED_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_LAST_UPDATED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_LAST_UPDATED_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_TARGET_URL = "AAAAAAAAAA";
    private static final String UPDATED_TARGET_URL = "BBBBBBBBBB";

    private static final UploadState DEFAULT_UPLOAD_STATE = UploadState.SUCCESSFUL;
    private static final UploadState UPDATED_UPLOAD_STATE = UploadState.PROCESSING;

    private static final String DEFAULT_DISCLAIMER = "AAAAAAAAAA";
    private static final String UPDATED_DISCLAIMER = "BBBBBBBBBB";

    private static final Boolean DEFAULT_DELIVERED = false;
    private static final Boolean UPDATED_DELIVERED = true;

    private static final Instant DEFAULT_DELIVERED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELIVERED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CONTACT = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final Long DEFAULT_PREVIOUS_PRODUCT_ID = 1L;
    private static final Long UPDATED_PREVIOUS_PRODUCT_ID = 2L;
    private static final Long SMALLER_PREVIOUS_PRODUCT_ID = 1 - 1L;

    private static final String DEFAULT_UPLOAD_FILTER = "AAAAAAAAAA";
    private static final String UPDATED_UPLOAD_FILTER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductMockMvc;

    private Product product;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createEntity(EntityManager em) {
        Product product = new Product()
            .name(DEFAULT_NAME)
            .identifier(DEFAULT_IDENTIFIER)
            .version(DEFAULT_VERSION)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastUpdatedDate(DEFAULT_LAST_UPDATED_DATE)
            .targetUrl(DEFAULT_TARGET_URL)
            .uploadState(DEFAULT_UPLOAD_STATE)
            .disclaimer(DEFAULT_DISCLAIMER)
            .delivered(DEFAULT_DELIVERED)
            .deliveredDate(DEFAULT_DELIVERED_DATE)
            .contact(DEFAULT_CONTACT)
            .comment(DEFAULT_COMMENT)
            .previousProductId(DEFAULT_PREVIOUS_PRODUCT_ID)
            .uploadFilter(DEFAULT_UPLOAD_FILTER);
        return product;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createUpdatedEntity(EntityManager em) {
        Product product = new Product()
            .name(UPDATED_NAME)
            .identifier(UPDATED_IDENTIFIER)
            .version(UPDATED_VERSION)
            .createdDate(UPDATED_CREATED_DATE)
            .lastUpdatedDate(UPDATED_LAST_UPDATED_DATE)
            .targetUrl(UPDATED_TARGET_URL)
            .uploadState(UPDATED_UPLOAD_STATE)
            .disclaimer(UPDATED_DISCLAIMER)
            .delivered(UPDATED_DELIVERED)
            .deliveredDate(UPDATED_DELIVERED_DATE)
            .contact(UPDATED_CONTACT)
            .comment(UPDATED_COMMENT)
            .previousProductId(UPDATED_PREVIOUS_PRODUCT_ID)
            .uploadFilter(UPDATED_UPLOAD_FILTER);
        return product;
    }

    @BeforeEach
    public void initTest() {
        product = createEntity(em);
    }

    @Test
    @Transactional
    void createProduct() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();
        // Create the Product
        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isCreated());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate + 1);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProduct.getIdentifier()).isEqualTo(DEFAULT_IDENTIFIER);
        assertThat(testProduct.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testProduct.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testProduct.getLastUpdatedDate()).isEqualTo(DEFAULT_LAST_UPDATED_DATE);
        assertThat(testProduct.getTargetUrl()).isEqualTo(DEFAULT_TARGET_URL);
        assertThat(testProduct.getUploadState()).isEqualTo(DEFAULT_UPLOAD_STATE);
        assertThat(testProduct.getDisclaimer()).isEqualTo(DEFAULT_DISCLAIMER);
        assertThat(testProduct.getDelivered()).isEqualTo(DEFAULT_DELIVERED);
        assertThat(testProduct.getDeliveredDate()).isEqualTo(DEFAULT_DELIVERED_DATE);
        assertThat(testProduct.getContact()).isEqualTo(DEFAULT_CONTACT);
        assertThat(testProduct.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testProduct.getPreviousProductId()).isEqualTo(DEFAULT_PREVIOUS_PRODUCT_ID);
        assertThat(testProduct.getUploadFilter()).isEqualTo(DEFAULT_UPLOAD_FILTER);
    }

    @Test
    @Transactional
    void createProductWithExistingId() throws Exception {
        // Create the Product with an existing ID
        product.setId(1L);

        int databaseSizeBeforeCreate = productRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setName(null);

        // Create the Product, which fails.

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIdentifierIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setIdentifier(null);

        // Create the Product, which fails.

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVersionIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setVersion(null);

        // Create the Product, which fails.

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProducts() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].identifier").value(hasItem(DEFAULT_IDENTIFIER)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastUpdatedDate").value(hasItem(DEFAULT_LAST_UPDATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].targetUrl").value(hasItem(DEFAULT_TARGET_URL)))
            .andExpect(jsonPath("$.[*].uploadState").value(hasItem(DEFAULT_UPLOAD_STATE.toString())))
            .andExpect(jsonPath("$.[*].disclaimer").value(hasItem(DEFAULT_DISCLAIMER.toString())))
            .andExpect(jsonPath("$.[*].delivered").value(hasItem(DEFAULT_DELIVERED.booleanValue())))
            .andExpect(jsonPath("$.[*].deliveredDate").value(hasItem(DEFAULT_DELIVERED_DATE.toString())))
            .andExpect(jsonPath("$.[*].contact").value(hasItem(DEFAULT_CONTACT)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].previousProductId").value(hasItem(DEFAULT_PREVIOUS_PRODUCT_ID)))
            .andExpect(jsonPath("$.[*].uploadFilter").value(hasItem(DEFAULT_UPLOAD_FILTER)));
    }

    @Test
    @Transactional
    void getProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get the product
        restProductMockMvc
            .perform(get(ENTITY_API_URL_ID, product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(product.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.identifier").value(DEFAULT_IDENTIFIER))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastUpdatedDate").value(DEFAULT_LAST_UPDATED_DATE.toString()))
            .andExpect(jsonPath("$.targetUrl").value(DEFAULT_TARGET_URL))
            .andExpect(jsonPath("$.uploadState").value(DEFAULT_UPLOAD_STATE.toString()))
            .andExpect(jsonPath("$.disclaimer").value(DEFAULT_DISCLAIMER.toString()))
            .andExpect(jsonPath("$.delivered").value(DEFAULT_DELIVERED.booleanValue()))
            .andExpect(jsonPath("$.deliveredDate").value(DEFAULT_DELIVERED_DATE.toString()))
            .andExpect(jsonPath("$.contact").value(DEFAULT_CONTACT))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT))
            .andExpect(jsonPath("$.previousProductId").value(DEFAULT_PREVIOUS_PRODUCT_ID))
            .andExpect(jsonPath("$.uploadFilter").value(DEFAULT_UPLOAD_FILTER));
    }

    @Test
    @Transactional
    void getProductsByIdFiltering() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        Long id = product.getId();

        defaultProductShouldBeFound("id.equals=" + id);
        defaultProductShouldNotBeFound("id.notEquals=" + id);

        defaultProductShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProductShouldNotBeFound("id.greaterThan=" + id);

        defaultProductShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProductShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name equals to DEFAULT_NAME
        defaultProductShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the productList where name equals to UPDATED_NAME
        defaultProductShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name not equals to DEFAULT_NAME
        defaultProductShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the productList where name not equals to UPDATED_NAME
        defaultProductShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name in DEFAULT_NAME or UPDATED_NAME
        defaultProductShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the productList where name equals to UPDATED_NAME
        defaultProductShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name is not null
        defaultProductShouldBeFound("name.specified=true");

        // Get all the productList where name is null
        defaultProductShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByNameContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name contains DEFAULT_NAME
        defaultProductShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the productList where name contains UPDATED_NAME
        defaultProductShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name does not contain DEFAULT_NAME
        defaultProductShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the productList where name does not contain UPDATED_NAME
        defaultProductShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByIdentifierIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where identifier equals to DEFAULT_IDENTIFIER
        defaultProductShouldBeFound("identifier.equals=" + DEFAULT_IDENTIFIER);

        // Get all the productList where identifier equals to UPDATED_IDENTIFIER
        defaultProductShouldNotBeFound("identifier.equals=" + UPDATED_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllProductsByIdentifierIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where identifier not equals to DEFAULT_IDENTIFIER
        defaultProductShouldNotBeFound("identifier.notEquals=" + DEFAULT_IDENTIFIER);

        // Get all the productList where identifier not equals to UPDATED_IDENTIFIER
        defaultProductShouldBeFound("identifier.notEquals=" + UPDATED_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllProductsByIdentifierIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where identifier in DEFAULT_IDENTIFIER or UPDATED_IDENTIFIER
        defaultProductShouldBeFound("identifier.in=" + DEFAULT_IDENTIFIER + "," + UPDATED_IDENTIFIER);

        // Get all the productList where identifier equals to UPDATED_IDENTIFIER
        defaultProductShouldNotBeFound("identifier.in=" + UPDATED_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllProductsByIdentifierIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where identifier is not null
        defaultProductShouldBeFound("identifier.specified=true");

        // Get all the productList where identifier is null
        defaultProductShouldNotBeFound("identifier.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByIdentifierContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where identifier contains DEFAULT_IDENTIFIER
        defaultProductShouldBeFound("identifier.contains=" + DEFAULT_IDENTIFIER);

        // Get all the productList where identifier contains UPDATED_IDENTIFIER
        defaultProductShouldNotBeFound("identifier.contains=" + UPDATED_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllProductsByIdentifierNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where identifier does not contain DEFAULT_IDENTIFIER
        defaultProductShouldNotBeFound("identifier.doesNotContain=" + DEFAULT_IDENTIFIER);

        // Get all the productList where identifier does not contain UPDATED_IDENTIFIER
        defaultProductShouldBeFound("identifier.doesNotContain=" + UPDATED_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllProductsByVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where version equals to DEFAULT_VERSION
        defaultProductShouldBeFound("version.equals=" + DEFAULT_VERSION);

        // Get all the productList where version equals to UPDATED_VERSION
        defaultProductShouldNotBeFound("version.equals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllProductsByVersionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where version not equals to DEFAULT_VERSION
        defaultProductShouldNotBeFound("version.notEquals=" + DEFAULT_VERSION);

        // Get all the productList where version not equals to UPDATED_VERSION
        defaultProductShouldBeFound("version.notEquals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllProductsByVersionIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where version in DEFAULT_VERSION or UPDATED_VERSION
        defaultProductShouldBeFound("version.in=" + DEFAULT_VERSION + "," + UPDATED_VERSION);

        // Get all the productList where version equals to UPDATED_VERSION
        defaultProductShouldNotBeFound("version.in=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllProductsByVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where version is not null
        defaultProductShouldBeFound("version.specified=true");

        // Get all the productList where version is null
        defaultProductShouldNotBeFound("version.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByVersionContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where version contains DEFAULT_VERSION
        defaultProductShouldBeFound("version.contains=" + DEFAULT_VERSION);

        // Get all the productList where version contains UPDATED_VERSION
        defaultProductShouldNotBeFound("version.contains=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllProductsByVersionNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where version does not contain DEFAULT_VERSION
        defaultProductShouldNotBeFound("version.doesNotContain=" + DEFAULT_VERSION);

        // Get all the productList where version does not contain UPDATED_VERSION
        defaultProductShouldBeFound("version.doesNotContain=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdDate equals to DEFAULT_CREATED_DATE
        defaultProductShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the productList where createdDate equals to UPDATED_CREATED_DATE
        defaultProductShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdDate not equals to DEFAULT_CREATED_DATE
        defaultProductShouldNotBeFound("createdDate.notEquals=" + DEFAULT_CREATED_DATE);

        // Get all the productList where createdDate not equals to UPDATED_CREATED_DATE
        defaultProductShouldBeFound("createdDate.notEquals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultProductShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the productList where createdDate equals to UPDATED_CREATED_DATE
        defaultProductShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdDate is not null
        defaultProductShouldBeFound("createdDate.specified=true");

        // Get all the productList where createdDate is null
        defaultProductShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByCreatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdDate is greater than or equal to DEFAULT_CREATED_DATE
        defaultProductShouldBeFound("createdDate.greaterThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the productList where createdDate is greater than or equal to UPDATED_CREATED_DATE
        defaultProductShouldNotBeFound("createdDate.greaterThanOrEqual=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdDate is less than or equal to DEFAULT_CREATED_DATE
        defaultProductShouldBeFound("createdDate.lessThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the productList where createdDate is less than or equal to SMALLER_CREATED_DATE
        defaultProductShouldNotBeFound("createdDate.lessThanOrEqual=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdDate is less than DEFAULT_CREATED_DATE
        defaultProductShouldNotBeFound("createdDate.lessThan=" + DEFAULT_CREATED_DATE);

        // Get all the productList where createdDate is less than UPDATED_CREATED_DATE
        defaultProductShouldBeFound("createdDate.lessThan=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByCreatedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where createdDate is greater than DEFAULT_CREATED_DATE
        defaultProductShouldNotBeFound("createdDate.greaterThan=" + DEFAULT_CREATED_DATE);

        // Get all the productList where createdDate is greater than SMALLER_CREATED_DATE
        defaultProductShouldBeFound("createdDate.greaterThan=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByLastUpdatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where lastUpdatedDate equals to DEFAULT_LAST_UPDATED_DATE
        defaultProductShouldBeFound("lastUpdatedDate.equals=" + DEFAULT_LAST_UPDATED_DATE);

        // Get all the productList where lastUpdatedDate equals to UPDATED_LAST_UPDATED_DATE
        defaultProductShouldNotBeFound("lastUpdatedDate.equals=" + UPDATED_LAST_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByLastUpdatedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where lastUpdatedDate not equals to DEFAULT_LAST_UPDATED_DATE
        defaultProductShouldNotBeFound("lastUpdatedDate.notEquals=" + DEFAULT_LAST_UPDATED_DATE);

        // Get all the productList where lastUpdatedDate not equals to UPDATED_LAST_UPDATED_DATE
        defaultProductShouldBeFound("lastUpdatedDate.notEquals=" + UPDATED_LAST_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByLastUpdatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where lastUpdatedDate in DEFAULT_LAST_UPDATED_DATE or UPDATED_LAST_UPDATED_DATE
        defaultProductShouldBeFound("lastUpdatedDate.in=" + DEFAULT_LAST_UPDATED_DATE + "," + UPDATED_LAST_UPDATED_DATE);

        // Get all the productList where lastUpdatedDate equals to UPDATED_LAST_UPDATED_DATE
        defaultProductShouldNotBeFound("lastUpdatedDate.in=" + UPDATED_LAST_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByLastUpdatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where lastUpdatedDate is not null
        defaultProductShouldBeFound("lastUpdatedDate.specified=true");

        // Get all the productList where lastUpdatedDate is null
        defaultProductShouldNotBeFound("lastUpdatedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByLastUpdatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where lastUpdatedDate is greater than or equal to DEFAULT_LAST_UPDATED_DATE
        defaultProductShouldBeFound("lastUpdatedDate.greaterThanOrEqual=" + DEFAULT_LAST_UPDATED_DATE);

        // Get all the productList where lastUpdatedDate is greater than or equal to UPDATED_LAST_UPDATED_DATE
        defaultProductShouldNotBeFound("lastUpdatedDate.greaterThanOrEqual=" + UPDATED_LAST_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByLastUpdatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where lastUpdatedDate is less than or equal to DEFAULT_LAST_UPDATED_DATE
        defaultProductShouldBeFound("lastUpdatedDate.lessThanOrEqual=" + DEFAULT_LAST_UPDATED_DATE);

        // Get all the productList where lastUpdatedDate is less than or equal to SMALLER_LAST_UPDATED_DATE
        defaultProductShouldNotBeFound("lastUpdatedDate.lessThanOrEqual=" + SMALLER_LAST_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByLastUpdatedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where lastUpdatedDate is less than DEFAULT_LAST_UPDATED_DATE
        defaultProductShouldNotBeFound("lastUpdatedDate.lessThan=" + DEFAULT_LAST_UPDATED_DATE);

        // Get all the productList where lastUpdatedDate is less than UPDATED_LAST_UPDATED_DATE
        defaultProductShouldBeFound("lastUpdatedDate.lessThan=" + UPDATED_LAST_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByLastUpdatedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where lastUpdatedDate is greater than DEFAULT_LAST_UPDATED_DATE
        defaultProductShouldNotBeFound("lastUpdatedDate.greaterThan=" + DEFAULT_LAST_UPDATED_DATE);

        // Get all the productList where lastUpdatedDate is greater than SMALLER_LAST_UPDATED_DATE
        defaultProductShouldBeFound("lastUpdatedDate.greaterThan=" + SMALLER_LAST_UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByTargetUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where targetUrl equals to DEFAULT_TARGET_URL
        defaultProductShouldBeFound("targetUrl.equals=" + DEFAULT_TARGET_URL);

        // Get all the productList where targetUrl equals to UPDATED_TARGET_URL
        defaultProductShouldNotBeFound("targetUrl.equals=" + UPDATED_TARGET_URL);
    }

    @Test
    @Transactional
    void getAllProductsByTargetUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where targetUrl not equals to DEFAULT_TARGET_URL
        defaultProductShouldNotBeFound("targetUrl.notEquals=" + DEFAULT_TARGET_URL);

        // Get all the productList where targetUrl not equals to UPDATED_TARGET_URL
        defaultProductShouldBeFound("targetUrl.notEquals=" + UPDATED_TARGET_URL);
    }

    @Test
    @Transactional
    void getAllProductsByTargetUrlIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where targetUrl in DEFAULT_TARGET_URL or UPDATED_TARGET_URL
        defaultProductShouldBeFound("targetUrl.in=" + DEFAULT_TARGET_URL + "," + UPDATED_TARGET_URL);

        // Get all the productList where targetUrl equals to UPDATED_TARGET_URL
        defaultProductShouldNotBeFound("targetUrl.in=" + UPDATED_TARGET_URL);
    }

    @Test
    @Transactional
    void getAllProductsByTargetUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where targetUrl is not null
        defaultProductShouldBeFound("targetUrl.specified=true");

        // Get all the productList where targetUrl is null
        defaultProductShouldNotBeFound("targetUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByTargetUrlContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where targetUrl contains DEFAULT_TARGET_URL
        defaultProductShouldBeFound("targetUrl.contains=" + DEFAULT_TARGET_URL);

        // Get all the productList where targetUrl contains UPDATED_TARGET_URL
        defaultProductShouldNotBeFound("targetUrl.contains=" + UPDATED_TARGET_URL);
    }

    @Test
    @Transactional
    void getAllProductsByTargetUrlNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where targetUrl does not contain DEFAULT_TARGET_URL
        defaultProductShouldNotBeFound("targetUrl.doesNotContain=" + DEFAULT_TARGET_URL);

        // Get all the productList where targetUrl does not contain UPDATED_TARGET_URL
        defaultProductShouldBeFound("targetUrl.doesNotContain=" + UPDATED_TARGET_URL);
    }

    @Test
    @Transactional
    void getAllProductsByUploadStateIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where uploadState equals to DEFAULT_UPLOAD_STATE
        defaultProductShouldBeFound("uploadState.equals=" + DEFAULT_UPLOAD_STATE);

        // Get all the productList where uploadState equals to UPDATED_UPLOAD_STATE
        defaultProductShouldNotBeFound("uploadState.equals=" + UPDATED_UPLOAD_STATE);
    }

    @Test
    @Transactional
    void getAllProductsByUploadStateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where uploadState not equals to DEFAULT_UPLOAD_STATE
        defaultProductShouldNotBeFound("uploadState.notEquals=" + DEFAULT_UPLOAD_STATE);

        // Get all the productList where uploadState not equals to UPDATED_UPLOAD_STATE
        defaultProductShouldBeFound("uploadState.notEquals=" + UPDATED_UPLOAD_STATE);
    }

    @Test
    @Transactional
    void getAllProductsByUploadStateIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where uploadState in DEFAULT_UPLOAD_STATE or UPDATED_UPLOAD_STATE
        defaultProductShouldBeFound("uploadState.in=" + DEFAULT_UPLOAD_STATE + "," + UPDATED_UPLOAD_STATE);

        // Get all the productList where uploadState equals to UPDATED_UPLOAD_STATE
        defaultProductShouldNotBeFound("uploadState.in=" + UPDATED_UPLOAD_STATE);
    }

    @Test
    @Transactional
    void getAllProductsByUploadStateIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where uploadState is not null
        defaultProductShouldBeFound("uploadState.specified=true");

        // Get all the productList where uploadState is null
        defaultProductShouldNotBeFound("uploadState.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByDeliveredIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where delivered equals to DEFAULT_DELIVERED
        defaultProductShouldBeFound("delivered.equals=" + DEFAULT_DELIVERED);

        // Get all the productList where delivered equals to UPDATED_DELIVERED
        defaultProductShouldNotBeFound("delivered.equals=" + UPDATED_DELIVERED);
    }

    @Test
    @Transactional
    void getAllProductsByDeliveredIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where delivered not equals to DEFAULT_DELIVERED
        defaultProductShouldNotBeFound("delivered.notEquals=" + DEFAULT_DELIVERED);

        // Get all the productList where delivered not equals to UPDATED_DELIVERED
        defaultProductShouldBeFound("delivered.notEquals=" + UPDATED_DELIVERED);
    }

    @Test
    @Transactional
    void getAllProductsByDeliveredIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where delivered in DEFAULT_DELIVERED or UPDATED_DELIVERED
        defaultProductShouldBeFound("delivered.in=" + DEFAULT_DELIVERED + "," + UPDATED_DELIVERED);

        // Get all the productList where delivered equals to UPDATED_DELIVERED
        defaultProductShouldNotBeFound("delivered.in=" + UPDATED_DELIVERED);
    }

    @Test
    @Transactional
    void getAllProductsByDeliveredIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where delivered is not null
        defaultProductShouldBeFound("delivered.specified=true");

        // Get all the productList where delivered is null
        defaultProductShouldNotBeFound("delivered.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByDeliveredDateIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where deliveredDate equals to DEFAULT_DELIVERED_DATE
        defaultProductShouldBeFound("deliveredDate.equals=" + DEFAULT_DELIVERED_DATE);

        // Get all the productList where deliveredDate equals to UPDATED_DELIVERED_DATE
        defaultProductShouldNotBeFound("deliveredDate.equals=" + UPDATED_DELIVERED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByDeliveredDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where deliveredDate not equals to DEFAULT_DELIVERED_DATE
        defaultProductShouldNotBeFound("deliveredDate.notEquals=" + DEFAULT_DELIVERED_DATE);

        // Get all the productList where deliveredDate not equals to UPDATED_DELIVERED_DATE
        defaultProductShouldBeFound("deliveredDate.notEquals=" + UPDATED_DELIVERED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByDeliveredDateIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where deliveredDate in DEFAULT_DELIVERED_DATE or UPDATED_DELIVERED_DATE
        defaultProductShouldBeFound("deliveredDate.in=" + DEFAULT_DELIVERED_DATE + "," + UPDATED_DELIVERED_DATE);

        // Get all the productList where deliveredDate equals to UPDATED_DELIVERED_DATE
        defaultProductShouldNotBeFound("deliveredDate.in=" + UPDATED_DELIVERED_DATE);
    }

    @Test
    @Transactional
    void getAllProductsByDeliveredDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where deliveredDate is not null
        defaultProductShouldBeFound("deliveredDate.specified=true");

        // Get all the productList where deliveredDate is null
        defaultProductShouldNotBeFound("deliveredDate.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByContactIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where contact equals to DEFAULT_CONTACT
        defaultProductShouldBeFound("contact.equals=" + DEFAULT_CONTACT);

        // Get all the productList where contact equals to UPDATED_CONTACT
        defaultProductShouldNotBeFound("contact.equals=" + UPDATED_CONTACT);
    }

    @Test
    @Transactional
    void getAllProductsByContactIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where contact not equals to DEFAULT_CONTACT
        defaultProductShouldNotBeFound("contact.notEquals=" + DEFAULT_CONTACT);

        // Get all the productList where contact not equals to UPDATED_CONTACT
        defaultProductShouldBeFound("contact.notEquals=" + UPDATED_CONTACT);
    }

    @Test
    @Transactional
    void getAllProductsByContactIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where contact in DEFAULT_CONTACT or UPDATED_CONTACT
        defaultProductShouldBeFound("contact.in=" + DEFAULT_CONTACT + "," + UPDATED_CONTACT);

        // Get all the productList where contact equals to UPDATED_CONTACT
        defaultProductShouldNotBeFound("contact.in=" + UPDATED_CONTACT);
    }

    @Test
    @Transactional
    void getAllProductsByContactIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where contact is not null
        defaultProductShouldBeFound("contact.specified=true");

        // Get all the productList where contact is null
        defaultProductShouldNotBeFound("contact.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByContactContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where contact contains DEFAULT_CONTACT
        defaultProductShouldBeFound("contact.contains=" + DEFAULT_CONTACT);

        // Get all the productList where contact contains UPDATED_CONTACT
        defaultProductShouldNotBeFound("contact.contains=" + UPDATED_CONTACT);
    }

    @Test
    @Transactional
    void getAllProductsByContactNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where contact does not contain DEFAULT_CONTACT
        defaultProductShouldNotBeFound("contact.doesNotContain=" + DEFAULT_CONTACT);

        // Get all the productList where contact does not contain UPDATED_CONTACT
        defaultProductShouldBeFound("contact.doesNotContain=" + UPDATED_CONTACT);
    }

    @Test
    @Transactional
    void getAllProductsByCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where comment equals to DEFAULT_COMMENT
        defaultProductShouldBeFound("comment.equals=" + DEFAULT_COMMENT);

        // Get all the productList where comment equals to UPDATED_COMMENT
        defaultProductShouldNotBeFound("comment.equals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllProductsByCommentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where comment not equals to DEFAULT_COMMENT
        defaultProductShouldNotBeFound("comment.notEquals=" + DEFAULT_COMMENT);

        // Get all the productList where comment not equals to UPDATED_COMMENT
        defaultProductShouldBeFound("comment.notEquals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllProductsByCommentIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where comment in DEFAULT_COMMENT or UPDATED_COMMENT
        defaultProductShouldBeFound("comment.in=" + DEFAULT_COMMENT + "," + UPDATED_COMMENT);

        // Get all the productList where comment equals to UPDATED_COMMENT
        defaultProductShouldNotBeFound("comment.in=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllProductsByCommentIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where comment is not null
        defaultProductShouldBeFound("comment.specified=true");

        // Get all the productList where comment is null
        defaultProductShouldNotBeFound("comment.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByCommentContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where comment contains DEFAULT_COMMENT
        defaultProductShouldBeFound("comment.contains=" + DEFAULT_COMMENT);

        // Get all the productList where comment contains UPDATED_COMMENT
        defaultProductShouldNotBeFound("comment.contains=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllProductsByCommentNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where comment does not contain DEFAULT_COMMENT
        defaultProductShouldNotBeFound("comment.doesNotContain=" + DEFAULT_COMMENT);

        // Get all the productList where comment does not contain UPDATED_COMMENT
        defaultProductShouldBeFound("comment.doesNotContain=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllProductsByPreviousProductIdIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where previousProductId equals to DEFAULT_PREVIOUS_PRODUCT_ID
        defaultProductShouldBeFound("previousProductId.equals=" + DEFAULT_PREVIOUS_PRODUCT_ID);

        // Get all the productList where previousProductId equals to UPDATED_PREVIOUS_PRODUCT_ID
        defaultProductShouldNotBeFound("previousProductId.equals=" + UPDATED_PREVIOUS_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllProductsByPreviousProductIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where previousProductId not equals to DEFAULT_PREVIOUS_PRODUCT_ID
        defaultProductShouldNotBeFound("previousProductId.notEquals=" + DEFAULT_PREVIOUS_PRODUCT_ID);

        // Get all the productList where previousProductId not equals to UPDATED_PREVIOUS_PRODUCT_ID
        defaultProductShouldBeFound("previousProductId.notEquals=" + UPDATED_PREVIOUS_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllProductsByPreviousProductIdIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where previousProductId in DEFAULT_PREVIOUS_PRODUCT_ID or UPDATED_PREVIOUS_PRODUCT_ID
        defaultProductShouldBeFound("previousProductId.in=" + DEFAULT_PREVIOUS_PRODUCT_ID + "," + UPDATED_PREVIOUS_PRODUCT_ID);

        // Get all the productList where previousProductId equals to UPDATED_PREVIOUS_PRODUCT_ID
        defaultProductShouldNotBeFound("previousProductId.in=" + UPDATED_PREVIOUS_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllProductsByPreviousProductIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where previousProductId is not null
        defaultProductShouldBeFound("previousProductId.specified=true");

        // Get all the productList where previousProductId is null
        defaultProductShouldNotBeFound("previousProductId.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByPreviousProductIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where previousProductId is greater than or equal to DEFAULT_PREVIOUS_PRODUCT_ID
        defaultProductShouldBeFound("previousProductId.greaterThanOrEqual=" + DEFAULT_PREVIOUS_PRODUCT_ID);

        // Get all the productList where previousProductId is greater than or equal to UPDATED_PREVIOUS_PRODUCT_ID
        defaultProductShouldNotBeFound("previousProductId.greaterThanOrEqual=" + UPDATED_PREVIOUS_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllProductsByPreviousProductIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where previousProductId is less than or equal to DEFAULT_PREVIOUS_PRODUCT_ID
        defaultProductShouldBeFound("previousProductId.lessThanOrEqual=" + DEFAULT_PREVIOUS_PRODUCT_ID);

        // Get all the productList where previousProductId is less than or equal to SMALLER_PREVIOUS_PRODUCT_ID
        defaultProductShouldNotBeFound("previousProductId.lessThanOrEqual=" + SMALLER_PREVIOUS_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllProductsByPreviousProductIdIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where previousProductId is less than DEFAULT_PREVIOUS_PRODUCT_ID
        defaultProductShouldNotBeFound("previousProductId.lessThan=" + DEFAULT_PREVIOUS_PRODUCT_ID);

        // Get all the productList where previousProductId is less than UPDATED_PREVIOUS_PRODUCT_ID
        defaultProductShouldBeFound("previousProductId.lessThan=" + UPDATED_PREVIOUS_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllProductsByPreviousProductIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where previousProductId is greater than DEFAULT_PREVIOUS_PRODUCT_ID
        defaultProductShouldNotBeFound("previousProductId.greaterThan=" + DEFAULT_PREVIOUS_PRODUCT_ID);

        // Get all the productList where previousProductId is greater than SMALLER_PREVIOUS_PRODUCT_ID
        defaultProductShouldBeFound("previousProductId.greaterThan=" + SMALLER_PREVIOUS_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllProductsByUploadFilterIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where uploadFilter equals to DEFAULT_UPLOAD_FILTER
        defaultProductShouldBeFound("uploadFilter.equals=" + DEFAULT_UPLOAD_FILTER);

        // Get all the productList where uploadFilter equals to UPDATED_UPLOAD_FILTER
        defaultProductShouldNotBeFound("uploadFilter.equals=" + UPDATED_UPLOAD_FILTER);
    }

    @Test
    @Transactional
    void getAllProductsByUploadFilterIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where uploadFilter not equals to DEFAULT_UPLOAD_FILTER
        defaultProductShouldNotBeFound("uploadFilter.notEquals=" + DEFAULT_UPLOAD_FILTER);

        // Get all the productList where uploadFilter not equals to UPDATED_UPLOAD_FILTER
        defaultProductShouldBeFound("uploadFilter.notEquals=" + UPDATED_UPLOAD_FILTER);
    }

    @Test
    @Transactional
    void getAllProductsByUploadFilterIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where uploadFilter in DEFAULT_UPLOAD_FILTER or UPDATED_UPLOAD_FILTER
        defaultProductShouldBeFound("uploadFilter.in=" + DEFAULT_UPLOAD_FILTER + "," + UPDATED_UPLOAD_FILTER);

        // Get all the productList where uploadFilter equals to UPDATED_UPLOAD_FILTER
        defaultProductShouldNotBeFound("uploadFilter.in=" + UPDATED_UPLOAD_FILTER);
    }

    @Test
    @Transactional
    void getAllProductsByUploadFilterIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where uploadFilter is not null
        defaultProductShouldBeFound("uploadFilter.specified=true");

        // Get all the productList where uploadFilter is null
        defaultProductShouldNotBeFound("uploadFilter.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByUploadFilterContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where uploadFilter contains DEFAULT_UPLOAD_FILTER
        defaultProductShouldBeFound("uploadFilter.contains=" + DEFAULT_UPLOAD_FILTER);

        // Get all the productList where uploadFilter contains UPDATED_UPLOAD_FILTER
        defaultProductShouldNotBeFound("uploadFilter.contains=" + UPDATED_UPLOAD_FILTER);
    }

    @Test
    @Transactional
    void getAllProductsByUploadFilterNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where uploadFilter does not contain DEFAULT_UPLOAD_FILTER
        defaultProductShouldNotBeFound("uploadFilter.doesNotContain=" + DEFAULT_UPLOAD_FILTER);

        // Get all the productList where uploadFilter does not contain UPDATED_UPLOAD_FILTER
        defaultProductShouldBeFound("uploadFilter.doesNotContain=" + UPDATED_UPLOAD_FILTER);
    }

    @Test
    @Transactional
    void getAllProductsByLibraryIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);
        LibraryPerProduct library;
        if (TestUtil.findAll(em, LibraryPerProduct.class).isEmpty()) {
            library = LibraryPerProductResourceIT.createEntity(em);
            em.persist(library);
            em.flush();
        } else {
            library = TestUtil.findAll(em, LibraryPerProduct.class).get(0);
        }
        em.persist(library);
        em.flush();
        product.addLibrary(library);
        productRepository.saveAndFlush(product);
        Long libraryId = library.getId();

        // Get all the productList where library equals to libraryId
        defaultProductShouldBeFound("libraryId.equals=" + libraryId);

        // Get all the productList where library equals to (libraryId + 1)
        defaultProductShouldNotBeFound("libraryId.equals=" + (libraryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductShouldBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].identifier").value(hasItem(DEFAULT_IDENTIFIER)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastUpdatedDate").value(hasItem(DEFAULT_LAST_UPDATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].targetUrl").value(hasItem(DEFAULT_TARGET_URL)))
            .andExpect(jsonPath("$.[*].uploadState").value(hasItem(DEFAULT_UPLOAD_STATE.toString())))
            .andExpect(jsonPath("$.[*].disclaimer").value(hasItem(DEFAULT_DISCLAIMER.toString())))
            .andExpect(jsonPath("$.[*].delivered").value(hasItem(DEFAULT_DELIVERED.booleanValue())))
            .andExpect(jsonPath("$.[*].deliveredDate").value(hasItem(DEFAULT_DELIVERED_DATE.toString())))
            .andExpect(jsonPath("$.[*].contact").value(hasItem(DEFAULT_CONTACT)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].previousProductId").value(hasItem(DEFAULT_PREVIOUS_PRODUCT_ID)))
            .andExpect(jsonPath("$.[*].uploadFilter").value(hasItem(DEFAULT_UPLOAD_FILTER)));

        // Check, that the count call also returns 1
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductShouldNotBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProduct() throws Exception {
        // Get the product
        restProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product
        Product updatedProduct = productRepository.findById(product.getId()).get();
        // Disconnect from session so that the updates on updatedProduct are not directly saved in db
        em.detach(updatedProduct);
        updatedProduct
            .name(UPDATED_NAME)
            .identifier(UPDATED_IDENTIFIER)
            .version(UPDATED_VERSION)
            .createdDate(UPDATED_CREATED_DATE)
            .lastUpdatedDate(UPDATED_LAST_UPDATED_DATE)
            .targetUrl(UPDATED_TARGET_URL)
            .uploadState(UPDATED_UPLOAD_STATE)
            .disclaimer(UPDATED_DISCLAIMER)
            .delivered(UPDATED_DELIVERED)
            .deliveredDate(UPDATED_DELIVERED_DATE)
            .contact(UPDATED_CONTACT)
            .comment(UPDATED_COMMENT)
            .previousProductId(UPDATED_PREVIOUS_PRODUCT_ID)
            .uploadFilter(UPDATED_UPLOAD_FILTER);

        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProduct.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProduct.getIdentifier()).isEqualTo(UPDATED_IDENTIFIER);
        assertThat(testProduct.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testProduct.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testProduct.getLastUpdatedDate()).isEqualTo(UPDATED_LAST_UPDATED_DATE);
        assertThat(testProduct.getTargetUrl()).isEqualTo(UPDATED_TARGET_URL);
        assertThat(testProduct.getUploadState()).isEqualTo(UPDATED_UPLOAD_STATE);
        assertThat(testProduct.getDisclaimer()).isEqualTo(UPDATED_DISCLAIMER);
        assertThat(testProduct.getDelivered()).isEqualTo(UPDATED_DELIVERED);
        assertThat(testProduct.getDeliveredDate()).isEqualTo(UPDATED_DELIVERED_DATE);
        assertThat(testProduct.getContact()).isEqualTo(UPDATED_CONTACT);
        assertThat(testProduct.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testProduct.getPreviousProductId()).isEqualTo(UPDATED_PREVIOUS_PRODUCT_ID);
        assertThat(testProduct.getUploadFilter()).isEqualTo(UPDATED_UPLOAD_FILTER);
    }

    @Test
    @Transactional
    void putNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, product.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(product))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(product))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductWithPatch() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct
            .lastUpdatedDate(UPDATED_LAST_UPDATED_DATE)
            .targetUrl(UPDATED_TARGET_URL)
            .contact(UPDATED_CONTACT)
            .uploadFilter(UPDATED_UPLOAD_FILTER);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProduct.getIdentifier()).isEqualTo(DEFAULT_IDENTIFIER);
        assertThat(testProduct.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testProduct.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testProduct.getLastUpdatedDate()).isEqualTo(UPDATED_LAST_UPDATED_DATE);
        assertThat(testProduct.getTargetUrl()).isEqualTo(UPDATED_TARGET_URL);
        assertThat(testProduct.getUploadState()).isEqualTo(DEFAULT_UPLOAD_STATE);
        assertThat(testProduct.getDisclaimer()).isEqualTo(DEFAULT_DISCLAIMER);
        assertThat(testProduct.getDelivered()).isEqualTo(DEFAULT_DELIVERED);
        assertThat(testProduct.getDeliveredDate()).isEqualTo(DEFAULT_DELIVERED_DATE);
        assertThat(testProduct.getContact()).isEqualTo(UPDATED_CONTACT);
        assertThat(testProduct.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testProduct.getPreviousProductId()).isEqualTo(DEFAULT_PREVIOUS_PRODUCT_ID);
        assertThat(testProduct.getUploadFilter()).isEqualTo(UPDATED_UPLOAD_FILTER);
    }

    @Test
    @Transactional
    void fullUpdateProductWithPatch() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct
            .name(UPDATED_NAME)
            .identifier(UPDATED_IDENTIFIER)
            .version(UPDATED_VERSION)
            .createdDate(UPDATED_CREATED_DATE)
            .lastUpdatedDate(UPDATED_LAST_UPDATED_DATE)
            .targetUrl(UPDATED_TARGET_URL)
            .uploadState(UPDATED_UPLOAD_STATE)
            .disclaimer(UPDATED_DISCLAIMER)
            .delivered(UPDATED_DELIVERED)
            .deliveredDate(UPDATED_DELIVERED_DATE)
            .contact(UPDATED_CONTACT)
            .comment(UPDATED_COMMENT)
            .previousProductId(UPDATED_PREVIOUS_PRODUCT_ID)
            .uploadFilter(UPDATED_UPLOAD_FILTER);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProduct.getIdentifier()).isEqualTo(UPDATED_IDENTIFIER);
        assertThat(testProduct.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testProduct.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testProduct.getLastUpdatedDate()).isEqualTo(UPDATED_LAST_UPDATED_DATE);
        assertThat(testProduct.getTargetUrl()).isEqualTo(UPDATED_TARGET_URL);
        assertThat(testProduct.getUploadState()).isEqualTo(UPDATED_UPLOAD_STATE);
        assertThat(testProduct.getDisclaimer()).isEqualTo(UPDATED_DISCLAIMER);
        assertThat(testProduct.getDelivered()).isEqualTo(UPDATED_DELIVERED);
        assertThat(testProduct.getDeliveredDate()).isEqualTo(UPDATED_DELIVERED_DATE);
        assertThat(testProduct.getContact()).isEqualTo(UPDATED_CONTACT);
        assertThat(testProduct.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testProduct.getPreviousProductId()).isEqualTo(UPDATED_PREVIOUS_PRODUCT_ID);
        assertThat(testProduct.getUploadFilter()).isEqualTo(UPDATED_UPLOAD_FILTER);
    }

    @Test
    @Transactional
    void patchNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, product.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(product))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(product))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeDelete = productRepository.findAll().size();

        // Delete the product
        restProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, product.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
