package net.regnology.lucy.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import net.regnology.lucy.IntegrationTest;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.domain.License;
import net.regnology.lucy.domain.LicenseConflict;
import net.regnology.lucy.domain.LicenseRisk;
import net.regnology.lucy.domain.Requirement;
import net.regnology.lucy.domain.User;
import net.regnology.lucy.repository.LicenseRepository;
import net.regnology.lucy.service.LicenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link LicenseResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LicenseResourceIT {

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SHORT_IDENTIFIER = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_IDENTIFIER = "BBBBBBBBBB";

    private static final String DEFAULT_SPDX_IDENTIFIER = "AAAAAAAAAA";
    private static final String UPDATED_SPDX_IDENTIFIER = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_GENERIC_LICENSE_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_GENERIC_LICENSE_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_OTHER = "AAAAAAAAAA";
    private static final String UPDATED_OTHER = "BBBBBBBBBB";

    private static final Boolean DEFAULT_REVIEWED = false;
    private static final Boolean UPDATED_REVIEWED = true;

    private static final LocalDate DEFAULT_LAST_REVIEWED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_REVIEWED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_LAST_REVIEWED_DATE = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/licenses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LicenseRepository licenseRepository;

    @Mock
    private LicenseRepository licenseRepositoryMock;

    @Mock
    private LicenseService licenseServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLicenseMockMvc;

    private License license;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static License createEntity(EntityManager em) {
        License license = new License()
            .fullName(DEFAULT_FULL_NAME)
            .shortIdentifier(DEFAULT_SHORT_IDENTIFIER)
            .spdxIdentifier(DEFAULT_SPDX_IDENTIFIER)
            .url(DEFAULT_URL)
            .genericLicenseText(DEFAULT_GENERIC_LICENSE_TEXT)
            .other(DEFAULT_OTHER)
            .reviewed(DEFAULT_REVIEWED)
            .lastReviewedDate(DEFAULT_LAST_REVIEWED_DATE);
        return license;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static License createUpdatedEntity(EntityManager em) {
        License license = new License()
            .fullName(UPDATED_FULL_NAME)
            .shortIdentifier(UPDATED_SHORT_IDENTIFIER)
            .spdxIdentifier(UPDATED_SPDX_IDENTIFIER)
            .url(UPDATED_URL)
            .genericLicenseText(UPDATED_GENERIC_LICENSE_TEXT)
            .other(UPDATED_OTHER)
            .reviewed(UPDATED_REVIEWED)
            .lastReviewedDate(UPDATED_LAST_REVIEWED_DATE);
        return license;
    }

    @BeforeEach
    public void initTest() {
        license = createEntity(em);
    }

    @Test
    @Transactional
    void createLicense() throws Exception {
        int databaseSizeBeforeCreate = licenseRepository.findAll().size();
        // Create the License
        restLicenseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(license)))
            .andExpect(status().isCreated());

        // Validate the License in the database
        List<License> licenseList = licenseRepository.findAll();
        assertThat(licenseList).hasSize(databaseSizeBeforeCreate + 1);
        License testLicense = licenseList.get(licenseList.size() - 1);
        assertThat(testLicense.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testLicense.getShortIdentifier()).isEqualTo(DEFAULT_SHORT_IDENTIFIER);
        assertThat(testLicense.getSpdxIdentifier()).isEqualTo(DEFAULT_SPDX_IDENTIFIER);
        assertThat(testLicense.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testLicense.getGenericLicenseText()).isEqualTo(DEFAULT_GENERIC_LICENSE_TEXT);
        assertThat(testLicense.getOther()).isEqualTo(DEFAULT_OTHER);
        assertThat(testLicense.getReviewed()).isEqualTo(DEFAULT_REVIEWED);
        assertThat(testLicense.getLastReviewedDate()).isEqualTo(DEFAULT_LAST_REVIEWED_DATE);
    }

    @Test
    @Transactional
    void createLicenseWithExistingId() throws Exception {
        // Create the License with an existing ID
        license.setId(1L);

        int databaseSizeBeforeCreate = licenseRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLicenseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(license)))
            .andExpect(status().isBadRequest());

        // Validate the License in the database
        List<License> licenseList = licenseRepository.findAll();
        assertThat(licenseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFullNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = licenseRepository.findAll().size();
        // set the field null
        license.setFullName(null);

        // Create the License, which fails.

        restLicenseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(license)))
            .andExpect(status().isBadRequest());

        List<License> licenseList = licenseRepository.findAll();
        assertThat(licenseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkShortIdentifierIsRequired() throws Exception {
        int databaseSizeBeforeTest = licenseRepository.findAll().size();
        // set the field null
        license.setShortIdentifier(null);

        // Create the License, which fails.

        restLicenseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(license)))
            .andExpect(status().isBadRequest());

        List<License> licenseList = licenseRepository.findAll();
        assertThat(licenseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLicenses() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList
        restLicenseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(license.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].shortIdentifier").value(hasItem(DEFAULT_SHORT_IDENTIFIER)))
            .andExpect(jsonPath("$.[*].spdxIdentifier").value(hasItem(DEFAULT_SPDX_IDENTIFIER)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].genericLicenseText").value(hasItem(DEFAULT_GENERIC_LICENSE_TEXT.toString())))
            .andExpect(jsonPath("$.[*].other").value(hasItem(DEFAULT_OTHER)))
            .andExpect(jsonPath("$.[*].reviewed").value(hasItem(DEFAULT_REVIEWED.booleanValue())))
            .andExpect(jsonPath("$.[*].lastReviewedDate").value(hasItem(DEFAULT_LAST_REVIEWED_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLicensesWithEagerRelationshipsIsEnabled() throws Exception {
        when(licenseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLicenseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(licenseServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLicensesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(licenseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLicenseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(licenseServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getLicense() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get the license
        restLicenseMockMvc
            .perform(get(ENTITY_API_URL_ID, license.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(license.getId().intValue()))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.shortIdentifier").value(DEFAULT_SHORT_IDENTIFIER))
            .andExpect(jsonPath("$.spdxIdentifier").value(DEFAULT_SPDX_IDENTIFIER))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.genericLicenseText").value(DEFAULT_GENERIC_LICENSE_TEXT.toString()))
            .andExpect(jsonPath("$.other").value(DEFAULT_OTHER))
            .andExpect(jsonPath("$.reviewed").value(DEFAULT_REVIEWED.booleanValue()))
            .andExpect(jsonPath("$.lastReviewedDate").value(DEFAULT_LAST_REVIEWED_DATE.toString()));
    }

    @Test
    @Transactional
    void getLicensesByIdFiltering() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        Long id = license.getId();

        defaultLicenseShouldBeFound("id.equals=" + id);
        defaultLicenseShouldNotBeFound("id.notEquals=" + id);

        defaultLicenseShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultLicenseShouldNotBeFound("id.greaterThan=" + id);

        defaultLicenseShouldBeFound("id.lessThanOrEqual=" + id);
        defaultLicenseShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLicensesByFullNameIsEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where fullName equals to DEFAULT_FULL_NAME
        defaultLicenseShouldBeFound("fullName.equals=" + DEFAULT_FULL_NAME);

        // Get all the licenseList where fullName equals to UPDATED_FULL_NAME
        defaultLicenseShouldNotBeFound("fullName.equals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllLicensesByFullNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where fullName not equals to DEFAULT_FULL_NAME
        defaultLicenseShouldNotBeFound("fullName.notEquals=" + DEFAULT_FULL_NAME);

        // Get all the licenseList where fullName not equals to UPDATED_FULL_NAME
        defaultLicenseShouldBeFound("fullName.notEquals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllLicensesByFullNameIsInShouldWork() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where fullName in DEFAULT_FULL_NAME or UPDATED_FULL_NAME
        defaultLicenseShouldBeFound("fullName.in=" + DEFAULT_FULL_NAME + "," + UPDATED_FULL_NAME);

        // Get all the licenseList where fullName equals to UPDATED_FULL_NAME
        defaultLicenseShouldNotBeFound("fullName.in=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllLicensesByFullNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where fullName is not null
        defaultLicenseShouldBeFound("fullName.specified=true");

        // Get all the licenseList where fullName is null
        defaultLicenseShouldNotBeFound("fullName.specified=false");
    }

    @Test
    @Transactional
    void getAllLicensesByFullNameContainsSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where fullName contains DEFAULT_FULL_NAME
        defaultLicenseShouldBeFound("fullName.contains=" + DEFAULT_FULL_NAME);

        // Get all the licenseList where fullName contains UPDATED_FULL_NAME
        defaultLicenseShouldNotBeFound("fullName.contains=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllLicensesByFullNameNotContainsSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where fullName does not contain DEFAULT_FULL_NAME
        defaultLicenseShouldNotBeFound("fullName.doesNotContain=" + DEFAULT_FULL_NAME);

        // Get all the licenseList where fullName does not contain UPDATED_FULL_NAME
        defaultLicenseShouldBeFound("fullName.doesNotContain=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllLicensesByShortIdentifierIsEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where shortIdentifier equals to DEFAULT_SHORT_IDENTIFIER
        defaultLicenseShouldBeFound("shortIdentifier.equals=" + DEFAULT_SHORT_IDENTIFIER);

        // Get all the licenseList where shortIdentifier equals to UPDATED_SHORT_IDENTIFIER
        defaultLicenseShouldNotBeFound("shortIdentifier.equals=" + UPDATED_SHORT_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllLicensesByShortIdentifierIsNotEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where shortIdentifier not equals to DEFAULT_SHORT_IDENTIFIER
        defaultLicenseShouldNotBeFound("shortIdentifier.notEquals=" + DEFAULT_SHORT_IDENTIFIER);

        // Get all the licenseList where shortIdentifier not equals to UPDATED_SHORT_IDENTIFIER
        defaultLicenseShouldBeFound("shortIdentifier.notEquals=" + UPDATED_SHORT_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllLicensesByShortIdentifierIsInShouldWork() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where shortIdentifier in DEFAULT_SHORT_IDENTIFIER or UPDATED_SHORT_IDENTIFIER
        defaultLicenseShouldBeFound("shortIdentifier.in=" + DEFAULT_SHORT_IDENTIFIER + "," + UPDATED_SHORT_IDENTIFIER);

        // Get all the licenseList where shortIdentifier equals to UPDATED_SHORT_IDENTIFIER
        defaultLicenseShouldNotBeFound("shortIdentifier.in=" + UPDATED_SHORT_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllLicensesByShortIdentifierIsNullOrNotNull() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where shortIdentifier is not null
        defaultLicenseShouldBeFound("shortIdentifier.specified=true");

        // Get all the licenseList where shortIdentifier is null
        defaultLicenseShouldNotBeFound("shortIdentifier.specified=false");
    }

    @Test
    @Transactional
    void getAllLicensesByShortIdentifierContainsSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where shortIdentifier contains DEFAULT_SHORT_IDENTIFIER
        defaultLicenseShouldBeFound("shortIdentifier.contains=" + DEFAULT_SHORT_IDENTIFIER);

        // Get all the licenseList where shortIdentifier contains UPDATED_SHORT_IDENTIFIER
        defaultLicenseShouldNotBeFound("shortIdentifier.contains=" + UPDATED_SHORT_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllLicensesByShortIdentifierNotContainsSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where shortIdentifier does not contain DEFAULT_SHORT_IDENTIFIER
        defaultLicenseShouldNotBeFound("shortIdentifier.doesNotContain=" + DEFAULT_SHORT_IDENTIFIER);

        // Get all the licenseList where shortIdentifier does not contain UPDATED_SHORT_IDENTIFIER
        defaultLicenseShouldBeFound("shortIdentifier.doesNotContain=" + UPDATED_SHORT_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllLicensesBySpdxIdentifierIsEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where spdxIdentifier equals to DEFAULT_SPDX_IDENTIFIER
        defaultLicenseShouldBeFound("spdxIdentifier.equals=" + DEFAULT_SPDX_IDENTIFIER);

        // Get all the licenseList where spdxIdentifier equals to UPDATED_SPDX_IDENTIFIER
        defaultLicenseShouldNotBeFound("spdxIdentifier.equals=" + UPDATED_SPDX_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllLicensesBySpdxIdentifierIsNotEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where spdxIdentifier not equals to DEFAULT_SPDX_IDENTIFIER
        defaultLicenseShouldNotBeFound("spdxIdentifier.notEquals=" + DEFAULT_SPDX_IDENTIFIER);

        // Get all the licenseList where spdxIdentifier not equals to UPDATED_SPDX_IDENTIFIER
        defaultLicenseShouldBeFound("spdxIdentifier.notEquals=" + UPDATED_SPDX_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllLicensesBySpdxIdentifierIsInShouldWork() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where spdxIdentifier in DEFAULT_SPDX_IDENTIFIER or UPDATED_SPDX_IDENTIFIER
        defaultLicenseShouldBeFound("spdxIdentifier.in=" + DEFAULT_SPDX_IDENTIFIER + "," + UPDATED_SPDX_IDENTIFIER);

        // Get all the licenseList where spdxIdentifier equals to UPDATED_SPDX_IDENTIFIER
        defaultLicenseShouldNotBeFound("spdxIdentifier.in=" + UPDATED_SPDX_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllLicensesBySpdxIdentifierIsNullOrNotNull() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where spdxIdentifier is not null
        defaultLicenseShouldBeFound("spdxIdentifier.specified=true");

        // Get all the licenseList where spdxIdentifier is null
        defaultLicenseShouldNotBeFound("spdxIdentifier.specified=false");
    }

    @Test
    @Transactional
    void getAllLicensesBySpdxIdentifierContainsSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where spdxIdentifier contains DEFAULT_SPDX_IDENTIFIER
        defaultLicenseShouldBeFound("spdxIdentifier.contains=" + DEFAULT_SPDX_IDENTIFIER);

        // Get all the licenseList where spdxIdentifier contains UPDATED_SPDX_IDENTIFIER
        defaultLicenseShouldNotBeFound("spdxIdentifier.contains=" + UPDATED_SPDX_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllLicensesBySpdxIdentifierNotContainsSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where spdxIdentifier does not contain DEFAULT_SPDX_IDENTIFIER
        defaultLicenseShouldNotBeFound("spdxIdentifier.doesNotContain=" + DEFAULT_SPDX_IDENTIFIER);

        // Get all the licenseList where spdxIdentifier does not contain UPDATED_SPDX_IDENTIFIER
        defaultLicenseShouldBeFound("spdxIdentifier.doesNotContain=" + UPDATED_SPDX_IDENTIFIER);
    }

    @Test
    @Transactional
    void getAllLicensesByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where url equals to DEFAULT_URL
        defaultLicenseShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the licenseList where url equals to UPDATED_URL
        defaultLicenseShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllLicensesByUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where url not equals to DEFAULT_URL
        defaultLicenseShouldNotBeFound("url.notEquals=" + DEFAULT_URL);

        // Get all the licenseList where url not equals to UPDATED_URL
        defaultLicenseShouldBeFound("url.notEquals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllLicensesByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where url in DEFAULT_URL or UPDATED_URL
        defaultLicenseShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the licenseList where url equals to UPDATED_URL
        defaultLicenseShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllLicensesByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where url is not null
        defaultLicenseShouldBeFound("url.specified=true");

        // Get all the licenseList where url is null
        defaultLicenseShouldNotBeFound("url.specified=false");
    }

    @Test
    @Transactional
    void getAllLicensesByUrlContainsSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where url contains DEFAULT_URL
        defaultLicenseShouldBeFound("url.contains=" + DEFAULT_URL);

        // Get all the licenseList where url contains UPDATED_URL
        defaultLicenseShouldNotBeFound("url.contains=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllLicensesByUrlNotContainsSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where url does not contain DEFAULT_URL
        defaultLicenseShouldNotBeFound("url.doesNotContain=" + DEFAULT_URL);

        // Get all the licenseList where url does not contain UPDATED_URL
        defaultLicenseShouldBeFound("url.doesNotContain=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllLicensesByOtherIsEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where other equals to DEFAULT_OTHER
        defaultLicenseShouldBeFound("other.equals=" + DEFAULT_OTHER);

        // Get all the licenseList where other equals to UPDATED_OTHER
        defaultLicenseShouldNotBeFound("other.equals=" + UPDATED_OTHER);
    }

    @Test
    @Transactional
    void getAllLicensesByOtherIsNotEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where other not equals to DEFAULT_OTHER
        defaultLicenseShouldNotBeFound("other.notEquals=" + DEFAULT_OTHER);

        // Get all the licenseList where other not equals to UPDATED_OTHER
        defaultLicenseShouldBeFound("other.notEquals=" + UPDATED_OTHER);
    }

    @Test
    @Transactional
    void getAllLicensesByOtherIsInShouldWork() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where other in DEFAULT_OTHER or UPDATED_OTHER
        defaultLicenseShouldBeFound("other.in=" + DEFAULT_OTHER + "," + UPDATED_OTHER);

        // Get all the licenseList where other equals to UPDATED_OTHER
        defaultLicenseShouldNotBeFound("other.in=" + UPDATED_OTHER);
    }

    @Test
    @Transactional
    void getAllLicensesByOtherIsNullOrNotNull() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where other is not null
        defaultLicenseShouldBeFound("other.specified=true");

        // Get all the licenseList where other is null
        defaultLicenseShouldNotBeFound("other.specified=false");
    }

    @Test
    @Transactional
    void getAllLicensesByOtherContainsSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where other contains DEFAULT_OTHER
        defaultLicenseShouldBeFound("other.contains=" + DEFAULT_OTHER);

        // Get all the licenseList where other contains UPDATED_OTHER
        defaultLicenseShouldNotBeFound("other.contains=" + UPDATED_OTHER);
    }

    @Test
    @Transactional
    void getAllLicensesByOtherNotContainsSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where other does not contain DEFAULT_OTHER
        defaultLicenseShouldNotBeFound("other.doesNotContain=" + DEFAULT_OTHER);

        // Get all the licenseList where other does not contain UPDATED_OTHER
        defaultLicenseShouldBeFound("other.doesNotContain=" + UPDATED_OTHER);
    }

    @Test
    @Transactional
    void getAllLicensesByReviewedIsEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where reviewed equals to DEFAULT_REVIEWED
        defaultLicenseShouldBeFound("reviewed.equals=" + DEFAULT_REVIEWED);

        // Get all the licenseList where reviewed equals to UPDATED_REVIEWED
        defaultLicenseShouldNotBeFound("reviewed.equals=" + UPDATED_REVIEWED);
    }

    @Test
    @Transactional
    void getAllLicensesByReviewedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where reviewed not equals to DEFAULT_REVIEWED
        defaultLicenseShouldNotBeFound("reviewed.notEquals=" + DEFAULT_REVIEWED);

        // Get all the licenseList where reviewed not equals to UPDATED_REVIEWED
        defaultLicenseShouldBeFound("reviewed.notEquals=" + UPDATED_REVIEWED);
    }

    @Test
    @Transactional
    void getAllLicensesByReviewedIsInShouldWork() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where reviewed in DEFAULT_REVIEWED or UPDATED_REVIEWED
        defaultLicenseShouldBeFound("reviewed.in=" + DEFAULT_REVIEWED + "," + UPDATED_REVIEWED);

        // Get all the licenseList where reviewed equals to UPDATED_REVIEWED
        defaultLicenseShouldNotBeFound("reviewed.in=" + UPDATED_REVIEWED);
    }

    @Test
    @Transactional
    void getAllLicensesByReviewedIsNullOrNotNull() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where reviewed is not null
        defaultLicenseShouldBeFound("reviewed.specified=true");

        // Get all the licenseList where reviewed is null
        defaultLicenseShouldNotBeFound("reviewed.specified=false");
    }

    @Test
    @Transactional
    void getAllLicensesByLastReviewedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where lastReviewedDate equals to DEFAULT_LAST_REVIEWED_DATE
        defaultLicenseShouldBeFound("lastReviewedDate.equals=" + DEFAULT_LAST_REVIEWED_DATE);

        // Get all the licenseList where lastReviewedDate equals to UPDATED_LAST_REVIEWED_DATE
        defaultLicenseShouldNotBeFound("lastReviewedDate.equals=" + UPDATED_LAST_REVIEWED_DATE);
    }

    @Test
    @Transactional
    void getAllLicensesByLastReviewedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where lastReviewedDate not equals to DEFAULT_LAST_REVIEWED_DATE
        defaultLicenseShouldNotBeFound("lastReviewedDate.notEquals=" + DEFAULT_LAST_REVIEWED_DATE);

        // Get all the licenseList where lastReviewedDate not equals to UPDATED_LAST_REVIEWED_DATE
        defaultLicenseShouldBeFound("lastReviewedDate.notEquals=" + UPDATED_LAST_REVIEWED_DATE);
    }

    @Test
    @Transactional
    void getAllLicensesByLastReviewedDateIsInShouldWork() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where lastReviewedDate in DEFAULT_LAST_REVIEWED_DATE or UPDATED_LAST_REVIEWED_DATE
        defaultLicenseShouldBeFound("lastReviewedDate.in=" + DEFAULT_LAST_REVIEWED_DATE + "," + UPDATED_LAST_REVIEWED_DATE);

        // Get all the licenseList where lastReviewedDate equals to UPDATED_LAST_REVIEWED_DATE
        defaultLicenseShouldNotBeFound("lastReviewedDate.in=" + UPDATED_LAST_REVIEWED_DATE);
    }

    @Test
    @Transactional
    void getAllLicensesByLastReviewedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where lastReviewedDate is not null
        defaultLicenseShouldBeFound("lastReviewedDate.specified=true");

        // Get all the licenseList where lastReviewedDate is null
        defaultLicenseShouldNotBeFound("lastReviewedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllLicensesByLastReviewedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where lastReviewedDate is greater than or equal to DEFAULT_LAST_REVIEWED_DATE
        defaultLicenseShouldBeFound("lastReviewedDate.greaterThanOrEqual=" + DEFAULT_LAST_REVIEWED_DATE);

        // Get all the licenseList where lastReviewedDate is greater than or equal to UPDATED_LAST_REVIEWED_DATE
        defaultLicenseShouldNotBeFound("lastReviewedDate.greaterThanOrEqual=" + UPDATED_LAST_REVIEWED_DATE);
    }

    @Test
    @Transactional
    void getAllLicensesByLastReviewedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where lastReviewedDate is less than or equal to DEFAULT_LAST_REVIEWED_DATE
        defaultLicenseShouldBeFound("lastReviewedDate.lessThanOrEqual=" + DEFAULT_LAST_REVIEWED_DATE);

        // Get all the licenseList where lastReviewedDate is less than or equal to SMALLER_LAST_REVIEWED_DATE
        defaultLicenseShouldNotBeFound("lastReviewedDate.lessThanOrEqual=" + SMALLER_LAST_REVIEWED_DATE);
    }

    @Test
    @Transactional
    void getAllLicensesByLastReviewedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where lastReviewedDate is less than DEFAULT_LAST_REVIEWED_DATE
        defaultLicenseShouldNotBeFound("lastReviewedDate.lessThan=" + DEFAULT_LAST_REVIEWED_DATE);

        // Get all the licenseList where lastReviewedDate is less than UPDATED_LAST_REVIEWED_DATE
        defaultLicenseShouldBeFound("lastReviewedDate.lessThan=" + UPDATED_LAST_REVIEWED_DATE);
    }

    @Test
    @Transactional
    void getAllLicensesByLastReviewedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        // Get all the licenseList where lastReviewedDate is greater than DEFAULT_LAST_REVIEWED_DATE
        defaultLicenseShouldNotBeFound("lastReviewedDate.greaterThan=" + DEFAULT_LAST_REVIEWED_DATE);

        // Get all the licenseList where lastReviewedDate is greater than SMALLER_LAST_REVIEWED_DATE
        defaultLicenseShouldBeFound("lastReviewedDate.greaterThan=" + SMALLER_LAST_REVIEWED_DATE);
    }

    @Test
    @Transactional
    void getAllLicensesByLicenseConflictIsEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);
        LicenseConflict licenseConflict;
        if (TestUtil.findAll(em, LicenseConflict.class).isEmpty()) {
            licenseConflict = LicenseConflictResourceIT.createEntity(em);
            em.persist(licenseConflict);
            em.flush();
        } else {
            licenseConflict = TestUtil.findAll(em, LicenseConflict.class).get(0);
        }
        em.persist(licenseConflict);
        em.flush();
        license.addLicenseConflict(licenseConflict);
        licenseRepository.saveAndFlush(license);
        Long licenseConflictId = licenseConflict.getId();

        // Get all the licenseList where licenseConflict equals to licenseConflictId
        defaultLicenseShouldBeFound("licenseConflictId.equals=" + licenseConflictId);

        // Get all the licenseList where licenseConflict equals to (licenseConflictId + 1)
        defaultLicenseShouldNotBeFound("licenseConflictId.equals=" + (licenseConflictId + 1));
    }

    @Test
    @Transactional
    void getAllLicensesByLastReviewedByIsEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);
        User lastReviewedBy;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            lastReviewedBy = UserResourceIT.createEntity(em);
            em.persist(lastReviewedBy);
            em.flush();
        } else {
            lastReviewedBy = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(lastReviewedBy);
        em.flush();
        license.setLastReviewedBy(lastReviewedBy);
        licenseRepository.saveAndFlush(license);
        Long lastReviewedById = lastReviewedBy.getId();

        // Get all the licenseList where lastReviewedBy equals to lastReviewedById
        defaultLicenseShouldBeFound("lastReviewedById.equals=" + lastReviewedById);

        // Get all the licenseList where lastReviewedBy equals to (lastReviewedById + 1)
        defaultLicenseShouldNotBeFound("lastReviewedById.equals=" + (lastReviewedById + 1));
    }

    @Test
    @Transactional
    void getAllLicensesByLicenseRiskIsEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);
        LicenseRisk licenseRisk;
        if (TestUtil.findAll(em, LicenseRisk.class).isEmpty()) {
            licenseRisk = LicenseRiskResourceIT.createEntity(em);
            em.persist(licenseRisk);
            em.flush();
        } else {
            licenseRisk = TestUtil.findAll(em, LicenseRisk.class).get(0);
        }
        em.persist(licenseRisk);
        em.flush();
        license.setLicenseRisk(licenseRisk);
        licenseRepository.saveAndFlush(license);
        Long licenseRiskId = licenseRisk.getId();

        // Get all the licenseList where licenseRisk equals to licenseRiskId
        defaultLicenseShouldBeFound("licenseRiskId.equals=" + licenseRiskId);

        // Get all the licenseList where licenseRisk equals to (licenseRiskId + 1)
        defaultLicenseShouldNotBeFound("licenseRiskId.equals=" + (licenseRiskId + 1));
    }

    @Test
    @Transactional
    void getAllLicensesByRequirementIsEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);
        Requirement requirement;
        if (TestUtil.findAll(em, Requirement.class).isEmpty()) {
            requirement = RequirementResourceIT.createEntity(em);
            em.persist(requirement);
            em.flush();
        } else {
            requirement = TestUtil.findAll(em, Requirement.class).get(0);
        }
        em.persist(requirement);
        em.flush();
        license.addRequirement(requirement);
        licenseRepository.saveAndFlush(license);
        Long requirementId = requirement.getId();

        // Get all the licenseList where requirement equals to requirementId
        defaultLicenseShouldBeFound("requirementId.equals=" + requirementId);

        // Get all the licenseList where requirement equals to (requirementId + 1)
        defaultLicenseShouldNotBeFound("requirementId.equals=" + (requirementId + 1));
    }

    @Test
    @Transactional
    void getAllLicensesByLibraryPublishIsEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);
        Library libraryPublish;
        if (TestUtil.findAll(em, Library.class).isEmpty()) {
            libraryPublish = LibraryResourceIT.createEntity(em);
            em.persist(libraryPublish);
            em.flush();
        } else {
            libraryPublish = TestUtil.findAll(em, Library.class).get(0);
        }
        em.persist(libraryPublish);
        em.flush();
        license.addLibraryPublish(libraryPublish);
        licenseRepository.saveAndFlush(license);
        Long libraryPublishId = libraryPublish.getId();

        // Get all the licenseList where libraryPublish equals to libraryPublishId
        defaultLicenseShouldBeFound("libraryPublishId.equals=" + libraryPublishId);

        // Get all the licenseList where libraryPublish equals to (libraryPublishId + 1)
        defaultLicenseShouldNotBeFound("libraryPublishId.equals=" + (libraryPublishId + 1));
    }

    @Test
    @Transactional
    void getAllLicensesByLibraryFilesIsEqualToSomething() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);
        Library libraryFiles;
        if (TestUtil.findAll(em, Library.class).isEmpty()) {
            libraryFiles = LibraryResourceIT.createEntity(em);
            em.persist(libraryFiles);
            em.flush();
        } else {
            libraryFiles = TestUtil.findAll(em, Library.class).get(0);
        }
        em.persist(libraryFiles);
        em.flush();
        license.addLibraryFiles(libraryFiles);
        licenseRepository.saveAndFlush(license);
        Long libraryFilesId = libraryFiles.getId();

        // Get all the licenseList where libraryFiles equals to libraryFilesId
        defaultLicenseShouldBeFound("libraryFilesId.equals=" + libraryFilesId);

        // Get all the licenseList where libraryFiles equals to (libraryFilesId + 1)
        defaultLicenseShouldNotBeFound("libraryFilesId.equals=" + (libraryFilesId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLicenseShouldBeFound(String filter) throws Exception {
        restLicenseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(license.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].shortIdentifier").value(hasItem(DEFAULT_SHORT_IDENTIFIER)))
            .andExpect(jsonPath("$.[*].spdxIdentifier").value(hasItem(DEFAULT_SPDX_IDENTIFIER)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].genericLicenseText").value(hasItem(DEFAULT_GENERIC_LICENSE_TEXT.toString())))
            .andExpect(jsonPath("$.[*].other").value(hasItem(DEFAULT_OTHER)))
            .andExpect(jsonPath("$.[*].reviewed").value(hasItem(DEFAULT_REVIEWED.booleanValue())))
            .andExpect(jsonPath("$.[*].lastReviewedDate").value(hasItem(DEFAULT_LAST_REVIEWED_DATE.toString())));

        // Check, that the count call also returns 1
        restLicenseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLicenseShouldNotBeFound(String filter) throws Exception {
        restLicenseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLicenseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLicense() throws Exception {
        // Get the license
        restLicenseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLicense() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        int databaseSizeBeforeUpdate = licenseRepository.findAll().size();

        // Update the license
        License updatedLicense = licenseRepository.findById(license.getId()).get();
        // Disconnect from session so that the updates on updatedLicense are not directly saved in db
        em.detach(updatedLicense);
        updatedLicense
            .fullName(UPDATED_FULL_NAME)
            .shortIdentifier(UPDATED_SHORT_IDENTIFIER)
            .spdxIdentifier(UPDATED_SPDX_IDENTIFIER)
            .url(UPDATED_URL)
            .genericLicenseText(UPDATED_GENERIC_LICENSE_TEXT)
            .other(UPDATED_OTHER)
            .reviewed(UPDATED_REVIEWED)
            .lastReviewedDate(UPDATED_LAST_REVIEWED_DATE);

        restLicenseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLicense.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLicense))
            )
            .andExpect(status().isOk());

        // Validate the License in the database
        List<License> licenseList = licenseRepository.findAll();
        assertThat(licenseList).hasSize(databaseSizeBeforeUpdate);
        License testLicense = licenseList.get(licenseList.size() - 1);
        assertThat(testLicense.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testLicense.getShortIdentifier()).isEqualTo(UPDATED_SHORT_IDENTIFIER);
        assertThat(testLicense.getSpdxIdentifier()).isEqualTo(UPDATED_SPDX_IDENTIFIER);
        assertThat(testLicense.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testLicense.getGenericLicenseText()).isEqualTo(UPDATED_GENERIC_LICENSE_TEXT);
        assertThat(testLicense.getOther()).isEqualTo(UPDATED_OTHER);
        assertThat(testLicense.getReviewed()).isEqualTo(UPDATED_REVIEWED);
        assertThat(testLicense.getLastReviewedDate()).isEqualTo(UPDATED_LAST_REVIEWED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingLicense() throws Exception {
        int databaseSizeBeforeUpdate = licenseRepository.findAll().size();
        license.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLicenseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, license.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(license))
            )
            .andExpect(status().isBadRequest());

        // Validate the License in the database
        List<License> licenseList = licenseRepository.findAll();
        assertThat(licenseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLicense() throws Exception {
        int databaseSizeBeforeUpdate = licenseRepository.findAll().size();
        license.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicenseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(license))
            )
            .andExpect(status().isBadRequest());

        // Validate the License in the database
        List<License> licenseList = licenseRepository.findAll();
        assertThat(licenseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLicense() throws Exception {
        int databaseSizeBeforeUpdate = licenseRepository.findAll().size();
        license.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicenseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(license)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the License in the database
        List<License> licenseList = licenseRepository.findAll();
        assertThat(licenseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLicenseWithPatch() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        int databaseSizeBeforeUpdate = licenseRepository.findAll().size();

        // Update the license using partial update
        License partialUpdatedLicense = new License();
        partialUpdatedLicense.setId(license.getId());

        partialUpdatedLicense
            .fullName(UPDATED_FULL_NAME)
            .spdxIdentifier(UPDATED_SPDX_IDENTIFIER)
            .other(UPDATED_OTHER)
            .reviewed(UPDATED_REVIEWED);

        restLicenseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLicense.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLicense))
            )
            .andExpect(status().isOk());

        // Validate the License in the database
        List<License> licenseList = licenseRepository.findAll();
        assertThat(licenseList).hasSize(databaseSizeBeforeUpdate);
        License testLicense = licenseList.get(licenseList.size() - 1);
        assertThat(testLicense.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testLicense.getShortIdentifier()).isEqualTo(DEFAULT_SHORT_IDENTIFIER);
        assertThat(testLicense.getSpdxIdentifier()).isEqualTo(UPDATED_SPDX_IDENTIFIER);
        assertThat(testLicense.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testLicense.getGenericLicenseText()).isEqualTo(DEFAULT_GENERIC_LICENSE_TEXT);
        assertThat(testLicense.getOther()).isEqualTo(UPDATED_OTHER);
        assertThat(testLicense.getReviewed()).isEqualTo(UPDATED_REVIEWED);
        assertThat(testLicense.getLastReviewedDate()).isEqualTo(DEFAULT_LAST_REVIEWED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateLicenseWithPatch() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        int databaseSizeBeforeUpdate = licenseRepository.findAll().size();

        // Update the license using partial update
        License partialUpdatedLicense = new License();
        partialUpdatedLicense.setId(license.getId());

        partialUpdatedLicense
            .fullName(UPDATED_FULL_NAME)
            .shortIdentifier(UPDATED_SHORT_IDENTIFIER)
            .spdxIdentifier(UPDATED_SPDX_IDENTIFIER)
            .url(UPDATED_URL)
            .genericLicenseText(UPDATED_GENERIC_LICENSE_TEXT)
            .other(UPDATED_OTHER)
            .reviewed(UPDATED_REVIEWED)
            .lastReviewedDate(UPDATED_LAST_REVIEWED_DATE);

        restLicenseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLicense.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLicense))
            )
            .andExpect(status().isOk());

        // Validate the License in the database
        List<License> licenseList = licenseRepository.findAll();
        assertThat(licenseList).hasSize(databaseSizeBeforeUpdate);
        License testLicense = licenseList.get(licenseList.size() - 1);
        assertThat(testLicense.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testLicense.getShortIdentifier()).isEqualTo(UPDATED_SHORT_IDENTIFIER);
        assertThat(testLicense.getSpdxIdentifier()).isEqualTo(UPDATED_SPDX_IDENTIFIER);
        assertThat(testLicense.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testLicense.getGenericLicenseText()).isEqualTo(UPDATED_GENERIC_LICENSE_TEXT);
        assertThat(testLicense.getOther()).isEqualTo(UPDATED_OTHER);
        assertThat(testLicense.getReviewed()).isEqualTo(UPDATED_REVIEWED);
        assertThat(testLicense.getLastReviewedDate()).isEqualTo(UPDATED_LAST_REVIEWED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingLicense() throws Exception {
        int databaseSizeBeforeUpdate = licenseRepository.findAll().size();
        license.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLicenseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, license.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(license))
            )
            .andExpect(status().isBadRequest());

        // Validate the License in the database
        List<License> licenseList = licenseRepository.findAll();
        assertThat(licenseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLicense() throws Exception {
        int databaseSizeBeforeUpdate = licenseRepository.findAll().size();
        license.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicenseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(license))
            )
            .andExpect(status().isBadRequest());

        // Validate the License in the database
        List<License> licenseList = licenseRepository.findAll();
        assertThat(licenseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLicense() throws Exception {
        int databaseSizeBeforeUpdate = licenseRepository.findAll().size();
        license.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicenseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(license)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the License in the database
        List<License> licenseList = licenseRepository.findAll();
        assertThat(licenseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLicense() throws Exception {
        // Initialize the database
        licenseRepository.saveAndFlush(license);

        int databaseSizeBeforeDelete = licenseRepository.findAll().size();

        // Delete the license
        restLicenseMockMvc
            .perform(delete(ENTITY_API_URL_ID, license.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<License> licenseList = licenseRepository.findAll();
        assertThat(licenseList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
