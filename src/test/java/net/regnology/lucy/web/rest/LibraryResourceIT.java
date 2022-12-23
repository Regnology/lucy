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
import net.regnology.lucy.domain.LibraryErrorLog;
import net.regnology.lucy.domain.License;
import net.regnology.lucy.domain.LicensePerLibrary;
import net.regnology.lucy.domain.User;
import net.regnology.lucy.domain.enumeration.LibraryType;
import net.regnology.lucy.repository.LibraryRepository;
import net.regnology.lucy.service.LibraryService;
import org.assertj.core.api.Assertions;
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
 * Integration tests for the {@link LibraryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LibraryResourceIT {

    private static final String DEFAULT_GROUP_ID = "AAAAAAAAAA";
    private static final String UPDATED_GROUP_ID = "BBBBBBBBBB";

    private static final String DEFAULT_ARTIFACT_ID = "AAAAAAAAAA";
    private static final String UPDATED_ARTIFACT_ID = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final LibraryType DEFAULT_TYPE = LibraryType.MAVEN;
    private static final LibraryType UPDATED_TYPE = LibraryType.NPM;

    private static final String DEFAULT_ORIGINAL_LICENSE = "AAAAAAAAAA";
    private static final String UPDATED_ORIGINAL_LICENSE = "BBBBBBBBBB";

    private static final String DEFAULT_LICENSE_URL = "AAAAAAAAAA";
    private static final String UPDATED_LICENSE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_LICENSE_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_LICENSE_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_SOURCE_CODE_URL = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE_CODE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_P_URL = "AAAAAAAAAA";
    private static final String UPDATED_P_URL = "BBBBBBBBBB";

    private static final String DEFAULT_COPYRIGHT = "AAAAAAAAAA";
    private static final String UPDATED_COPYRIGHT = "BBBBBBBBBB";

    private static final String DEFAULT_COMPLIANCE = "AAAAAAAAAA";
    private static final String UPDATED_COMPLIANCE = "BBBBBBBBBB";

    private static final String DEFAULT_COMPLIANCE_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMPLIANCE_COMMENT = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_REVIEWED = false;
    private static final Boolean UPDATED_REVIEWED = true;

    private static final LocalDate DEFAULT_LAST_REVIEWED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_REVIEWED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_LAST_REVIEWED_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_CREATED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CREATED_DATE = LocalDate.ofEpochDay(-1L);

    private static final Boolean DEFAULT_HIDE_FOR_PUBLISHING = false;
    private static final Boolean UPDATED_HIDE_FOR_PUBLISHING = true;

    private static final String DEFAULT_MD_5 = "AAAAAAAAAA";
    private static final String UPDATED_MD_5 = "BBBBBBBBBB";

    private static final String DEFAULT_SHA_1 = "AAAAAAAAAA";
    private static final String UPDATED_SHA_1 = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/libraries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LibraryRepository libraryRepository;

    @Mock
    private LibraryRepository libraryRepositoryMock;

    @Mock
    private LibraryService libraryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLibraryMockMvc;

    private Library library;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Library createEntity(EntityManager em) {
        Library library = new Library()
            .groupId(DEFAULT_GROUP_ID)
            .artifactId(DEFAULT_ARTIFACT_ID)
            .version(DEFAULT_VERSION)
            .type(DEFAULT_TYPE)
            .originalLicense(DEFAULT_ORIGINAL_LICENSE)
            .licenseUrl(DEFAULT_LICENSE_URL)
            .licenseText(DEFAULT_LICENSE_TEXT)
            .sourceCodeUrl(DEFAULT_SOURCE_CODE_URL)
            .pUrl(DEFAULT_P_URL)
            .copyright(DEFAULT_COPYRIGHT)
            .compliance(DEFAULT_COMPLIANCE)
            .complianceComment(DEFAULT_COMPLIANCE_COMMENT)
            .comment(DEFAULT_COMMENT)
            .reviewed(DEFAULT_REVIEWED)
            .lastReviewedDate(DEFAULT_LAST_REVIEWED_DATE)
            .createdDate(DEFAULT_CREATED_DATE)
            .hideForPublishing(DEFAULT_HIDE_FOR_PUBLISHING)
            .md5(DEFAULT_MD_5)
            .sha1(DEFAULT_SHA_1);
        return library;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Library createUpdatedEntity(EntityManager em) {
        Library library = new Library()
            .groupId(UPDATED_GROUP_ID)
            .artifactId(UPDATED_ARTIFACT_ID)
            .version(UPDATED_VERSION)
            .type(UPDATED_TYPE)
            .originalLicense(UPDATED_ORIGINAL_LICENSE)
            .licenseUrl(UPDATED_LICENSE_URL)
            .licenseText(UPDATED_LICENSE_TEXT)
            .sourceCodeUrl(UPDATED_SOURCE_CODE_URL)
            .pUrl(UPDATED_P_URL)
            .copyright(UPDATED_COPYRIGHT)
            .compliance(UPDATED_COMPLIANCE)
            .complianceComment(UPDATED_COMPLIANCE_COMMENT)
            .comment(UPDATED_COMMENT)
            .reviewed(UPDATED_REVIEWED)
            .lastReviewedDate(UPDATED_LAST_REVIEWED_DATE)
            .createdDate(UPDATED_CREATED_DATE)
            .hideForPublishing(UPDATED_HIDE_FOR_PUBLISHING)
            .md5(UPDATED_MD_5)
            .sha1(UPDATED_SHA_1);
        return library;
    }

    @BeforeEach
    public void initTest() {
        library = createEntity(em);
    }

    @Test
    @Transactional
    void createLibrary() throws Exception {
        int databaseSizeBeforeCreate = libraryRepository.findAll().size();
        // Create the Library
        restLibraryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(library)))
            .andExpect(status().isCreated());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeCreate + 1);
        Library testLibrary = libraryList.get(libraryList.size() - 1);
        assertThat(testLibrary.getGroupId()).isEqualTo(DEFAULT_GROUP_ID);
        assertThat(testLibrary.getArtifactId()).isEqualTo(DEFAULT_ARTIFACT_ID);
        assertThat(testLibrary.getVersion()).isEqualTo(DEFAULT_VERSION);
        Assertions.assertThat(testLibrary.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testLibrary.getOriginalLicense()).isEqualTo(DEFAULT_ORIGINAL_LICENSE);
        assertThat(testLibrary.getLicenseUrl()).isEqualTo(DEFAULT_LICENSE_URL);
        assertThat(testLibrary.getLicenseText()).isEqualTo(DEFAULT_LICENSE_TEXT);
        assertThat(testLibrary.getSourceCodeUrl()).isEqualTo(DEFAULT_SOURCE_CODE_URL);
        assertThat(testLibrary.getpUrl()).isEqualTo(DEFAULT_P_URL);
        assertThat(testLibrary.getCopyright()).isEqualTo(DEFAULT_COPYRIGHT);
        assertThat(testLibrary.getCompliance()).isEqualTo(DEFAULT_COMPLIANCE);
        assertThat(testLibrary.getComplianceComment()).isEqualTo(DEFAULT_COMPLIANCE_COMMENT);
        assertThat(testLibrary.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testLibrary.getReviewed()).isEqualTo(DEFAULT_REVIEWED);
        assertThat(testLibrary.getLastReviewedDate()).isEqualTo(DEFAULT_LAST_REVIEWED_DATE);
        assertThat(testLibrary.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testLibrary.getHideForPublishing()).isEqualTo(DEFAULT_HIDE_FOR_PUBLISHING);
        assertThat(testLibrary.getMd5()).isEqualTo(DEFAULT_MD_5);
        assertThat(testLibrary.getSha1()).isEqualTo(DEFAULT_SHA_1);
    }

    @Test
    @Transactional
    void createLibraryWithExistingId() throws Exception {
        // Create the Library with an existing ID
        library.setId(1L);

        int databaseSizeBeforeCreate = libraryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLibraryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(library)))
            .andExpect(status().isBadRequest());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkArtifactIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = libraryRepository.findAll().size();
        // set the field null
        library.setArtifactId(null);

        // Create the Library, which fails.

        restLibraryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(library)))
            .andExpect(status().isBadRequest());

        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVersionIsRequired() throws Exception {
        int databaseSizeBeforeTest = libraryRepository.findAll().size();
        // set the field null
        library.setVersion(null);

        // Create the Library, which fails.

        restLibraryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(library)))
            .andExpect(status().isBadRequest());

        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLibraries() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList
        restLibraryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(library.getId().intValue())))
            .andExpect(jsonPath("$.[*].groupId").value(hasItem(DEFAULT_GROUP_ID)))
            .andExpect(jsonPath("$.[*].artifactId").value(hasItem(DEFAULT_ARTIFACT_ID)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].originalLicense").value(hasItem(DEFAULT_ORIGINAL_LICENSE)))
            .andExpect(jsonPath("$.[*].licenseUrl").value(hasItem(DEFAULT_LICENSE_URL)))
            .andExpect(jsonPath("$.[*].licenseText").value(hasItem(DEFAULT_LICENSE_TEXT.toString())))
            .andExpect(jsonPath("$.[*].sourceCodeUrl").value(hasItem(DEFAULT_SOURCE_CODE_URL)))
            .andExpect(jsonPath("$.[*].pUrl").value(hasItem(DEFAULT_P_URL)))
            .andExpect(jsonPath("$.[*].copyright").value(hasItem(DEFAULT_COPYRIGHT)))
            .andExpect(jsonPath("$.[*].compliance").value(hasItem(DEFAULT_COMPLIANCE)))
            .andExpect(jsonPath("$.[*].complianceComment").value(hasItem(DEFAULT_COMPLIANCE_COMMENT)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].reviewed").value(hasItem(DEFAULT_REVIEWED.booleanValue())))
            .andExpect(jsonPath("$.[*].lastReviewedDate").value(hasItem(DEFAULT_LAST_REVIEWED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].hideForPublishing").value(hasItem(DEFAULT_HIDE_FOR_PUBLISHING.booleanValue())))
            .andExpect(jsonPath("$.[*].md5").value(hasItem(DEFAULT_MD_5)))
            .andExpect(jsonPath("$.[*].sha1").value(hasItem(DEFAULT_SHA_1)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLibrariesWithEagerRelationshipsIsEnabled() throws Exception {
        when(libraryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLibraryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(libraryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLibrariesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(libraryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLibraryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(libraryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getLibrary() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get the library
        restLibraryMockMvc
            .perform(get(ENTITY_API_URL_ID, library.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(library.getId().intValue()))
            .andExpect(jsonPath("$.groupId").value(DEFAULT_GROUP_ID))
            .andExpect(jsonPath("$.artifactId").value(DEFAULT_ARTIFACT_ID))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.originalLicense").value(DEFAULT_ORIGINAL_LICENSE))
            .andExpect(jsonPath("$.licenseUrl").value(DEFAULT_LICENSE_URL))
            .andExpect(jsonPath("$.licenseText").value(DEFAULT_LICENSE_TEXT.toString()))
            .andExpect(jsonPath("$.sourceCodeUrl").value(DEFAULT_SOURCE_CODE_URL))
            .andExpect(jsonPath("$.pUrl").value(DEFAULT_P_URL))
            .andExpect(jsonPath("$.copyright").value(DEFAULT_COPYRIGHT))
            .andExpect(jsonPath("$.compliance").value(DEFAULT_COMPLIANCE))
            .andExpect(jsonPath("$.complianceComment").value(DEFAULT_COMPLIANCE_COMMENT))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT))
            .andExpect(jsonPath("$.reviewed").value(DEFAULT_REVIEWED.booleanValue()))
            .andExpect(jsonPath("$.lastReviewedDate").value(DEFAULT_LAST_REVIEWED_DATE.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.hideForPublishing").value(DEFAULT_HIDE_FOR_PUBLISHING.booleanValue()))
            .andExpect(jsonPath("$.md5").value(DEFAULT_MD_5))
            .andExpect(jsonPath("$.sha1").value(DEFAULT_SHA_1));
    }

    @Test
    @Transactional
    void getLibrariesByIdFiltering() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        Long id = library.getId();

        defaultLibraryShouldBeFound("id.equals=" + id);
        defaultLibraryShouldNotBeFound("id.notEquals=" + id);

        defaultLibraryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultLibraryShouldNotBeFound("id.greaterThan=" + id);

        defaultLibraryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultLibraryShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLibrariesByGroupIdIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where groupId equals to DEFAULT_GROUP_ID
        defaultLibraryShouldBeFound("groupId.equals=" + DEFAULT_GROUP_ID);

        // Get all the libraryList where groupId equals to UPDATED_GROUP_ID
        defaultLibraryShouldNotBeFound("groupId.equals=" + UPDATED_GROUP_ID);
    }

    @Test
    @Transactional
    void getAllLibrariesByGroupIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where groupId not equals to DEFAULT_GROUP_ID
        defaultLibraryShouldNotBeFound("groupId.notEquals=" + DEFAULT_GROUP_ID);

        // Get all the libraryList where groupId not equals to UPDATED_GROUP_ID
        defaultLibraryShouldBeFound("groupId.notEquals=" + UPDATED_GROUP_ID);
    }

    @Test
    @Transactional
    void getAllLibrariesByGroupIdIsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where groupId in DEFAULT_GROUP_ID or UPDATED_GROUP_ID
        defaultLibraryShouldBeFound("groupId.in=" + DEFAULT_GROUP_ID + "," + UPDATED_GROUP_ID);

        // Get all the libraryList where groupId equals to UPDATED_GROUP_ID
        defaultLibraryShouldNotBeFound("groupId.in=" + UPDATED_GROUP_ID);
    }

    @Test
    @Transactional
    void getAllLibrariesByGroupIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where groupId is not null
        defaultLibraryShouldBeFound("groupId.specified=true");

        // Get all the libraryList where groupId is null
        defaultLibraryShouldNotBeFound("groupId.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesByGroupIdContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where groupId contains DEFAULT_GROUP_ID
        defaultLibraryShouldBeFound("groupId.contains=" + DEFAULT_GROUP_ID);

        // Get all the libraryList where groupId contains UPDATED_GROUP_ID
        defaultLibraryShouldNotBeFound("groupId.contains=" + UPDATED_GROUP_ID);
    }

    @Test
    @Transactional
    void getAllLibrariesByGroupIdNotContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where groupId does not contain DEFAULT_GROUP_ID
        defaultLibraryShouldNotBeFound("groupId.doesNotContain=" + DEFAULT_GROUP_ID);

        // Get all the libraryList where groupId does not contain UPDATED_GROUP_ID
        defaultLibraryShouldBeFound("groupId.doesNotContain=" + UPDATED_GROUP_ID);
    }

    @Test
    @Transactional
    void getAllLibrariesByArtifactIdIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where artifactId equals to DEFAULT_ARTIFACT_ID
        defaultLibraryShouldBeFound("artifactId.equals=" + DEFAULT_ARTIFACT_ID);

        // Get all the libraryList where artifactId equals to UPDATED_ARTIFACT_ID
        defaultLibraryShouldNotBeFound("artifactId.equals=" + UPDATED_ARTIFACT_ID);
    }

    @Test
    @Transactional
    void getAllLibrariesByArtifactIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where artifactId not equals to DEFAULT_ARTIFACT_ID
        defaultLibraryShouldNotBeFound("artifactId.notEquals=" + DEFAULT_ARTIFACT_ID);

        // Get all the libraryList where artifactId not equals to UPDATED_ARTIFACT_ID
        defaultLibraryShouldBeFound("artifactId.notEquals=" + UPDATED_ARTIFACT_ID);
    }

    @Test
    @Transactional
    void getAllLibrariesByArtifactIdIsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where artifactId in DEFAULT_ARTIFACT_ID or UPDATED_ARTIFACT_ID
        defaultLibraryShouldBeFound("artifactId.in=" + DEFAULT_ARTIFACT_ID + "," + UPDATED_ARTIFACT_ID);

        // Get all the libraryList where artifactId equals to UPDATED_ARTIFACT_ID
        defaultLibraryShouldNotBeFound("artifactId.in=" + UPDATED_ARTIFACT_ID);
    }

    @Test
    @Transactional
    void getAllLibrariesByArtifactIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where artifactId is not null
        defaultLibraryShouldBeFound("artifactId.specified=true");

        // Get all the libraryList where artifactId is null
        defaultLibraryShouldNotBeFound("artifactId.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesByArtifactIdContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where artifactId contains DEFAULT_ARTIFACT_ID
        defaultLibraryShouldBeFound("artifactId.contains=" + DEFAULT_ARTIFACT_ID);

        // Get all the libraryList where artifactId contains UPDATED_ARTIFACT_ID
        defaultLibraryShouldNotBeFound("artifactId.contains=" + UPDATED_ARTIFACT_ID);
    }

    @Test
    @Transactional
    void getAllLibrariesByArtifactIdNotContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where artifactId does not contain DEFAULT_ARTIFACT_ID
        defaultLibraryShouldNotBeFound("artifactId.doesNotContain=" + DEFAULT_ARTIFACT_ID);

        // Get all the libraryList where artifactId does not contain UPDATED_ARTIFACT_ID
        defaultLibraryShouldBeFound("artifactId.doesNotContain=" + UPDATED_ARTIFACT_ID);
    }

    @Test
    @Transactional
    void getAllLibrariesByVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where version equals to DEFAULT_VERSION
        defaultLibraryShouldBeFound("version.equals=" + DEFAULT_VERSION);

        // Get all the libraryList where version equals to UPDATED_VERSION
        defaultLibraryShouldNotBeFound("version.equals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllLibrariesByVersionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where version not equals to DEFAULT_VERSION
        defaultLibraryShouldNotBeFound("version.notEquals=" + DEFAULT_VERSION);

        // Get all the libraryList where version not equals to UPDATED_VERSION
        defaultLibraryShouldBeFound("version.notEquals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllLibrariesByVersionIsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where version in DEFAULT_VERSION or UPDATED_VERSION
        defaultLibraryShouldBeFound("version.in=" + DEFAULT_VERSION + "," + UPDATED_VERSION);

        // Get all the libraryList where version equals to UPDATED_VERSION
        defaultLibraryShouldNotBeFound("version.in=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllLibrariesByVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where version is not null
        defaultLibraryShouldBeFound("version.specified=true");

        // Get all the libraryList where version is null
        defaultLibraryShouldNotBeFound("version.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesByVersionContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where version contains DEFAULT_VERSION
        defaultLibraryShouldBeFound("version.contains=" + DEFAULT_VERSION);

        // Get all the libraryList where version contains UPDATED_VERSION
        defaultLibraryShouldNotBeFound("version.contains=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllLibrariesByVersionNotContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where version does not contain DEFAULT_VERSION
        defaultLibraryShouldNotBeFound("version.doesNotContain=" + DEFAULT_VERSION);

        // Get all the libraryList where version does not contain UPDATED_VERSION
        defaultLibraryShouldBeFound("version.doesNotContain=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllLibrariesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where type equals to DEFAULT_TYPE
        defaultLibraryShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the libraryList where type equals to UPDATED_TYPE
        defaultLibraryShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllLibrariesByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where type not equals to DEFAULT_TYPE
        defaultLibraryShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the libraryList where type not equals to UPDATED_TYPE
        defaultLibraryShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllLibrariesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultLibraryShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the libraryList where type equals to UPDATED_TYPE
        defaultLibraryShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllLibrariesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where type is not null
        defaultLibraryShouldBeFound("type.specified=true");

        // Get all the libraryList where type is null
        defaultLibraryShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesByOriginalLicenseIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where originalLicense equals to DEFAULT_ORIGINAL_LICENSE
        defaultLibraryShouldBeFound("originalLicense.equals=" + DEFAULT_ORIGINAL_LICENSE);

        // Get all the libraryList where originalLicense equals to UPDATED_ORIGINAL_LICENSE
        defaultLibraryShouldNotBeFound("originalLicense.equals=" + UPDATED_ORIGINAL_LICENSE);
    }

    @Test
    @Transactional
    void getAllLibrariesByOriginalLicenseIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where originalLicense not equals to DEFAULT_ORIGINAL_LICENSE
        defaultLibraryShouldNotBeFound("originalLicense.notEquals=" + DEFAULT_ORIGINAL_LICENSE);

        // Get all the libraryList where originalLicense not equals to UPDATED_ORIGINAL_LICENSE
        defaultLibraryShouldBeFound("originalLicense.notEquals=" + UPDATED_ORIGINAL_LICENSE);
    }

    @Test
    @Transactional
    void getAllLibrariesByOriginalLicenseIsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where originalLicense in DEFAULT_ORIGINAL_LICENSE or UPDATED_ORIGINAL_LICENSE
        defaultLibraryShouldBeFound("originalLicense.in=" + DEFAULT_ORIGINAL_LICENSE + "," + UPDATED_ORIGINAL_LICENSE);

        // Get all the libraryList where originalLicense equals to UPDATED_ORIGINAL_LICENSE
        defaultLibraryShouldNotBeFound("originalLicense.in=" + UPDATED_ORIGINAL_LICENSE);
    }

    @Test
    @Transactional
    void getAllLibrariesByOriginalLicenseIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where originalLicense is not null
        defaultLibraryShouldBeFound("originalLicense.specified=true");

        // Get all the libraryList where originalLicense is null
        defaultLibraryShouldNotBeFound("originalLicense.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesByOriginalLicenseContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where originalLicense contains DEFAULT_ORIGINAL_LICENSE
        defaultLibraryShouldBeFound("originalLicense.contains=" + DEFAULT_ORIGINAL_LICENSE);

        // Get all the libraryList where originalLicense contains UPDATED_ORIGINAL_LICENSE
        defaultLibraryShouldNotBeFound("originalLicense.contains=" + UPDATED_ORIGINAL_LICENSE);
    }

    @Test
    @Transactional
    void getAllLibrariesByOriginalLicenseNotContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where originalLicense does not contain DEFAULT_ORIGINAL_LICENSE
        defaultLibraryShouldNotBeFound("originalLicense.doesNotContain=" + DEFAULT_ORIGINAL_LICENSE);

        // Get all the libraryList where originalLicense does not contain UPDATED_ORIGINAL_LICENSE
        defaultLibraryShouldBeFound("originalLicense.doesNotContain=" + UPDATED_ORIGINAL_LICENSE);
    }

    @Test
    @Transactional
    void getAllLibrariesByLicenseUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where licenseUrl equals to DEFAULT_LICENSE_URL
        defaultLibraryShouldBeFound("licenseUrl.equals=" + DEFAULT_LICENSE_URL);

        // Get all the libraryList where licenseUrl equals to UPDATED_LICENSE_URL
        defaultLibraryShouldNotBeFound("licenseUrl.equals=" + UPDATED_LICENSE_URL);
    }

    @Test
    @Transactional
    void getAllLibrariesByLicenseUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where licenseUrl not equals to DEFAULT_LICENSE_URL
        defaultLibraryShouldNotBeFound("licenseUrl.notEquals=" + DEFAULT_LICENSE_URL);

        // Get all the libraryList where licenseUrl not equals to UPDATED_LICENSE_URL
        defaultLibraryShouldBeFound("licenseUrl.notEquals=" + UPDATED_LICENSE_URL);
    }

    @Test
    @Transactional
    void getAllLibrariesByLicenseUrlIsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where licenseUrl in DEFAULT_LICENSE_URL or UPDATED_LICENSE_URL
        defaultLibraryShouldBeFound("licenseUrl.in=" + DEFAULT_LICENSE_URL + "," + UPDATED_LICENSE_URL);

        // Get all the libraryList where licenseUrl equals to UPDATED_LICENSE_URL
        defaultLibraryShouldNotBeFound("licenseUrl.in=" + UPDATED_LICENSE_URL);
    }

    @Test
    @Transactional
    void getAllLibrariesByLicenseUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where licenseUrl is not null
        defaultLibraryShouldBeFound("licenseUrl.specified=true");

        // Get all the libraryList where licenseUrl is null
        defaultLibraryShouldNotBeFound("licenseUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesByLicenseUrlContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where licenseUrl contains DEFAULT_LICENSE_URL
        defaultLibraryShouldBeFound("licenseUrl.contains=" + DEFAULT_LICENSE_URL);

        // Get all the libraryList where licenseUrl contains UPDATED_LICENSE_URL
        defaultLibraryShouldNotBeFound("licenseUrl.contains=" + UPDATED_LICENSE_URL);
    }

    @Test
    @Transactional
    void getAllLibrariesByLicenseUrlNotContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where licenseUrl does not contain DEFAULT_LICENSE_URL
        defaultLibraryShouldNotBeFound("licenseUrl.doesNotContain=" + DEFAULT_LICENSE_URL);

        // Get all the libraryList where licenseUrl does not contain UPDATED_LICENSE_URL
        defaultLibraryShouldBeFound("licenseUrl.doesNotContain=" + UPDATED_LICENSE_URL);
    }

    @Test
    @Transactional
    void getAllLibrariesBySourceCodeUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where sourceCodeUrl equals to DEFAULT_SOURCE_CODE_URL
        defaultLibraryShouldBeFound("sourceCodeUrl.equals=" + DEFAULT_SOURCE_CODE_URL);

        // Get all the libraryList where sourceCodeUrl equals to UPDATED_SOURCE_CODE_URL
        defaultLibraryShouldNotBeFound("sourceCodeUrl.equals=" + UPDATED_SOURCE_CODE_URL);
    }

    @Test
    @Transactional
    void getAllLibrariesBySourceCodeUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where sourceCodeUrl not equals to DEFAULT_SOURCE_CODE_URL
        defaultLibraryShouldNotBeFound("sourceCodeUrl.notEquals=" + DEFAULT_SOURCE_CODE_URL);

        // Get all the libraryList where sourceCodeUrl not equals to UPDATED_SOURCE_CODE_URL
        defaultLibraryShouldBeFound("sourceCodeUrl.notEquals=" + UPDATED_SOURCE_CODE_URL);
    }

    @Test
    @Transactional
    void getAllLibrariesBySourceCodeUrlIsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where sourceCodeUrl in DEFAULT_SOURCE_CODE_URL or UPDATED_SOURCE_CODE_URL
        defaultLibraryShouldBeFound("sourceCodeUrl.in=" + DEFAULT_SOURCE_CODE_URL + "," + UPDATED_SOURCE_CODE_URL);

        // Get all the libraryList where sourceCodeUrl equals to UPDATED_SOURCE_CODE_URL
        defaultLibraryShouldNotBeFound("sourceCodeUrl.in=" + UPDATED_SOURCE_CODE_URL);
    }

    @Test
    @Transactional
    void getAllLibrariesBySourceCodeUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where sourceCodeUrl is not null
        defaultLibraryShouldBeFound("sourceCodeUrl.specified=true");

        // Get all the libraryList where sourceCodeUrl is null
        defaultLibraryShouldNotBeFound("sourceCodeUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesBySourceCodeUrlContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where sourceCodeUrl contains DEFAULT_SOURCE_CODE_URL
        defaultLibraryShouldBeFound("sourceCodeUrl.contains=" + DEFAULT_SOURCE_CODE_URL);

        // Get all the libraryList where sourceCodeUrl contains UPDATED_SOURCE_CODE_URL
        defaultLibraryShouldNotBeFound("sourceCodeUrl.contains=" + UPDATED_SOURCE_CODE_URL);
    }

    @Test
    @Transactional
    void getAllLibrariesBySourceCodeUrlNotContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where sourceCodeUrl does not contain DEFAULT_SOURCE_CODE_URL
        defaultLibraryShouldNotBeFound("sourceCodeUrl.doesNotContain=" + DEFAULT_SOURCE_CODE_URL);

        // Get all the libraryList where sourceCodeUrl does not contain UPDATED_SOURCE_CODE_URL
        defaultLibraryShouldBeFound("sourceCodeUrl.doesNotContain=" + UPDATED_SOURCE_CODE_URL);
    }

    @Test
    @Transactional
    void getAllLibrariesBypUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where pUrl equals to DEFAULT_P_URL
        defaultLibraryShouldBeFound("pUrl.equals=" + DEFAULT_P_URL);

        // Get all the libraryList where pUrl equals to UPDATED_P_URL
        defaultLibraryShouldNotBeFound("pUrl.equals=" + UPDATED_P_URL);
    }

    @Test
    @Transactional
    void getAllLibrariesBypUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where pUrl not equals to DEFAULT_P_URL
        defaultLibraryShouldNotBeFound("pUrl.notEquals=" + DEFAULT_P_URL);

        // Get all the libraryList where pUrl not equals to UPDATED_P_URL
        defaultLibraryShouldBeFound("pUrl.notEquals=" + UPDATED_P_URL);
    }

    @Test
    @Transactional
    void getAllLibrariesBypUrlIsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where pUrl in DEFAULT_P_URL or UPDATED_P_URL
        defaultLibraryShouldBeFound("pUrl.in=" + DEFAULT_P_URL + "," + UPDATED_P_URL);

        // Get all the libraryList where pUrl equals to UPDATED_P_URL
        defaultLibraryShouldNotBeFound("pUrl.in=" + UPDATED_P_URL);
    }

    @Test
    @Transactional
    void getAllLibrariesBypUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where pUrl is not null
        defaultLibraryShouldBeFound("pUrl.specified=true");

        // Get all the libraryList where pUrl is null
        defaultLibraryShouldNotBeFound("pUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesBypUrlContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where pUrl contains DEFAULT_P_URL
        defaultLibraryShouldBeFound("pUrl.contains=" + DEFAULT_P_URL);

        // Get all the libraryList where pUrl contains UPDATED_P_URL
        defaultLibraryShouldNotBeFound("pUrl.contains=" + UPDATED_P_URL);
    }

    @Test
    @Transactional
    void getAllLibrariesBypUrlNotContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where pUrl does not contain DEFAULT_P_URL
        defaultLibraryShouldNotBeFound("pUrl.doesNotContain=" + DEFAULT_P_URL);

        // Get all the libraryList where pUrl does not contain UPDATED_P_URL
        defaultLibraryShouldBeFound("pUrl.doesNotContain=" + UPDATED_P_URL);
    }

    @Test
    @Transactional
    void getAllLibrariesByCopyrightIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where copyright equals to DEFAULT_COPYRIGHT
        defaultLibraryShouldBeFound("copyright.equals=" + DEFAULT_COPYRIGHT);

        // Get all the libraryList where copyright equals to UPDATED_COPYRIGHT
        defaultLibraryShouldNotBeFound("copyright.equals=" + UPDATED_COPYRIGHT);
    }

    @Test
    @Transactional
    void getAllLibrariesByCopyrightIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where copyright not equals to DEFAULT_COPYRIGHT
        defaultLibraryShouldNotBeFound("copyright.notEquals=" + DEFAULT_COPYRIGHT);

        // Get all the libraryList where copyright not equals to UPDATED_COPYRIGHT
        defaultLibraryShouldBeFound("copyright.notEquals=" + UPDATED_COPYRIGHT);
    }

    @Test
    @Transactional
    void getAllLibrariesByCopyrightIsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where copyright in DEFAULT_COPYRIGHT or UPDATED_COPYRIGHT
        defaultLibraryShouldBeFound("copyright.in=" + DEFAULT_COPYRIGHT + "," + UPDATED_COPYRIGHT);

        // Get all the libraryList where copyright equals to UPDATED_COPYRIGHT
        defaultLibraryShouldNotBeFound("copyright.in=" + UPDATED_COPYRIGHT);
    }

    @Test
    @Transactional
    void getAllLibrariesByCopyrightIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where copyright is not null
        defaultLibraryShouldBeFound("copyright.specified=true");

        // Get all the libraryList where copyright is null
        defaultLibraryShouldNotBeFound("copyright.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesByCopyrightContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where copyright contains DEFAULT_COPYRIGHT
        defaultLibraryShouldBeFound("copyright.contains=" + DEFAULT_COPYRIGHT);

        // Get all the libraryList where copyright contains UPDATED_COPYRIGHT
        defaultLibraryShouldNotBeFound("copyright.contains=" + UPDATED_COPYRIGHT);
    }

    @Test
    @Transactional
    void getAllLibrariesByCopyrightNotContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where copyright does not contain DEFAULT_COPYRIGHT
        defaultLibraryShouldNotBeFound("copyright.doesNotContain=" + DEFAULT_COPYRIGHT);

        // Get all the libraryList where copyright does not contain UPDATED_COPYRIGHT
        defaultLibraryShouldBeFound("copyright.doesNotContain=" + UPDATED_COPYRIGHT);
    }

    @Test
    @Transactional
    void getAllLibrariesByComplianceIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where compliance equals to DEFAULT_COMPLIANCE
        defaultLibraryShouldBeFound("compliance.equals=" + DEFAULT_COMPLIANCE);

        // Get all the libraryList where compliance equals to UPDATED_COMPLIANCE
        defaultLibraryShouldNotBeFound("compliance.equals=" + UPDATED_COMPLIANCE);
    }

    @Test
    @Transactional
    void getAllLibrariesByComplianceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where compliance not equals to DEFAULT_COMPLIANCE
        defaultLibraryShouldNotBeFound("compliance.notEquals=" + DEFAULT_COMPLIANCE);

        // Get all the libraryList where compliance not equals to UPDATED_COMPLIANCE
        defaultLibraryShouldBeFound("compliance.notEquals=" + UPDATED_COMPLIANCE);
    }

    @Test
    @Transactional
    void getAllLibrariesByComplianceIsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where compliance in DEFAULT_COMPLIANCE or UPDATED_COMPLIANCE
        defaultLibraryShouldBeFound("compliance.in=" + DEFAULT_COMPLIANCE + "," + UPDATED_COMPLIANCE);

        // Get all the libraryList where compliance equals to UPDATED_COMPLIANCE
        defaultLibraryShouldNotBeFound("compliance.in=" + UPDATED_COMPLIANCE);
    }

    @Test
    @Transactional
    void getAllLibrariesByComplianceIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where compliance is not null
        defaultLibraryShouldBeFound("compliance.specified=true");

        // Get all the libraryList where compliance is null
        defaultLibraryShouldNotBeFound("compliance.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesByComplianceContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where compliance contains DEFAULT_COMPLIANCE
        defaultLibraryShouldBeFound("compliance.contains=" + DEFAULT_COMPLIANCE);

        // Get all the libraryList where compliance contains UPDATED_COMPLIANCE
        defaultLibraryShouldNotBeFound("compliance.contains=" + UPDATED_COMPLIANCE);
    }

    @Test
    @Transactional
    void getAllLibrariesByComplianceNotContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where compliance does not contain DEFAULT_COMPLIANCE
        defaultLibraryShouldNotBeFound("compliance.doesNotContain=" + DEFAULT_COMPLIANCE);

        // Get all the libraryList where compliance does not contain UPDATED_COMPLIANCE
        defaultLibraryShouldBeFound("compliance.doesNotContain=" + UPDATED_COMPLIANCE);
    }

    @Test
    @Transactional
    void getAllLibrariesByComplianceCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where complianceComment equals to DEFAULT_COMPLIANCE_COMMENT
        defaultLibraryShouldBeFound("complianceComment.equals=" + DEFAULT_COMPLIANCE_COMMENT);

        // Get all the libraryList where complianceComment equals to UPDATED_COMPLIANCE_COMMENT
        defaultLibraryShouldNotBeFound("complianceComment.equals=" + UPDATED_COMPLIANCE_COMMENT);
    }

    @Test
    @Transactional
    void getAllLibrariesByComplianceCommentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where complianceComment not equals to DEFAULT_COMPLIANCE_COMMENT
        defaultLibraryShouldNotBeFound("complianceComment.notEquals=" + DEFAULT_COMPLIANCE_COMMENT);

        // Get all the libraryList where complianceComment not equals to UPDATED_COMPLIANCE_COMMENT
        defaultLibraryShouldBeFound("complianceComment.notEquals=" + UPDATED_COMPLIANCE_COMMENT);
    }

    @Test
    @Transactional
    void getAllLibrariesByComplianceCommentIsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where complianceComment in DEFAULT_COMPLIANCE_COMMENT or UPDATED_COMPLIANCE_COMMENT
        defaultLibraryShouldBeFound("complianceComment.in=" + DEFAULT_COMPLIANCE_COMMENT + "," + UPDATED_COMPLIANCE_COMMENT);

        // Get all the libraryList where complianceComment equals to UPDATED_COMPLIANCE_COMMENT
        defaultLibraryShouldNotBeFound("complianceComment.in=" + UPDATED_COMPLIANCE_COMMENT);
    }

    @Test
    @Transactional
    void getAllLibrariesByComplianceCommentIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where complianceComment is not null
        defaultLibraryShouldBeFound("complianceComment.specified=true");

        // Get all the libraryList where complianceComment is null
        defaultLibraryShouldNotBeFound("complianceComment.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesByComplianceCommentContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where complianceComment contains DEFAULT_COMPLIANCE_COMMENT
        defaultLibraryShouldBeFound("complianceComment.contains=" + DEFAULT_COMPLIANCE_COMMENT);

        // Get all the libraryList where complianceComment contains UPDATED_COMPLIANCE_COMMENT
        defaultLibraryShouldNotBeFound("complianceComment.contains=" + UPDATED_COMPLIANCE_COMMENT);
    }

    @Test
    @Transactional
    void getAllLibrariesByComplianceCommentNotContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where complianceComment does not contain DEFAULT_COMPLIANCE_COMMENT
        defaultLibraryShouldNotBeFound("complianceComment.doesNotContain=" + DEFAULT_COMPLIANCE_COMMENT);

        // Get all the libraryList where complianceComment does not contain UPDATED_COMPLIANCE_COMMENT
        defaultLibraryShouldBeFound("complianceComment.doesNotContain=" + UPDATED_COMPLIANCE_COMMENT);
    }

    @Test
    @Transactional
    void getAllLibrariesByCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where comment equals to DEFAULT_COMMENT
        defaultLibraryShouldBeFound("comment.equals=" + DEFAULT_COMMENT);

        // Get all the libraryList where comment equals to UPDATED_COMMENT
        defaultLibraryShouldNotBeFound("comment.equals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllLibrariesByCommentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where comment not equals to DEFAULT_COMMENT
        defaultLibraryShouldNotBeFound("comment.notEquals=" + DEFAULT_COMMENT);

        // Get all the libraryList where comment not equals to UPDATED_COMMENT
        defaultLibraryShouldBeFound("comment.notEquals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllLibrariesByCommentIsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where comment in DEFAULT_COMMENT or UPDATED_COMMENT
        defaultLibraryShouldBeFound("comment.in=" + DEFAULT_COMMENT + "," + UPDATED_COMMENT);

        // Get all the libraryList where comment equals to UPDATED_COMMENT
        defaultLibraryShouldNotBeFound("comment.in=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllLibrariesByCommentIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where comment is not null
        defaultLibraryShouldBeFound("comment.specified=true");

        // Get all the libraryList where comment is null
        defaultLibraryShouldNotBeFound("comment.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesByCommentContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where comment contains DEFAULT_COMMENT
        defaultLibraryShouldBeFound("comment.contains=" + DEFAULT_COMMENT);

        // Get all the libraryList where comment contains UPDATED_COMMENT
        defaultLibraryShouldNotBeFound("comment.contains=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllLibrariesByCommentNotContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where comment does not contain DEFAULT_COMMENT
        defaultLibraryShouldNotBeFound("comment.doesNotContain=" + DEFAULT_COMMENT);

        // Get all the libraryList where comment does not contain UPDATED_COMMENT
        defaultLibraryShouldBeFound("comment.doesNotContain=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllLibrariesByReviewedIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where reviewed equals to DEFAULT_REVIEWED
        defaultLibraryShouldBeFound("reviewed.equals=" + DEFAULT_REVIEWED);

        // Get all the libraryList where reviewed equals to UPDATED_REVIEWED
        defaultLibraryShouldNotBeFound("reviewed.equals=" + UPDATED_REVIEWED);
    }

    @Test
    @Transactional
    void getAllLibrariesByReviewedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where reviewed not equals to DEFAULT_REVIEWED
        defaultLibraryShouldNotBeFound("reviewed.notEquals=" + DEFAULT_REVIEWED);

        // Get all the libraryList where reviewed not equals to UPDATED_REVIEWED
        defaultLibraryShouldBeFound("reviewed.notEquals=" + UPDATED_REVIEWED);
    }

    @Test
    @Transactional
    void getAllLibrariesByReviewedIsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where reviewed in DEFAULT_REVIEWED or UPDATED_REVIEWED
        defaultLibraryShouldBeFound("reviewed.in=" + DEFAULT_REVIEWED + "," + UPDATED_REVIEWED);

        // Get all the libraryList where reviewed equals to UPDATED_REVIEWED
        defaultLibraryShouldNotBeFound("reviewed.in=" + UPDATED_REVIEWED);
    }

    @Test
    @Transactional
    void getAllLibrariesByReviewedIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where reviewed is not null
        defaultLibraryShouldBeFound("reviewed.specified=true");

        // Get all the libraryList where reviewed is null
        defaultLibraryShouldNotBeFound("reviewed.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesByLastReviewedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where lastReviewedDate equals to DEFAULT_LAST_REVIEWED_DATE
        defaultLibraryShouldBeFound("lastReviewedDate.equals=" + DEFAULT_LAST_REVIEWED_DATE);

        // Get all the libraryList where lastReviewedDate equals to UPDATED_LAST_REVIEWED_DATE
        defaultLibraryShouldNotBeFound("lastReviewedDate.equals=" + UPDATED_LAST_REVIEWED_DATE);
    }

    @Test
    @Transactional
    void getAllLibrariesByLastReviewedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where lastReviewedDate not equals to DEFAULT_LAST_REVIEWED_DATE
        defaultLibraryShouldNotBeFound("lastReviewedDate.notEquals=" + DEFAULT_LAST_REVIEWED_DATE);

        // Get all the libraryList where lastReviewedDate not equals to UPDATED_LAST_REVIEWED_DATE
        defaultLibraryShouldBeFound("lastReviewedDate.notEquals=" + UPDATED_LAST_REVIEWED_DATE);
    }

    @Test
    @Transactional
    void getAllLibrariesByLastReviewedDateIsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where lastReviewedDate in DEFAULT_LAST_REVIEWED_DATE or UPDATED_LAST_REVIEWED_DATE
        defaultLibraryShouldBeFound("lastReviewedDate.in=" + DEFAULT_LAST_REVIEWED_DATE + "," + UPDATED_LAST_REVIEWED_DATE);

        // Get all the libraryList where lastReviewedDate equals to UPDATED_LAST_REVIEWED_DATE
        defaultLibraryShouldNotBeFound("lastReviewedDate.in=" + UPDATED_LAST_REVIEWED_DATE);
    }

    @Test
    @Transactional
    void getAllLibrariesByLastReviewedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where lastReviewedDate is not null
        defaultLibraryShouldBeFound("lastReviewedDate.specified=true");

        // Get all the libraryList where lastReviewedDate is null
        defaultLibraryShouldNotBeFound("lastReviewedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesByLastReviewedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where lastReviewedDate is greater than or equal to DEFAULT_LAST_REVIEWED_DATE
        defaultLibraryShouldBeFound("lastReviewedDate.greaterThanOrEqual=" + DEFAULT_LAST_REVIEWED_DATE);

        // Get all the libraryList where lastReviewedDate is greater than or equal to UPDATED_LAST_REVIEWED_DATE
        defaultLibraryShouldNotBeFound("lastReviewedDate.greaterThanOrEqual=" + UPDATED_LAST_REVIEWED_DATE);
    }

    @Test
    @Transactional
    void getAllLibrariesByLastReviewedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where lastReviewedDate is less than or equal to DEFAULT_LAST_REVIEWED_DATE
        defaultLibraryShouldBeFound("lastReviewedDate.lessThanOrEqual=" + DEFAULT_LAST_REVIEWED_DATE);

        // Get all the libraryList where lastReviewedDate is less than or equal to SMALLER_LAST_REVIEWED_DATE
        defaultLibraryShouldNotBeFound("lastReviewedDate.lessThanOrEqual=" + SMALLER_LAST_REVIEWED_DATE);
    }

    @Test
    @Transactional
    void getAllLibrariesByLastReviewedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where lastReviewedDate is less than DEFAULT_LAST_REVIEWED_DATE
        defaultLibraryShouldNotBeFound("lastReviewedDate.lessThan=" + DEFAULT_LAST_REVIEWED_DATE);

        // Get all the libraryList where lastReviewedDate is less than UPDATED_LAST_REVIEWED_DATE
        defaultLibraryShouldBeFound("lastReviewedDate.lessThan=" + UPDATED_LAST_REVIEWED_DATE);
    }

    @Test
    @Transactional
    void getAllLibrariesByLastReviewedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where lastReviewedDate is greater than DEFAULT_LAST_REVIEWED_DATE
        defaultLibraryShouldNotBeFound("lastReviewedDate.greaterThan=" + DEFAULT_LAST_REVIEWED_DATE);

        // Get all the libraryList where lastReviewedDate is greater than SMALLER_LAST_REVIEWED_DATE
        defaultLibraryShouldBeFound("lastReviewedDate.greaterThan=" + SMALLER_LAST_REVIEWED_DATE);
    }

    @Test
    @Transactional
    void getAllLibrariesByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where createdDate equals to DEFAULT_CREATED_DATE
        defaultLibraryShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the libraryList where createdDate equals to UPDATED_CREATED_DATE
        defaultLibraryShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllLibrariesByCreatedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where createdDate not equals to DEFAULT_CREATED_DATE
        defaultLibraryShouldNotBeFound("createdDate.notEquals=" + DEFAULT_CREATED_DATE);

        // Get all the libraryList where createdDate not equals to UPDATED_CREATED_DATE
        defaultLibraryShouldBeFound("createdDate.notEquals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllLibrariesByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultLibraryShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the libraryList where createdDate equals to UPDATED_CREATED_DATE
        defaultLibraryShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllLibrariesByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where createdDate is not null
        defaultLibraryShouldBeFound("createdDate.specified=true");

        // Get all the libraryList where createdDate is null
        defaultLibraryShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesByCreatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where createdDate is greater than or equal to DEFAULT_CREATED_DATE
        defaultLibraryShouldBeFound("createdDate.greaterThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the libraryList where createdDate is greater than or equal to UPDATED_CREATED_DATE
        defaultLibraryShouldNotBeFound("createdDate.greaterThanOrEqual=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllLibrariesByCreatedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where createdDate is less than or equal to DEFAULT_CREATED_DATE
        defaultLibraryShouldBeFound("createdDate.lessThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the libraryList where createdDate is less than or equal to SMALLER_CREATED_DATE
        defaultLibraryShouldNotBeFound("createdDate.lessThanOrEqual=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllLibrariesByCreatedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where createdDate is less than DEFAULT_CREATED_DATE
        defaultLibraryShouldNotBeFound("createdDate.lessThan=" + DEFAULT_CREATED_DATE);

        // Get all the libraryList where createdDate is less than UPDATED_CREATED_DATE
        defaultLibraryShouldBeFound("createdDate.lessThan=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllLibrariesByCreatedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where createdDate is greater than DEFAULT_CREATED_DATE
        defaultLibraryShouldNotBeFound("createdDate.greaterThan=" + DEFAULT_CREATED_DATE);

        // Get all the libraryList where createdDate is greater than SMALLER_CREATED_DATE
        defaultLibraryShouldBeFound("createdDate.greaterThan=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllLibrariesByHideForPublishingIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where hideForPublishing equals to DEFAULT_HIDE_FOR_PUBLISHING
        defaultLibraryShouldBeFound("hideForPublishing.equals=" + DEFAULT_HIDE_FOR_PUBLISHING);

        // Get all the libraryList where hideForPublishing equals to UPDATED_HIDE_FOR_PUBLISHING
        defaultLibraryShouldNotBeFound("hideForPublishing.equals=" + UPDATED_HIDE_FOR_PUBLISHING);
    }

    @Test
    @Transactional
    void getAllLibrariesByHideForPublishingIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where hideForPublishing not equals to DEFAULT_HIDE_FOR_PUBLISHING
        defaultLibraryShouldNotBeFound("hideForPublishing.notEquals=" + DEFAULT_HIDE_FOR_PUBLISHING);

        // Get all the libraryList where hideForPublishing not equals to UPDATED_HIDE_FOR_PUBLISHING
        defaultLibraryShouldBeFound("hideForPublishing.notEquals=" + UPDATED_HIDE_FOR_PUBLISHING);
    }

    @Test
    @Transactional
    void getAllLibrariesByHideForPublishingIsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where hideForPublishing in DEFAULT_HIDE_FOR_PUBLISHING or UPDATED_HIDE_FOR_PUBLISHING
        defaultLibraryShouldBeFound("hideForPublishing.in=" + DEFAULT_HIDE_FOR_PUBLISHING + "," + UPDATED_HIDE_FOR_PUBLISHING);

        // Get all the libraryList where hideForPublishing equals to UPDATED_HIDE_FOR_PUBLISHING
        defaultLibraryShouldNotBeFound("hideForPublishing.in=" + UPDATED_HIDE_FOR_PUBLISHING);
    }

    @Test
    @Transactional
    void getAllLibrariesByHideForPublishingIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where hideForPublishing is not null
        defaultLibraryShouldBeFound("hideForPublishing.specified=true");

        // Get all the libraryList where hideForPublishing is null
        defaultLibraryShouldNotBeFound("hideForPublishing.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesByMd5IsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where md5 equals to DEFAULT_MD_5
        defaultLibraryShouldBeFound("md5.equals=" + DEFAULT_MD_5);

        // Get all the libraryList where md5 equals to UPDATED_MD_5
        defaultLibraryShouldNotBeFound("md5.equals=" + UPDATED_MD_5);
    }

    @Test
    @Transactional
    void getAllLibrariesByMd5IsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where md5 not equals to DEFAULT_MD_5
        defaultLibraryShouldNotBeFound("md5.notEquals=" + DEFAULT_MD_5);

        // Get all the libraryList where md5 not equals to UPDATED_MD_5
        defaultLibraryShouldBeFound("md5.notEquals=" + UPDATED_MD_5);
    }

    @Test
    @Transactional
    void getAllLibrariesByMd5IsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where md5 in DEFAULT_MD_5 or UPDATED_MD_5
        defaultLibraryShouldBeFound("md5.in=" + DEFAULT_MD_5 + "," + UPDATED_MD_5);

        // Get all the libraryList where md5 equals to UPDATED_MD_5
        defaultLibraryShouldNotBeFound("md5.in=" + UPDATED_MD_5);
    }

    @Test
    @Transactional
    void getAllLibrariesByMd5IsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where md5 is not null
        defaultLibraryShouldBeFound("md5.specified=true");

        // Get all the libraryList where md5 is null
        defaultLibraryShouldNotBeFound("md5.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesByMd5ContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where md5 contains DEFAULT_MD_5
        defaultLibraryShouldBeFound("md5.contains=" + DEFAULT_MD_5);

        // Get all the libraryList where md5 contains UPDATED_MD_5
        defaultLibraryShouldNotBeFound("md5.contains=" + UPDATED_MD_5);
    }

    @Test
    @Transactional
    void getAllLibrariesByMd5NotContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where md5 does not contain DEFAULT_MD_5
        defaultLibraryShouldNotBeFound("md5.doesNotContain=" + DEFAULT_MD_5);

        // Get all the libraryList where md5 does not contain UPDATED_MD_5
        defaultLibraryShouldBeFound("md5.doesNotContain=" + UPDATED_MD_5);
    }

    @Test
    @Transactional
    void getAllLibrariesBySha1IsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where sha1 equals to DEFAULT_SHA_1
        defaultLibraryShouldBeFound("sha1.equals=" + DEFAULT_SHA_1);

        // Get all the libraryList where sha1 equals to UPDATED_SHA_1
        defaultLibraryShouldNotBeFound("sha1.equals=" + UPDATED_SHA_1);
    }

    @Test
    @Transactional
    void getAllLibrariesBySha1IsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where sha1 not equals to DEFAULT_SHA_1
        defaultLibraryShouldNotBeFound("sha1.notEquals=" + DEFAULT_SHA_1);

        // Get all the libraryList where sha1 not equals to UPDATED_SHA_1
        defaultLibraryShouldBeFound("sha1.notEquals=" + UPDATED_SHA_1);
    }

    @Test
    @Transactional
    void getAllLibrariesBySha1IsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where sha1 in DEFAULT_SHA_1 or UPDATED_SHA_1
        defaultLibraryShouldBeFound("sha1.in=" + DEFAULT_SHA_1 + "," + UPDATED_SHA_1);

        // Get all the libraryList where sha1 equals to UPDATED_SHA_1
        defaultLibraryShouldNotBeFound("sha1.in=" + UPDATED_SHA_1);
    }

    @Test
    @Transactional
    void getAllLibrariesBySha1IsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where sha1 is not null
        defaultLibraryShouldBeFound("sha1.specified=true");

        // Get all the libraryList where sha1 is null
        defaultLibraryShouldNotBeFound("sha1.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesBySha1ContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where sha1 contains DEFAULT_SHA_1
        defaultLibraryShouldBeFound("sha1.contains=" + DEFAULT_SHA_1);

        // Get all the libraryList where sha1 contains UPDATED_SHA_1
        defaultLibraryShouldNotBeFound("sha1.contains=" + UPDATED_SHA_1);
    }

    @Test
    @Transactional
    void getAllLibrariesBySha1NotContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where sha1 does not contain DEFAULT_SHA_1
        defaultLibraryShouldNotBeFound("sha1.doesNotContain=" + DEFAULT_SHA_1);

        // Get all the libraryList where sha1 does not contain UPDATED_SHA_1
        defaultLibraryShouldBeFound("sha1.doesNotContain=" + UPDATED_SHA_1);
    }

    @Test
    @Transactional
    void getAllLibrariesByLicenseIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);
        LicensePerLibrary license;
        if (TestUtil.findAll(em, LicensePerLibrary.class).isEmpty()) {
            license = LicensePerLibraryResourceIT.createEntity(em);
            em.persist(license);
            em.flush();
        } else {
            license = TestUtil.findAll(em, LicensePerLibrary.class).get(0);
        }
        em.persist(license);
        em.flush();
        library.addLicenses(license);
        libraryRepository.saveAndFlush(library);
        Long licenseId = license.getId();

        // Get all the libraryList where license equals to licenseId
        defaultLibraryShouldBeFound("licenseId.equals=" + licenseId);

        // Get all the libraryList where license equals to (licenseId + 1)
        defaultLibraryShouldNotBeFound("licenseId.equals=" + (licenseId + 1));
    }

    @Test
    @Transactional
    void getAllLibrariesByErrorLogIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);
        LibraryErrorLog errorLog;
        if (TestUtil.findAll(em, LibraryErrorLog.class).isEmpty()) {
            errorLog = LibraryErrorLogResourceIT.createEntity(em);
            em.persist(errorLog);
            em.flush();
        } else {
            errorLog = TestUtil.findAll(em, LibraryErrorLog.class).get(0);
        }
        em.persist(errorLog);
        em.flush();
        library.addErrorLog(errorLog);
        libraryRepository.saveAndFlush(library);
        Long errorLogId = errorLog.getId();

        // Get all the libraryList where errorLog equals to errorLogId
        defaultLibraryShouldBeFound("errorLogId.equals=" + errorLogId);

        // Get all the libraryList where errorLog equals to (errorLogId + 1)
        defaultLibraryShouldNotBeFound("errorLogId.equals=" + (errorLogId + 1));
    }

    @Test
    @Transactional
    void getAllLibrariesByLastReviewedByIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);
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
        library.setLastReviewedBy(lastReviewedBy);
        libraryRepository.saveAndFlush(library);
        Long lastReviewedById = lastReviewedBy.getId();

        // Get all the libraryList where lastReviewedBy equals to lastReviewedById
        defaultLibraryShouldBeFound("lastReviewedById.equals=" + lastReviewedById);

        // Get all the libraryList where lastReviewedBy equals to (lastReviewedById + 1)
        defaultLibraryShouldNotBeFound("lastReviewedById.equals=" + (lastReviewedById + 1));
    }

    @Test
    @Transactional
    void getAllLibrariesByLicenseToPublishIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);
        License licenseToPublish;
        if (TestUtil.findAll(em, License.class).isEmpty()) {
            licenseToPublish = LicenseResourceIT.createEntity(em);
            em.persist(licenseToPublish);
            em.flush();
        } else {
            licenseToPublish = TestUtil.findAll(em, License.class).get(0);
        }
        em.persist(licenseToPublish);
        em.flush();
        library.addLicenseToPublish(licenseToPublish);
        libraryRepository.saveAndFlush(library);
        Long licenseToPublishId = licenseToPublish.getId();

        // Get all the libraryList where licenseToPublish equals to licenseToPublishId
        defaultLibraryShouldBeFound("licenseToPublishId.equals=" + licenseToPublishId);

        // Get all the libraryList where licenseToPublish equals to (licenseToPublishId + 1)
        defaultLibraryShouldNotBeFound("licenseToPublishId.equals=" + (licenseToPublishId + 1));
    }

    @Test
    @Transactional
    void getAllLibrariesByLicenseOfFilesIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);
        License licenseOfFiles;
        if (TestUtil.findAll(em, License.class).isEmpty()) {
            licenseOfFiles = LicenseResourceIT.createEntity(em);
            em.persist(licenseOfFiles);
            em.flush();
        } else {
            licenseOfFiles = TestUtil.findAll(em, License.class).get(0);
        }
        em.persist(licenseOfFiles);
        em.flush();
        library.addLicenseOfFiles(licenseOfFiles);
        libraryRepository.saveAndFlush(library);
        Long licenseOfFilesId = licenseOfFiles.getId();

        // Get all the libraryList where licenseOfFiles equals to licenseOfFilesId
        defaultLibraryShouldBeFound("licenseOfFilesId.equals=" + licenseOfFilesId);

        // Get all the libraryList where licenseOfFiles equals to (licenseOfFilesId + 1)
        defaultLibraryShouldNotBeFound("licenseOfFilesId.equals=" + (licenseOfFilesId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLibraryShouldBeFound(String filter) throws Exception {
        restLibraryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(library.getId().intValue())))
            .andExpect(jsonPath("$.[*].groupId").value(hasItem(DEFAULT_GROUP_ID)))
            .andExpect(jsonPath("$.[*].artifactId").value(hasItem(DEFAULT_ARTIFACT_ID)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].originalLicense").value(hasItem(DEFAULT_ORIGINAL_LICENSE)))
            .andExpect(jsonPath("$.[*].licenseUrl").value(hasItem(DEFAULT_LICENSE_URL)))
            .andExpect(jsonPath("$.[*].licenseText").value(hasItem(DEFAULT_LICENSE_TEXT.toString())))
            .andExpect(jsonPath("$.[*].sourceCodeUrl").value(hasItem(DEFAULT_SOURCE_CODE_URL)))
            .andExpect(jsonPath("$.[*].pUrl").value(hasItem(DEFAULT_P_URL)))
            .andExpect(jsonPath("$.[*].copyright").value(hasItem(DEFAULT_COPYRIGHT)))
            .andExpect(jsonPath("$.[*].compliance").value(hasItem(DEFAULT_COMPLIANCE)))
            .andExpect(jsonPath("$.[*].complianceComment").value(hasItem(DEFAULT_COMPLIANCE_COMMENT)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].reviewed").value(hasItem(DEFAULT_REVIEWED.booleanValue())))
            .andExpect(jsonPath("$.[*].lastReviewedDate").value(hasItem(DEFAULT_LAST_REVIEWED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].hideForPublishing").value(hasItem(DEFAULT_HIDE_FOR_PUBLISHING.booleanValue())))
            .andExpect(jsonPath("$.[*].md5").value(hasItem(DEFAULT_MD_5)))
            .andExpect(jsonPath("$.[*].sha1").value(hasItem(DEFAULT_SHA_1)));

        // Check, that the count call also returns 1
        restLibraryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLibraryShouldNotBeFound(String filter) throws Exception {
        restLibraryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLibraryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLibrary() throws Exception {
        // Get the library
        restLibraryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLibrary() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        int databaseSizeBeforeUpdate = libraryRepository.findAll().size();

        // Update the library
        Library updatedLibrary = libraryRepository.findById(library.getId()).get();
        // Disconnect from session so that the updates on updatedLibrary are not directly saved in db
        em.detach(updatedLibrary);
        updatedLibrary
            .groupId(UPDATED_GROUP_ID)
            .artifactId(UPDATED_ARTIFACT_ID)
            .version(UPDATED_VERSION)
            .type(UPDATED_TYPE)
            .originalLicense(UPDATED_ORIGINAL_LICENSE)
            .licenseUrl(UPDATED_LICENSE_URL)
            .licenseText(UPDATED_LICENSE_TEXT)
            .sourceCodeUrl(UPDATED_SOURCE_CODE_URL)
            .pUrl(UPDATED_P_URL)
            .copyright(UPDATED_COPYRIGHT)
            .compliance(UPDATED_COMPLIANCE)
            .complianceComment(UPDATED_COMPLIANCE_COMMENT)
            .comment(UPDATED_COMMENT)
            .reviewed(UPDATED_REVIEWED)
            .lastReviewedDate(UPDATED_LAST_REVIEWED_DATE)
            .createdDate(UPDATED_CREATED_DATE)
            .hideForPublishing(UPDATED_HIDE_FOR_PUBLISHING)
            .md5(UPDATED_MD_5)
            .sha1(UPDATED_SHA_1);

        restLibraryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLibrary.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLibrary))
            )
            .andExpect(status().isOk());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeUpdate);
        Library testLibrary = libraryList.get(libraryList.size() - 1);
        assertThat(testLibrary.getGroupId()).isEqualTo(UPDATED_GROUP_ID);
        assertThat(testLibrary.getArtifactId()).isEqualTo(UPDATED_ARTIFACT_ID);
        assertThat(testLibrary.getVersion()).isEqualTo(UPDATED_VERSION);
        Assertions.assertThat(testLibrary.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testLibrary.getOriginalLicense()).isEqualTo(UPDATED_ORIGINAL_LICENSE);
        assertThat(testLibrary.getLicenseUrl()).isEqualTo(UPDATED_LICENSE_URL);
        assertThat(testLibrary.getLicenseText()).isEqualTo(UPDATED_LICENSE_TEXT);
        assertThat(testLibrary.getSourceCodeUrl()).isEqualTo(UPDATED_SOURCE_CODE_URL);
        assertThat(testLibrary.getpUrl()).isEqualTo(UPDATED_P_URL);
        assertThat(testLibrary.getCopyright()).isEqualTo(UPDATED_COPYRIGHT);
        assertThat(testLibrary.getCompliance()).isEqualTo(UPDATED_COMPLIANCE);
        assertThat(testLibrary.getComplianceComment()).isEqualTo(UPDATED_COMPLIANCE_COMMENT);
        assertThat(testLibrary.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testLibrary.getReviewed()).isEqualTo(UPDATED_REVIEWED);
        assertThat(testLibrary.getLastReviewedDate()).isEqualTo(UPDATED_LAST_REVIEWED_DATE);
        assertThat(testLibrary.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testLibrary.getHideForPublishing()).isEqualTo(UPDATED_HIDE_FOR_PUBLISHING);
        assertThat(testLibrary.getMd5()).isEqualTo(UPDATED_MD_5);
        assertThat(testLibrary.getSha1()).isEqualTo(UPDATED_SHA_1);
    }

    @Test
    @Transactional
    void putNonExistingLibrary() throws Exception {
        int databaseSizeBeforeUpdate = libraryRepository.findAll().size();
        library.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLibraryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, library.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(library))
            )
            .andExpect(status().isBadRequest());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLibrary() throws Exception {
        int databaseSizeBeforeUpdate = libraryRepository.findAll().size();
        library.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(library))
            )
            .andExpect(status().isBadRequest());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLibrary() throws Exception {
        int databaseSizeBeforeUpdate = libraryRepository.findAll().size();
        library.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(library)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLibraryWithPatch() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        int databaseSizeBeforeUpdate = libraryRepository.findAll().size();

        // Update the library using partial update
        Library partialUpdatedLibrary = new Library();
        partialUpdatedLibrary.setId(library.getId());

        partialUpdatedLibrary
            .version(UPDATED_VERSION)
            .type(UPDATED_TYPE)
            .licenseText(UPDATED_LICENSE_TEXT)
            .sourceCodeUrl(UPDATED_SOURCE_CODE_URL)
            .pUrl(UPDATED_P_URL)
            .copyright(UPDATED_COPYRIGHT)
            .compliance(UPDATED_COMPLIANCE)
            .complianceComment(UPDATED_COMPLIANCE_COMMENT)
            .reviewed(UPDATED_REVIEWED)
            .lastReviewedDate(UPDATED_LAST_REVIEWED_DATE)
            .createdDate(UPDATED_CREATED_DATE)
            .md5(UPDATED_MD_5)
            .sha1(UPDATED_SHA_1);

        restLibraryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLibrary.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLibrary))
            )
            .andExpect(status().isOk());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeUpdate);
        Library testLibrary = libraryList.get(libraryList.size() - 1);
        assertThat(testLibrary.getGroupId()).isEqualTo(DEFAULT_GROUP_ID);
        assertThat(testLibrary.getArtifactId()).isEqualTo(DEFAULT_ARTIFACT_ID);
        assertThat(testLibrary.getVersion()).isEqualTo(UPDATED_VERSION);
        Assertions.assertThat(testLibrary.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testLibrary.getOriginalLicense()).isEqualTo(DEFAULT_ORIGINAL_LICENSE);
        assertThat(testLibrary.getLicenseUrl()).isEqualTo(DEFAULT_LICENSE_URL);
        assertThat(testLibrary.getLicenseText()).isEqualTo(UPDATED_LICENSE_TEXT);
        assertThat(testLibrary.getSourceCodeUrl()).isEqualTo(UPDATED_SOURCE_CODE_URL);
        assertThat(testLibrary.getpUrl()).isEqualTo(UPDATED_P_URL);
        assertThat(testLibrary.getCopyright()).isEqualTo(UPDATED_COPYRIGHT);
        assertThat(testLibrary.getCompliance()).isEqualTo(UPDATED_COMPLIANCE);
        assertThat(testLibrary.getComplianceComment()).isEqualTo(UPDATED_COMPLIANCE_COMMENT);
        assertThat(testLibrary.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testLibrary.getReviewed()).isEqualTo(UPDATED_REVIEWED);
        assertThat(testLibrary.getLastReviewedDate()).isEqualTo(UPDATED_LAST_REVIEWED_DATE);
        assertThat(testLibrary.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testLibrary.getHideForPublishing()).isEqualTo(DEFAULT_HIDE_FOR_PUBLISHING);
        assertThat(testLibrary.getMd5()).isEqualTo(UPDATED_MD_5);
        assertThat(testLibrary.getSha1()).isEqualTo(UPDATED_SHA_1);
    }

    @Test
    @Transactional
    void fullUpdateLibraryWithPatch() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        int databaseSizeBeforeUpdate = libraryRepository.findAll().size();

        // Update the library using partial update
        Library partialUpdatedLibrary = new Library();
        partialUpdatedLibrary.setId(library.getId());

        partialUpdatedLibrary
            .groupId(UPDATED_GROUP_ID)
            .artifactId(UPDATED_ARTIFACT_ID)
            .version(UPDATED_VERSION)
            .type(UPDATED_TYPE)
            .originalLicense(UPDATED_ORIGINAL_LICENSE)
            .licenseUrl(UPDATED_LICENSE_URL)
            .licenseText(UPDATED_LICENSE_TEXT)
            .sourceCodeUrl(UPDATED_SOURCE_CODE_URL)
            .pUrl(UPDATED_P_URL)
            .copyright(UPDATED_COPYRIGHT)
            .compliance(UPDATED_COMPLIANCE)
            .complianceComment(UPDATED_COMPLIANCE_COMMENT)
            .comment(UPDATED_COMMENT)
            .reviewed(UPDATED_REVIEWED)
            .lastReviewedDate(UPDATED_LAST_REVIEWED_DATE)
            .createdDate(UPDATED_CREATED_DATE)
            .hideForPublishing(UPDATED_HIDE_FOR_PUBLISHING)
            .md5(UPDATED_MD_5)
            .sha1(UPDATED_SHA_1);

        restLibraryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLibrary.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLibrary))
            )
            .andExpect(status().isOk());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeUpdate);
        Library testLibrary = libraryList.get(libraryList.size() - 1);
        assertThat(testLibrary.getGroupId()).isEqualTo(UPDATED_GROUP_ID);
        assertThat(testLibrary.getArtifactId()).isEqualTo(UPDATED_ARTIFACT_ID);
        assertThat(testLibrary.getVersion()).isEqualTo(UPDATED_VERSION);
        Assertions.assertThat(testLibrary.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testLibrary.getOriginalLicense()).isEqualTo(UPDATED_ORIGINAL_LICENSE);
        assertThat(testLibrary.getLicenseUrl()).isEqualTo(UPDATED_LICENSE_URL);
        assertThat(testLibrary.getLicenseText()).isEqualTo(UPDATED_LICENSE_TEXT);
        assertThat(testLibrary.getSourceCodeUrl()).isEqualTo(UPDATED_SOURCE_CODE_URL);
        assertThat(testLibrary.getpUrl()).isEqualTo(UPDATED_P_URL);
        assertThat(testLibrary.getCopyright()).isEqualTo(UPDATED_COPYRIGHT);
        assertThat(testLibrary.getCompliance()).isEqualTo(UPDATED_COMPLIANCE);
        assertThat(testLibrary.getComplianceComment()).isEqualTo(UPDATED_COMPLIANCE_COMMENT);
        assertThat(testLibrary.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testLibrary.getReviewed()).isEqualTo(UPDATED_REVIEWED);
        assertThat(testLibrary.getLastReviewedDate()).isEqualTo(UPDATED_LAST_REVIEWED_DATE);
        assertThat(testLibrary.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testLibrary.getHideForPublishing()).isEqualTo(UPDATED_HIDE_FOR_PUBLISHING);
        assertThat(testLibrary.getMd5()).isEqualTo(UPDATED_MD_5);
        assertThat(testLibrary.getSha1()).isEqualTo(UPDATED_SHA_1);
    }

    @Test
    @Transactional
    void patchNonExistingLibrary() throws Exception {
        int databaseSizeBeforeUpdate = libraryRepository.findAll().size();
        library.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLibraryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, library.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(library))
            )
            .andExpect(status().isBadRequest());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLibrary() throws Exception {
        int databaseSizeBeforeUpdate = libraryRepository.findAll().size();
        library.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(library))
            )
            .andExpect(status().isBadRequest());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLibrary() throws Exception {
        int databaseSizeBeforeUpdate = libraryRepository.findAll().size();
        library.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(library)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLibrary() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        int databaseSizeBeforeDelete = libraryRepository.findAll().size();

        // Delete the library
        restLibraryMockMvc
            .perform(delete(ENTITY_API_URL_ID, library.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
