package net.regnology.lucy.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import net.regnology.lucy.IntegrationTest;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.domain.LibraryErrorLog;
import net.regnology.lucy.domain.enumeration.LogSeverity;
import net.regnology.lucy.domain.enumeration.LogStatus;
import net.regnology.lucy.repository.LibraryErrorLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link LibraryErrorLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LibraryErrorLogResourceIT {

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final LogSeverity DEFAULT_SEVERITY = LogSeverity.LOW;
    private static final LogSeverity UPDATED_SEVERITY = LogSeverity.MEDIUM;

    private static final LogStatus DEFAULT_STATUS = LogStatus.CLOSED;
    private static final LogStatus UPDATED_STATUS = LogStatus.FIXED;

    private static final Instant DEFAULT_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/library-error-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LibraryErrorLogRepository libraryErrorLogRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLibraryErrorLogMockMvc;

    private LibraryErrorLog libraryErrorLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LibraryErrorLog createEntity(EntityManager em) {
        LibraryErrorLog libraryErrorLog = new LibraryErrorLog()
            .message(DEFAULT_MESSAGE)
            .severity(DEFAULT_SEVERITY)
            .status(DEFAULT_STATUS)
            .timestamp(DEFAULT_TIMESTAMP);
        return libraryErrorLog;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LibraryErrorLog createUpdatedEntity(EntityManager em) {
        LibraryErrorLog libraryErrorLog = new LibraryErrorLog()
            .message(UPDATED_MESSAGE)
            .severity(UPDATED_SEVERITY)
            .status(UPDATED_STATUS)
            .timestamp(UPDATED_TIMESTAMP);
        return libraryErrorLog;
    }

    @BeforeEach
    public void initTest() {
        libraryErrorLog = createEntity(em);
    }

    @Test
    @Transactional
    void createLibraryErrorLog() throws Exception {
        int databaseSizeBeforeCreate = libraryErrorLogRepository.findAll().size();
        // Create the LibraryErrorLog
        restLibraryErrorLogMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(libraryErrorLog))
            )
            .andExpect(status().isCreated());

        // Validate the LibraryErrorLog in the database
        List<LibraryErrorLog> libraryErrorLogList = libraryErrorLogRepository.findAll();
        assertThat(libraryErrorLogList).hasSize(databaseSizeBeforeCreate + 1);
        LibraryErrorLog testLibraryErrorLog = libraryErrorLogList.get(libraryErrorLogList.size() - 1);
        assertThat(testLibraryErrorLog.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testLibraryErrorLog.getSeverity()).isEqualTo(DEFAULT_SEVERITY);
        assertThat(testLibraryErrorLog.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testLibraryErrorLog.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
    }

    @Test
    @Transactional
    void createLibraryErrorLogWithExistingId() throws Exception {
        // Create the LibraryErrorLog with an existing ID
        libraryErrorLog.setId(1L);

        int databaseSizeBeforeCreate = libraryErrorLogRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLibraryErrorLogMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(libraryErrorLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the LibraryErrorLog in the database
        List<LibraryErrorLog> libraryErrorLogList = libraryErrorLogRepository.findAll();
        assertThat(libraryErrorLogList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMessageIsRequired() throws Exception {
        int databaseSizeBeforeTest = libraryErrorLogRepository.findAll().size();
        // set the field null
        libraryErrorLog.setMessage(null);

        // Create the LibraryErrorLog, which fails.

        restLibraryErrorLogMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(libraryErrorLog))
            )
            .andExpect(status().isBadRequest());

        List<LibraryErrorLog> libraryErrorLogList = libraryErrorLogRepository.findAll();
        assertThat(libraryErrorLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSeverityIsRequired() throws Exception {
        int databaseSizeBeforeTest = libraryErrorLogRepository.findAll().size();
        // set the field null
        libraryErrorLog.setSeverity(null);

        // Create the LibraryErrorLog, which fails.

        restLibraryErrorLogMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(libraryErrorLog))
            )
            .andExpect(status().isBadRequest());

        List<LibraryErrorLog> libraryErrorLogList = libraryErrorLogRepository.findAll();
        assertThat(libraryErrorLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = libraryErrorLogRepository.findAll().size();
        // set the field null
        libraryErrorLog.setStatus(null);

        // Create the LibraryErrorLog, which fails.

        restLibraryErrorLogMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(libraryErrorLog))
            )
            .andExpect(status().isBadRequest());

        List<LibraryErrorLog> libraryErrorLogList = libraryErrorLogRepository.findAll();
        assertThat(libraryErrorLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTimestampIsRequired() throws Exception {
        int databaseSizeBeforeTest = libraryErrorLogRepository.findAll().size();
        // set the field null
        libraryErrorLog.setTimestamp(null);

        // Create the LibraryErrorLog, which fails.

        restLibraryErrorLogMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(libraryErrorLog))
            )
            .andExpect(status().isBadRequest());

        List<LibraryErrorLog> libraryErrorLogList = libraryErrorLogRepository.findAll();
        assertThat(libraryErrorLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogs() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList
        restLibraryErrorLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(libraryErrorLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].severity").value(hasItem(DEFAULT_SEVERITY.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())));
    }

    @Test
    @Transactional
    void getLibraryErrorLog() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get the libraryErrorLog
        restLibraryErrorLogMockMvc
            .perform(get(ENTITY_API_URL_ID, libraryErrorLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(libraryErrorLog.getId().intValue()))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.severity").value(DEFAULT_SEVERITY.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()));
    }

    @Test
    @Transactional
    void getLibraryErrorLogsByIdFiltering() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        Long id = libraryErrorLog.getId();

        defaultLibraryErrorLogShouldBeFound("id.equals=" + id);
        defaultLibraryErrorLogShouldNotBeFound("id.notEquals=" + id);

        defaultLibraryErrorLogShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultLibraryErrorLogShouldNotBeFound("id.greaterThan=" + id);

        defaultLibraryErrorLogShouldBeFound("id.lessThanOrEqual=" + id);
        defaultLibraryErrorLogShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsByMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList where message equals to DEFAULT_MESSAGE
        defaultLibraryErrorLogShouldBeFound("message.equals=" + DEFAULT_MESSAGE);

        // Get all the libraryErrorLogList where message equals to UPDATED_MESSAGE
        defaultLibraryErrorLogShouldNotBeFound("message.equals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsByMessageIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList where message not equals to DEFAULT_MESSAGE
        defaultLibraryErrorLogShouldNotBeFound("message.notEquals=" + DEFAULT_MESSAGE);

        // Get all the libraryErrorLogList where message not equals to UPDATED_MESSAGE
        defaultLibraryErrorLogShouldBeFound("message.notEquals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsByMessageIsInShouldWork() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList where message in DEFAULT_MESSAGE or UPDATED_MESSAGE
        defaultLibraryErrorLogShouldBeFound("message.in=" + DEFAULT_MESSAGE + "," + UPDATED_MESSAGE);

        // Get all the libraryErrorLogList where message equals to UPDATED_MESSAGE
        defaultLibraryErrorLogShouldNotBeFound("message.in=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsByMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList where message is not null
        defaultLibraryErrorLogShouldBeFound("message.specified=true");

        // Get all the libraryErrorLogList where message is null
        defaultLibraryErrorLogShouldNotBeFound("message.specified=false");
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsByMessageContainsSomething() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList where message contains DEFAULT_MESSAGE
        defaultLibraryErrorLogShouldBeFound("message.contains=" + DEFAULT_MESSAGE);

        // Get all the libraryErrorLogList where message contains UPDATED_MESSAGE
        defaultLibraryErrorLogShouldNotBeFound("message.contains=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsByMessageNotContainsSomething() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList where message does not contain DEFAULT_MESSAGE
        defaultLibraryErrorLogShouldNotBeFound("message.doesNotContain=" + DEFAULT_MESSAGE);

        // Get all the libraryErrorLogList where message does not contain UPDATED_MESSAGE
        defaultLibraryErrorLogShouldBeFound("message.doesNotContain=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsBySeverityIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList where severity equals to DEFAULT_SEVERITY
        defaultLibraryErrorLogShouldBeFound("severity.equals=" + DEFAULT_SEVERITY);

        // Get all the libraryErrorLogList where severity equals to UPDATED_SEVERITY
        defaultLibraryErrorLogShouldNotBeFound("severity.equals=" + UPDATED_SEVERITY);
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsBySeverityIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList where severity not equals to DEFAULT_SEVERITY
        defaultLibraryErrorLogShouldNotBeFound("severity.notEquals=" + DEFAULT_SEVERITY);

        // Get all the libraryErrorLogList where severity not equals to UPDATED_SEVERITY
        defaultLibraryErrorLogShouldBeFound("severity.notEquals=" + UPDATED_SEVERITY);
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsBySeverityIsInShouldWork() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList where severity in DEFAULT_SEVERITY or UPDATED_SEVERITY
        defaultLibraryErrorLogShouldBeFound("severity.in=" + DEFAULT_SEVERITY + "," + UPDATED_SEVERITY);

        // Get all the libraryErrorLogList where severity equals to UPDATED_SEVERITY
        defaultLibraryErrorLogShouldNotBeFound("severity.in=" + UPDATED_SEVERITY);
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsBySeverityIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList where severity is not null
        defaultLibraryErrorLogShouldBeFound("severity.specified=true");

        // Get all the libraryErrorLogList where severity is null
        defaultLibraryErrorLogShouldNotBeFound("severity.specified=false");
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList where status equals to DEFAULT_STATUS
        defaultLibraryErrorLogShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the libraryErrorLogList where status equals to UPDATED_STATUS
        defaultLibraryErrorLogShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList where status not equals to DEFAULT_STATUS
        defaultLibraryErrorLogShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the libraryErrorLogList where status not equals to UPDATED_STATUS
        defaultLibraryErrorLogShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultLibraryErrorLogShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the libraryErrorLogList where status equals to UPDATED_STATUS
        defaultLibraryErrorLogShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList where status is not null
        defaultLibraryErrorLogShouldBeFound("status.specified=true");

        // Get all the libraryErrorLogList where status is null
        defaultLibraryErrorLogShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsByTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList where timestamp equals to DEFAULT_TIMESTAMP
        defaultLibraryErrorLogShouldBeFound("timestamp.equals=" + DEFAULT_TIMESTAMP);

        // Get all the libraryErrorLogList where timestamp equals to UPDATED_TIMESTAMP
        defaultLibraryErrorLogShouldNotBeFound("timestamp.equals=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsByTimestampIsNotEqualToSomething() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList where timestamp not equals to DEFAULT_TIMESTAMP
        defaultLibraryErrorLogShouldNotBeFound("timestamp.notEquals=" + DEFAULT_TIMESTAMP);

        // Get all the libraryErrorLogList where timestamp not equals to UPDATED_TIMESTAMP
        defaultLibraryErrorLogShouldBeFound("timestamp.notEquals=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsByTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList where timestamp in DEFAULT_TIMESTAMP or UPDATED_TIMESTAMP
        defaultLibraryErrorLogShouldBeFound("timestamp.in=" + DEFAULT_TIMESTAMP + "," + UPDATED_TIMESTAMP);

        // Get all the libraryErrorLogList where timestamp equals to UPDATED_TIMESTAMP
        defaultLibraryErrorLogShouldNotBeFound("timestamp.in=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsByTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        // Get all the libraryErrorLogList where timestamp is not null
        defaultLibraryErrorLogShouldBeFound("timestamp.specified=true");

        // Get all the libraryErrorLogList where timestamp is null
        defaultLibraryErrorLogShouldNotBeFound("timestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllLibraryErrorLogsByLibraryIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);
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
        libraryErrorLog.setLibrary(library);
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);
        Long libraryId = library.getId();

        // Get all the libraryErrorLogList where library equals to libraryId
        defaultLibraryErrorLogShouldBeFound("libraryId.equals=" + libraryId);

        // Get all the libraryErrorLogList where library equals to (libraryId + 1)
        defaultLibraryErrorLogShouldNotBeFound("libraryId.equals=" + (libraryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLibraryErrorLogShouldBeFound(String filter) throws Exception {
        restLibraryErrorLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(libraryErrorLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].severity").value(hasItem(DEFAULT_SEVERITY.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())));

        // Check, that the count call also returns 1
        restLibraryErrorLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLibraryErrorLogShouldNotBeFound(String filter) throws Exception {
        restLibraryErrorLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLibraryErrorLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLibraryErrorLog() throws Exception {
        // Get the libraryErrorLog
        restLibraryErrorLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLibraryErrorLog() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        int databaseSizeBeforeUpdate = libraryErrorLogRepository.findAll().size();

        // Update the libraryErrorLog
        LibraryErrorLog updatedLibraryErrorLog = libraryErrorLogRepository.findById(libraryErrorLog.getId()).get();
        // Disconnect from session so that the updates on updatedLibraryErrorLog are not directly saved in db
        em.detach(updatedLibraryErrorLog);
        updatedLibraryErrorLog.message(UPDATED_MESSAGE).severity(UPDATED_SEVERITY).status(UPDATED_STATUS).timestamp(UPDATED_TIMESTAMP);

        restLibraryErrorLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLibraryErrorLog.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLibraryErrorLog))
            )
            .andExpect(status().isOk());

        // Validate the LibraryErrorLog in the database
        List<LibraryErrorLog> libraryErrorLogList = libraryErrorLogRepository.findAll();
        assertThat(libraryErrorLogList).hasSize(databaseSizeBeforeUpdate);
        LibraryErrorLog testLibraryErrorLog = libraryErrorLogList.get(libraryErrorLogList.size() - 1);
        assertThat(testLibraryErrorLog.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testLibraryErrorLog.getSeverity()).isEqualTo(UPDATED_SEVERITY);
        assertThat(testLibraryErrorLog.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testLibraryErrorLog.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void putNonExistingLibraryErrorLog() throws Exception {
        int databaseSizeBeforeUpdate = libraryErrorLogRepository.findAll().size();
        libraryErrorLog.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLibraryErrorLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, libraryErrorLog.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(libraryErrorLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the LibraryErrorLog in the database
        List<LibraryErrorLog> libraryErrorLogList = libraryErrorLogRepository.findAll();
        assertThat(libraryErrorLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLibraryErrorLog() throws Exception {
        int databaseSizeBeforeUpdate = libraryErrorLogRepository.findAll().size();
        libraryErrorLog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryErrorLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(libraryErrorLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the LibraryErrorLog in the database
        List<LibraryErrorLog> libraryErrorLogList = libraryErrorLogRepository.findAll();
        assertThat(libraryErrorLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLibraryErrorLog() throws Exception {
        int databaseSizeBeforeUpdate = libraryErrorLogRepository.findAll().size();
        libraryErrorLog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryErrorLogMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(libraryErrorLog))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LibraryErrorLog in the database
        List<LibraryErrorLog> libraryErrorLogList = libraryErrorLogRepository.findAll();
        assertThat(libraryErrorLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLibraryErrorLogWithPatch() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        int databaseSizeBeforeUpdate = libraryErrorLogRepository.findAll().size();

        // Update the libraryErrorLog using partial update
        LibraryErrorLog partialUpdatedLibraryErrorLog = new LibraryErrorLog();
        partialUpdatedLibraryErrorLog.setId(libraryErrorLog.getId());

        partialUpdatedLibraryErrorLog.message(UPDATED_MESSAGE).status(UPDATED_STATUS).timestamp(UPDATED_TIMESTAMP);

        restLibraryErrorLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLibraryErrorLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLibraryErrorLog))
            )
            .andExpect(status().isOk());

        // Validate the LibraryErrorLog in the database
        List<LibraryErrorLog> libraryErrorLogList = libraryErrorLogRepository.findAll();
        assertThat(libraryErrorLogList).hasSize(databaseSizeBeforeUpdate);
        LibraryErrorLog testLibraryErrorLog = libraryErrorLogList.get(libraryErrorLogList.size() - 1);
        assertThat(testLibraryErrorLog.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testLibraryErrorLog.getSeverity()).isEqualTo(DEFAULT_SEVERITY);
        assertThat(testLibraryErrorLog.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testLibraryErrorLog.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void fullUpdateLibraryErrorLogWithPatch() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        int databaseSizeBeforeUpdate = libraryErrorLogRepository.findAll().size();

        // Update the libraryErrorLog using partial update
        LibraryErrorLog partialUpdatedLibraryErrorLog = new LibraryErrorLog();
        partialUpdatedLibraryErrorLog.setId(libraryErrorLog.getId());

        partialUpdatedLibraryErrorLog
            .message(UPDATED_MESSAGE)
            .severity(UPDATED_SEVERITY)
            .status(UPDATED_STATUS)
            .timestamp(UPDATED_TIMESTAMP);

        restLibraryErrorLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLibraryErrorLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLibraryErrorLog))
            )
            .andExpect(status().isOk());

        // Validate the LibraryErrorLog in the database
        List<LibraryErrorLog> libraryErrorLogList = libraryErrorLogRepository.findAll();
        assertThat(libraryErrorLogList).hasSize(databaseSizeBeforeUpdate);
        LibraryErrorLog testLibraryErrorLog = libraryErrorLogList.get(libraryErrorLogList.size() - 1);
        assertThat(testLibraryErrorLog.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testLibraryErrorLog.getSeverity()).isEqualTo(UPDATED_SEVERITY);
        assertThat(testLibraryErrorLog.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testLibraryErrorLog.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void patchNonExistingLibraryErrorLog() throws Exception {
        int databaseSizeBeforeUpdate = libraryErrorLogRepository.findAll().size();
        libraryErrorLog.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLibraryErrorLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, libraryErrorLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(libraryErrorLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the LibraryErrorLog in the database
        List<LibraryErrorLog> libraryErrorLogList = libraryErrorLogRepository.findAll();
        assertThat(libraryErrorLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLibraryErrorLog() throws Exception {
        int databaseSizeBeforeUpdate = libraryErrorLogRepository.findAll().size();
        libraryErrorLog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryErrorLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(libraryErrorLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the LibraryErrorLog in the database
        List<LibraryErrorLog> libraryErrorLogList = libraryErrorLogRepository.findAll();
        assertThat(libraryErrorLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLibraryErrorLog() throws Exception {
        int databaseSizeBeforeUpdate = libraryErrorLogRepository.findAll().size();
        libraryErrorLog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryErrorLogMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(libraryErrorLog))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LibraryErrorLog in the database
        List<LibraryErrorLog> libraryErrorLogList = libraryErrorLogRepository.findAll();
        assertThat(libraryErrorLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLibraryErrorLog() throws Exception {
        // Initialize the database
        libraryErrorLogRepository.saveAndFlush(libraryErrorLog);

        int databaseSizeBeforeDelete = libraryErrorLogRepository.findAll().size();

        // Delete the libraryErrorLog
        restLibraryErrorLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, libraryErrorLog.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LibraryErrorLog> libraryErrorLogList = libraryErrorLogRepository.findAll();
        assertThat(libraryErrorLogList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
