package net.regnology.lucy.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import net.regnology.lucy.IntegrationTest;
import net.regnology.lucy.domain.LicenseNamingMapping;
import net.regnology.lucy.repository.LicenseNamingMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link LicenseNamingMappingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LicenseNamingMappingResourceIT {

    private static final String DEFAULT_REGEX = "AAAAAAAAAA";
    private static final String UPDATED_REGEX = "BBBBBBBBBB";

    private static final String DEFAULT_UNIFORM_SHORT_IDENTIFIER = "AAAAAAAAAA";
    private static final String UPDATED_UNIFORM_SHORT_IDENTIFIER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/license-naming-mappings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LicenseNamingMappingRepository licenseNamingMappingRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLicenseNamingMappingMockMvc;

    private LicenseNamingMapping licenseNamingMapping;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LicenseNamingMapping createEntity(EntityManager em) {
        LicenseNamingMapping licenseNamingMapping = new LicenseNamingMapping()
            .regex(DEFAULT_REGEX)
            .uniformShortIdentifier(DEFAULT_UNIFORM_SHORT_IDENTIFIER);
        return licenseNamingMapping;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LicenseNamingMapping createUpdatedEntity(EntityManager em) {
        LicenseNamingMapping licenseNamingMapping = new LicenseNamingMapping()
            .regex(UPDATED_REGEX)
            .uniformShortIdentifier(UPDATED_UNIFORM_SHORT_IDENTIFIER);
        return licenseNamingMapping;
    }

    @BeforeEach
    public void initTest() {
        licenseNamingMapping = createEntity(em);
    }

    @Test
    @Transactional
    void createLicenseNamingMapping() throws Exception {
        int databaseSizeBeforeCreate = licenseNamingMappingRepository.findAll().size();
        // Create the LicenseNamingMapping
        restLicenseNamingMappingMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(licenseNamingMapping))
            )
            .andExpect(status().isCreated());

        // Validate the LicenseNamingMapping in the database
        List<LicenseNamingMapping> licenseNamingMappingList = licenseNamingMappingRepository.findAll();
        assertThat(licenseNamingMappingList).hasSize(databaseSizeBeforeCreate + 1);
        LicenseNamingMapping testLicenseNamingMapping = licenseNamingMappingList.get(licenseNamingMappingList.size() - 1);
        assertThat(testLicenseNamingMapping.getRegex()).isEqualTo(DEFAULT_REGEX);
        assertThat(testLicenseNamingMapping.getUniformShortIdentifier()).isEqualTo(DEFAULT_UNIFORM_SHORT_IDENTIFIER);
    }

    @Test
    @Transactional
    void createLicenseNamingMappingWithExistingId() throws Exception {
        // Create the LicenseNamingMapping with an existing ID
        licenseNamingMapping.setId(1L);

        int databaseSizeBeforeCreate = licenseNamingMappingRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLicenseNamingMappingMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(licenseNamingMapping))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicenseNamingMapping in the database
        List<LicenseNamingMapping> licenseNamingMappingList = licenseNamingMappingRepository.findAll();
        assertThat(licenseNamingMappingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRegexIsRequired() throws Exception {
        int databaseSizeBeforeTest = licenseNamingMappingRepository.findAll().size();
        // set the field null
        licenseNamingMapping.setRegex(null);

        // Create the LicenseNamingMapping, which fails.

        restLicenseNamingMappingMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(licenseNamingMapping))
            )
            .andExpect(status().isBadRequest());

        List<LicenseNamingMapping> licenseNamingMappingList = licenseNamingMappingRepository.findAll();
        assertThat(licenseNamingMappingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLicenseNamingMappings() throws Exception {
        // Initialize the database
        licenseNamingMappingRepository.saveAndFlush(licenseNamingMapping);

        // Get all the licenseNamingMappingList
        restLicenseNamingMappingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(licenseNamingMapping.getId().intValue())))
            .andExpect(jsonPath("$.[*].regex").value(hasItem(DEFAULT_REGEX)))
            .andExpect(jsonPath("$.[*].uniformShortIdentifier").value(hasItem(DEFAULT_UNIFORM_SHORT_IDENTIFIER)));
    }

    @Test
    @Transactional
    void getLicenseNamingMapping() throws Exception {
        // Initialize the database
        licenseNamingMappingRepository.saveAndFlush(licenseNamingMapping);

        // Get the licenseNamingMapping
        restLicenseNamingMappingMockMvc
            .perform(get(ENTITY_API_URL_ID, licenseNamingMapping.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(licenseNamingMapping.getId().intValue()))
            .andExpect(jsonPath("$.regex").value(DEFAULT_REGEX))
            .andExpect(jsonPath("$.uniformShortIdentifier").value(DEFAULT_UNIFORM_SHORT_IDENTIFIER));
    }

    @Test
    @Transactional
    void getNonExistingLicenseNamingMapping() throws Exception {
        // Get the licenseNamingMapping
        restLicenseNamingMappingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLicenseNamingMapping() throws Exception {
        // Initialize the database
        licenseNamingMappingRepository.saveAndFlush(licenseNamingMapping);

        int databaseSizeBeforeUpdate = licenseNamingMappingRepository.findAll().size();

        // Update the licenseNamingMapping
        LicenseNamingMapping updatedLicenseNamingMapping = licenseNamingMappingRepository.findById(licenseNamingMapping.getId()).get();
        // Disconnect from session so that the updates on updatedLicenseNamingMapping are not directly saved in db
        em.detach(updatedLicenseNamingMapping);
        updatedLicenseNamingMapping.regex(UPDATED_REGEX).uniformShortIdentifier(UPDATED_UNIFORM_SHORT_IDENTIFIER);

        restLicenseNamingMappingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLicenseNamingMapping.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLicenseNamingMapping))
            )
            .andExpect(status().isOk());

        // Validate the LicenseNamingMapping in the database
        List<LicenseNamingMapping> licenseNamingMappingList = licenseNamingMappingRepository.findAll();
        assertThat(licenseNamingMappingList).hasSize(databaseSizeBeforeUpdate);
        LicenseNamingMapping testLicenseNamingMapping = licenseNamingMappingList.get(licenseNamingMappingList.size() - 1);
        assertThat(testLicenseNamingMapping.getRegex()).isEqualTo(UPDATED_REGEX);
        assertThat(testLicenseNamingMapping.getUniformShortIdentifier()).isEqualTo(UPDATED_UNIFORM_SHORT_IDENTIFIER);
    }

    @Test
    @Transactional
    void putNonExistingLicenseNamingMapping() throws Exception {
        int databaseSizeBeforeUpdate = licenseNamingMappingRepository.findAll().size();
        licenseNamingMapping.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLicenseNamingMappingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, licenseNamingMapping.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(licenseNamingMapping))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicenseNamingMapping in the database
        List<LicenseNamingMapping> licenseNamingMappingList = licenseNamingMappingRepository.findAll();
        assertThat(licenseNamingMappingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLicenseNamingMapping() throws Exception {
        int databaseSizeBeforeUpdate = licenseNamingMappingRepository.findAll().size();
        licenseNamingMapping.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicenseNamingMappingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(licenseNamingMapping))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicenseNamingMapping in the database
        List<LicenseNamingMapping> licenseNamingMappingList = licenseNamingMappingRepository.findAll();
        assertThat(licenseNamingMappingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLicenseNamingMapping() throws Exception {
        int databaseSizeBeforeUpdate = licenseNamingMappingRepository.findAll().size();
        licenseNamingMapping.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicenseNamingMappingMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(licenseNamingMapping))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LicenseNamingMapping in the database
        List<LicenseNamingMapping> licenseNamingMappingList = licenseNamingMappingRepository.findAll();
        assertThat(licenseNamingMappingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLicenseNamingMappingWithPatch() throws Exception {
        // Initialize the database
        licenseNamingMappingRepository.saveAndFlush(licenseNamingMapping);

        int databaseSizeBeforeUpdate = licenseNamingMappingRepository.findAll().size();

        // Update the licenseNamingMapping using partial update
        LicenseNamingMapping partialUpdatedLicenseNamingMapping = new LicenseNamingMapping();
        partialUpdatedLicenseNamingMapping.setId(licenseNamingMapping.getId());

        partialUpdatedLicenseNamingMapping.regex(UPDATED_REGEX).uniformShortIdentifier(UPDATED_UNIFORM_SHORT_IDENTIFIER);

        restLicenseNamingMappingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLicenseNamingMapping.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLicenseNamingMapping))
            )
            .andExpect(status().isOk());

        // Validate the LicenseNamingMapping in the database
        List<LicenseNamingMapping> licenseNamingMappingList = licenseNamingMappingRepository.findAll();
        assertThat(licenseNamingMappingList).hasSize(databaseSizeBeforeUpdate);
        LicenseNamingMapping testLicenseNamingMapping = licenseNamingMappingList.get(licenseNamingMappingList.size() - 1);
        assertThat(testLicenseNamingMapping.getRegex()).isEqualTo(UPDATED_REGEX);
        assertThat(testLicenseNamingMapping.getUniformShortIdentifier()).isEqualTo(UPDATED_UNIFORM_SHORT_IDENTIFIER);
    }

    @Test
    @Transactional
    void fullUpdateLicenseNamingMappingWithPatch() throws Exception {
        // Initialize the database
        licenseNamingMappingRepository.saveAndFlush(licenseNamingMapping);

        int databaseSizeBeforeUpdate = licenseNamingMappingRepository.findAll().size();

        // Update the licenseNamingMapping using partial update
        LicenseNamingMapping partialUpdatedLicenseNamingMapping = new LicenseNamingMapping();
        partialUpdatedLicenseNamingMapping.setId(licenseNamingMapping.getId());

        partialUpdatedLicenseNamingMapping.regex(UPDATED_REGEX).uniformShortIdentifier(UPDATED_UNIFORM_SHORT_IDENTIFIER);

        restLicenseNamingMappingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLicenseNamingMapping.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLicenseNamingMapping))
            )
            .andExpect(status().isOk());

        // Validate the LicenseNamingMapping in the database
        List<LicenseNamingMapping> licenseNamingMappingList = licenseNamingMappingRepository.findAll();
        assertThat(licenseNamingMappingList).hasSize(databaseSizeBeforeUpdate);
        LicenseNamingMapping testLicenseNamingMapping = licenseNamingMappingList.get(licenseNamingMappingList.size() - 1);
        assertThat(testLicenseNamingMapping.getRegex()).isEqualTo(UPDATED_REGEX);
        assertThat(testLicenseNamingMapping.getUniformShortIdentifier()).isEqualTo(UPDATED_UNIFORM_SHORT_IDENTIFIER);
    }

    @Test
    @Transactional
    void patchNonExistingLicenseNamingMapping() throws Exception {
        int databaseSizeBeforeUpdate = licenseNamingMappingRepository.findAll().size();
        licenseNamingMapping.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLicenseNamingMappingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, licenseNamingMapping.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(licenseNamingMapping))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicenseNamingMapping in the database
        List<LicenseNamingMapping> licenseNamingMappingList = licenseNamingMappingRepository.findAll();
        assertThat(licenseNamingMappingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLicenseNamingMapping() throws Exception {
        int databaseSizeBeforeUpdate = licenseNamingMappingRepository.findAll().size();
        licenseNamingMapping.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicenseNamingMappingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(licenseNamingMapping))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicenseNamingMapping in the database
        List<LicenseNamingMapping> licenseNamingMappingList = licenseNamingMappingRepository.findAll();
        assertThat(licenseNamingMappingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLicenseNamingMapping() throws Exception {
        int databaseSizeBeforeUpdate = licenseNamingMappingRepository.findAll().size();
        licenseNamingMapping.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicenseNamingMappingMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(licenseNamingMapping))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LicenseNamingMapping in the database
        List<LicenseNamingMapping> licenseNamingMappingList = licenseNamingMappingRepository.findAll();
        assertThat(licenseNamingMappingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLicenseNamingMapping() throws Exception {
        // Initialize the database
        licenseNamingMappingRepository.saveAndFlush(licenseNamingMapping);

        int databaseSizeBeforeDelete = licenseNamingMappingRepository.findAll().size();

        // Delete the licenseNamingMapping
        restLicenseNamingMappingMockMvc
            .perform(delete(ENTITY_API_URL_ID, licenseNamingMapping.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LicenseNamingMapping> licenseNamingMappingList = licenseNamingMappingRepository.findAll();
        assertThat(licenseNamingMappingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
