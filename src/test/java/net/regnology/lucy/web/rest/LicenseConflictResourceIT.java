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
import net.regnology.lucy.domain.LicenseConflict;
import net.regnology.lucy.domain.enumeration.CompatibilityState;
import net.regnology.lucy.repository.LicenseConflictRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link LicenseConflictResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LicenseConflictResourceIT {

    private static final CompatibilityState DEFAULT_COMPATIBILITY = CompatibilityState.Compatible;
    private static final CompatibilityState UPDATED_COMPATIBILITY = CompatibilityState.Incompatible;

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/license-conflicts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LicenseConflictRepository licenseConflictRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLicenseConflictMockMvc;

    private LicenseConflict licenseConflict;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LicenseConflict createEntity(EntityManager em) {
        LicenseConflict licenseConflict = new LicenseConflict().compatibility(DEFAULT_COMPATIBILITY).comment(DEFAULT_COMMENT);
        return licenseConflict;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LicenseConflict createUpdatedEntity(EntityManager em) {
        LicenseConflict licenseConflict = new LicenseConflict().compatibility(UPDATED_COMPATIBILITY).comment(UPDATED_COMMENT);
        return licenseConflict;
    }

    @BeforeEach
    public void initTest() {
        licenseConflict = createEntity(em);
    }

    @Test
    @Transactional
    void createLicenseConflict() throws Exception {
        int databaseSizeBeforeCreate = licenseConflictRepository.findAll().size();
        // Create the LicenseConflict
        restLicenseConflictMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(licenseConflict))
            )
            .andExpect(status().isCreated());

        // Validate the LicenseConflict in the database
        List<LicenseConflict> licenseConflictList = licenseConflictRepository.findAll();
        assertThat(licenseConflictList).hasSize(databaseSizeBeforeCreate + 1);
        LicenseConflict testLicenseConflict = licenseConflictList.get(licenseConflictList.size() - 1);
        assertThat(testLicenseConflict.getCompatibility()).isEqualTo(DEFAULT_COMPATIBILITY);
        assertThat(testLicenseConflict.getComment()).isEqualTo(DEFAULT_COMMENT);
    }

    @Test
    @Transactional
    void createLicenseConflictWithExistingId() throws Exception {
        // Create the LicenseConflict with an existing ID
        licenseConflict.setId(1L);

        int databaseSizeBeforeCreate = licenseConflictRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLicenseConflictMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(licenseConflict))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicenseConflict in the database
        List<LicenseConflict> licenseConflictList = licenseConflictRepository.findAll();
        assertThat(licenseConflictList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllLicenseConflicts() throws Exception {
        // Initialize the database
        licenseConflictRepository.saveAndFlush(licenseConflict);

        // Get all the licenseConflictList
        restLicenseConflictMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(licenseConflict.getId().intValue())))
            .andExpect(jsonPath("$.[*].compatibility").value(hasItem(DEFAULT_COMPATIBILITY.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)));
    }

    @Test
    @Transactional
    void getLicenseConflict() throws Exception {
        // Initialize the database
        licenseConflictRepository.saveAndFlush(licenseConflict);

        // Get the licenseConflict
        restLicenseConflictMockMvc
            .perform(get(ENTITY_API_URL_ID, licenseConflict.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(licenseConflict.getId().intValue()))
            .andExpect(jsonPath("$.compatibility").value(DEFAULT_COMPATIBILITY.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT));
    }

    @Test
    @Transactional
    void getNonExistingLicenseConflict() throws Exception {
        // Get the licenseConflict
        restLicenseConflictMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLicenseConflict() throws Exception {
        // Initialize the database
        licenseConflictRepository.saveAndFlush(licenseConflict);

        int databaseSizeBeforeUpdate = licenseConflictRepository.findAll().size();

        // Update the licenseConflict
        LicenseConflict updatedLicenseConflict = licenseConflictRepository.findById(licenseConflict.getId()).get();
        // Disconnect from session so that the updates on updatedLicenseConflict are not directly saved in db
        em.detach(updatedLicenseConflict);
        updatedLicenseConflict.compatibility(UPDATED_COMPATIBILITY).comment(UPDATED_COMMENT);

        restLicenseConflictMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLicenseConflict.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLicenseConflict))
            )
            .andExpect(status().isOk());

        // Validate the LicenseConflict in the database
        List<LicenseConflict> licenseConflictList = licenseConflictRepository.findAll();
        assertThat(licenseConflictList).hasSize(databaseSizeBeforeUpdate);
        LicenseConflict testLicenseConflict = licenseConflictList.get(licenseConflictList.size() - 1);
        assertThat(testLicenseConflict.getCompatibility()).isEqualTo(UPDATED_COMPATIBILITY);
        assertThat(testLicenseConflict.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void putNonExistingLicenseConflict() throws Exception {
        int databaseSizeBeforeUpdate = licenseConflictRepository.findAll().size();
        licenseConflict.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLicenseConflictMockMvc
            .perform(
                put(ENTITY_API_URL_ID, licenseConflict.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(licenseConflict))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicenseConflict in the database
        List<LicenseConflict> licenseConflictList = licenseConflictRepository.findAll();
        assertThat(licenseConflictList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLicenseConflict() throws Exception {
        int databaseSizeBeforeUpdate = licenseConflictRepository.findAll().size();
        licenseConflict.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicenseConflictMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(licenseConflict))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicenseConflict in the database
        List<LicenseConflict> licenseConflictList = licenseConflictRepository.findAll();
        assertThat(licenseConflictList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLicenseConflict() throws Exception {
        int databaseSizeBeforeUpdate = licenseConflictRepository.findAll().size();
        licenseConflict.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicenseConflictMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(licenseConflict))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LicenseConflict in the database
        List<LicenseConflict> licenseConflictList = licenseConflictRepository.findAll();
        assertThat(licenseConflictList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLicenseConflictWithPatch() throws Exception {
        // Initialize the database
        licenseConflictRepository.saveAndFlush(licenseConflict);

        int databaseSizeBeforeUpdate = licenseConflictRepository.findAll().size();

        // Update the licenseConflict using partial update
        LicenseConflict partialUpdatedLicenseConflict = new LicenseConflict();
        partialUpdatedLicenseConflict.setId(licenseConflict.getId());

        partialUpdatedLicenseConflict.compatibility(UPDATED_COMPATIBILITY).comment(UPDATED_COMMENT);

        restLicenseConflictMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLicenseConflict.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLicenseConflict))
            )
            .andExpect(status().isOk());

        // Validate the LicenseConflict in the database
        List<LicenseConflict> licenseConflictList = licenseConflictRepository.findAll();
        assertThat(licenseConflictList).hasSize(databaseSizeBeforeUpdate);
        LicenseConflict testLicenseConflict = licenseConflictList.get(licenseConflictList.size() - 1);
        assertThat(testLicenseConflict.getCompatibility()).isEqualTo(UPDATED_COMPATIBILITY);
        assertThat(testLicenseConflict.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void fullUpdateLicenseConflictWithPatch() throws Exception {
        // Initialize the database
        licenseConflictRepository.saveAndFlush(licenseConflict);

        int databaseSizeBeforeUpdate = licenseConflictRepository.findAll().size();

        // Update the licenseConflict using partial update
        LicenseConflict partialUpdatedLicenseConflict = new LicenseConflict();
        partialUpdatedLicenseConflict.setId(licenseConflict.getId());

        partialUpdatedLicenseConflict.compatibility(UPDATED_COMPATIBILITY).comment(UPDATED_COMMENT);

        restLicenseConflictMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLicenseConflict.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLicenseConflict))
            )
            .andExpect(status().isOk());

        // Validate the LicenseConflict in the database
        List<LicenseConflict> licenseConflictList = licenseConflictRepository.findAll();
        assertThat(licenseConflictList).hasSize(databaseSizeBeforeUpdate);
        LicenseConflict testLicenseConflict = licenseConflictList.get(licenseConflictList.size() - 1);
        assertThat(testLicenseConflict.getCompatibility()).isEqualTo(UPDATED_COMPATIBILITY);
        assertThat(testLicenseConflict.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void patchNonExistingLicenseConflict() throws Exception {
        int databaseSizeBeforeUpdate = licenseConflictRepository.findAll().size();
        licenseConflict.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLicenseConflictMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, licenseConflict.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(licenseConflict))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicenseConflict in the database
        List<LicenseConflict> licenseConflictList = licenseConflictRepository.findAll();
        assertThat(licenseConflictList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLicenseConflict() throws Exception {
        int databaseSizeBeforeUpdate = licenseConflictRepository.findAll().size();
        licenseConflict.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicenseConflictMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(licenseConflict))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicenseConflict in the database
        List<LicenseConflict> licenseConflictList = licenseConflictRepository.findAll();
        assertThat(licenseConflictList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLicenseConflict() throws Exception {
        int databaseSizeBeforeUpdate = licenseConflictRepository.findAll().size();
        licenseConflict.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicenseConflictMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(licenseConflict))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LicenseConflict in the database
        List<LicenseConflict> licenseConflictList = licenseConflictRepository.findAll();
        assertThat(licenseConflictList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLicenseConflict() throws Exception {
        // Initialize the database
        licenseConflictRepository.saveAndFlush(licenseConflict);

        int databaseSizeBeforeDelete = licenseConflictRepository.findAll().size();

        // Delete the licenseConflict
        restLicenseConflictMockMvc
            .perform(delete(ENTITY_API_URL_ID, licenseConflict.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LicenseConflict> licenseConflictList = licenseConflictRepository.findAll();
        assertThat(licenseConflictList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
