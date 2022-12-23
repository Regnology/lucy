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
import net.regnology.lucy.domain.LicenseRisk;
import net.regnology.lucy.repository.LicenseRiskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link LicenseRiskResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LicenseRiskResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_LEVEL = 1;
    private static final Integer UPDATED_LEVEL = 2;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_COLOR = "AAAAAAAAAA";
    private static final String UPDATED_COLOR = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/license-risks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LicenseRiskRepository licenseRiskRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLicenseRiskMockMvc;

    private LicenseRisk licenseRisk;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LicenseRisk createEntity(EntityManager em) {
        LicenseRisk licenseRisk = new LicenseRisk()
            .name(DEFAULT_NAME)
            .level(DEFAULT_LEVEL)
            .description(DEFAULT_DESCRIPTION)
            .color(DEFAULT_COLOR);
        return licenseRisk;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LicenseRisk createUpdatedEntity(EntityManager em) {
        LicenseRisk licenseRisk = new LicenseRisk()
            .name(UPDATED_NAME)
            .level(UPDATED_LEVEL)
            .description(UPDATED_DESCRIPTION)
            .color(UPDATED_COLOR);
        return licenseRisk;
    }

    @BeforeEach
    public void initTest() {
        licenseRisk = createEntity(em);
    }

    @Test
    @Transactional
    void createLicenseRisk() throws Exception {
        int databaseSizeBeforeCreate = licenseRiskRepository.findAll().size();
        // Create the LicenseRisk
        restLicenseRiskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(licenseRisk)))
            .andExpect(status().isCreated());

        // Validate the LicenseRisk in the database
        List<LicenseRisk> licenseRiskList = licenseRiskRepository.findAll();
        assertThat(licenseRiskList).hasSize(databaseSizeBeforeCreate + 1);
        LicenseRisk testLicenseRisk = licenseRiskList.get(licenseRiskList.size() - 1);
        assertThat(testLicenseRisk.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLicenseRisk.getLevel()).isEqualTo(DEFAULT_LEVEL);
        assertThat(testLicenseRisk.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testLicenseRisk.getColor()).isEqualTo(DEFAULT_COLOR);
    }

    @Test
    @Transactional
    void createLicenseRiskWithExistingId() throws Exception {
        // Create the LicenseRisk with an existing ID
        licenseRisk.setId(1L);

        int databaseSizeBeforeCreate = licenseRiskRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLicenseRiskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(licenseRisk)))
            .andExpect(status().isBadRequest());

        // Validate the LicenseRisk in the database
        List<LicenseRisk> licenseRiskList = licenseRiskRepository.findAll();
        assertThat(licenseRiskList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = licenseRiskRepository.findAll().size();
        // set the field null
        licenseRisk.setName(null);

        // Create the LicenseRisk, which fails.

        restLicenseRiskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(licenseRisk)))
            .andExpect(status().isBadRequest());

        List<LicenseRisk> licenseRiskList = licenseRiskRepository.findAll();
        assertThat(licenseRiskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLevelIsRequired() throws Exception {
        int databaseSizeBeforeTest = licenseRiskRepository.findAll().size();
        // set the field null
        licenseRisk.setLevel(null);

        // Create the LicenseRisk, which fails.

        restLicenseRiskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(licenseRisk)))
            .andExpect(status().isBadRequest());

        List<LicenseRisk> licenseRiskList = licenseRiskRepository.findAll();
        assertThat(licenseRiskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLicenseRisks() throws Exception {
        // Initialize the database
        licenseRiskRepository.saveAndFlush(licenseRisk);

        // Get all the licenseRiskList
        restLicenseRiskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(licenseRisk.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR)));
    }

    @Test
    @Transactional
    void getLicenseRisk() throws Exception {
        // Initialize the database
        licenseRiskRepository.saveAndFlush(licenseRisk);

        // Get the licenseRisk
        restLicenseRiskMockMvc
            .perform(get(ENTITY_API_URL_ID, licenseRisk.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(licenseRisk.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.level").value(DEFAULT_LEVEL))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.color").value(DEFAULT_COLOR));
    }

    @Test
    @Transactional
    void getNonExistingLicenseRisk() throws Exception {
        // Get the licenseRisk
        restLicenseRiskMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLicenseRisk() throws Exception {
        // Initialize the database
        licenseRiskRepository.saveAndFlush(licenseRisk);

        int databaseSizeBeforeUpdate = licenseRiskRepository.findAll().size();

        // Update the licenseRisk
        LicenseRisk updatedLicenseRisk = licenseRiskRepository.findById(licenseRisk.getId()).get();
        // Disconnect from session so that the updates on updatedLicenseRisk are not directly saved in db
        em.detach(updatedLicenseRisk);
        updatedLicenseRisk.name(UPDATED_NAME).level(UPDATED_LEVEL).description(UPDATED_DESCRIPTION).color(UPDATED_COLOR);

        restLicenseRiskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLicenseRisk.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLicenseRisk))
            )
            .andExpect(status().isOk());

        // Validate the LicenseRisk in the database
        List<LicenseRisk> licenseRiskList = licenseRiskRepository.findAll();
        assertThat(licenseRiskList).hasSize(databaseSizeBeforeUpdate);
        LicenseRisk testLicenseRisk = licenseRiskList.get(licenseRiskList.size() - 1);
        assertThat(testLicenseRisk.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLicenseRisk.getLevel()).isEqualTo(UPDATED_LEVEL);
        assertThat(testLicenseRisk.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testLicenseRisk.getColor()).isEqualTo(UPDATED_COLOR);
    }

    @Test
    @Transactional
    void putNonExistingLicenseRisk() throws Exception {
        int databaseSizeBeforeUpdate = licenseRiskRepository.findAll().size();
        licenseRisk.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLicenseRiskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, licenseRisk.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(licenseRisk))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicenseRisk in the database
        List<LicenseRisk> licenseRiskList = licenseRiskRepository.findAll();
        assertThat(licenseRiskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLicenseRisk() throws Exception {
        int databaseSizeBeforeUpdate = licenseRiskRepository.findAll().size();
        licenseRisk.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicenseRiskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(licenseRisk))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicenseRisk in the database
        List<LicenseRisk> licenseRiskList = licenseRiskRepository.findAll();
        assertThat(licenseRiskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLicenseRisk() throws Exception {
        int databaseSizeBeforeUpdate = licenseRiskRepository.findAll().size();
        licenseRisk.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicenseRiskMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(licenseRisk)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LicenseRisk in the database
        List<LicenseRisk> licenseRiskList = licenseRiskRepository.findAll();
        assertThat(licenseRiskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLicenseRiskWithPatch() throws Exception {
        // Initialize the database
        licenseRiskRepository.saveAndFlush(licenseRisk);

        int databaseSizeBeforeUpdate = licenseRiskRepository.findAll().size();

        // Update the licenseRisk using partial update
        LicenseRisk partialUpdatedLicenseRisk = new LicenseRisk();
        partialUpdatedLicenseRisk.setId(licenseRisk.getId());

        partialUpdatedLicenseRisk.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).color(UPDATED_COLOR);

        restLicenseRiskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLicenseRisk.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLicenseRisk))
            )
            .andExpect(status().isOk());

        // Validate the LicenseRisk in the database
        List<LicenseRisk> licenseRiskList = licenseRiskRepository.findAll();
        assertThat(licenseRiskList).hasSize(databaseSizeBeforeUpdate);
        LicenseRisk testLicenseRisk = licenseRiskList.get(licenseRiskList.size() - 1);
        assertThat(testLicenseRisk.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLicenseRisk.getLevel()).isEqualTo(DEFAULT_LEVEL);
        assertThat(testLicenseRisk.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testLicenseRisk.getColor()).isEqualTo(UPDATED_COLOR);
    }

    @Test
    @Transactional
    void fullUpdateLicenseRiskWithPatch() throws Exception {
        // Initialize the database
        licenseRiskRepository.saveAndFlush(licenseRisk);

        int databaseSizeBeforeUpdate = licenseRiskRepository.findAll().size();

        // Update the licenseRisk using partial update
        LicenseRisk partialUpdatedLicenseRisk = new LicenseRisk();
        partialUpdatedLicenseRisk.setId(licenseRisk.getId());

        partialUpdatedLicenseRisk.name(UPDATED_NAME).level(UPDATED_LEVEL).description(UPDATED_DESCRIPTION).color(UPDATED_COLOR);

        restLicenseRiskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLicenseRisk.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLicenseRisk))
            )
            .andExpect(status().isOk());

        // Validate the LicenseRisk in the database
        List<LicenseRisk> licenseRiskList = licenseRiskRepository.findAll();
        assertThat(licenseRiskList).hasSize(databaseSizeBeforeUpdate);
        LicenseRisk testLicenseRisk = licenseRiskList.get(licenseRiskList.size() - 1);
        assertThat(testLicenseRisk.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLicenseRisk.getLevel()).isEqualTo(UPDATED_LEVEL);
        assertThat(testLicenseRisk.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testLicenseRisk.getColor()).isEqualTo(UPDATED_COLOR);
    }

    @Test
    @Transactional
    void patchNonExistingLicenseRisk() throws Exception {
        int databaseSizeBeforeUpdate = licenseRiskRepository.findAll().size();
        licenseRisk.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLicenseRiskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, licenseRisk.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(licenseRisk))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicenseRisk in the database
        List<LicenseRisk> licenseRiskList = licenseRiskRepository.findAll();
        assertThat(licenseRiskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLicenseRisk() throws Exception {
        int databaseSizeBeforeUpdate = licenseRiskRepository.findAll().size();
        licenseRisk.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicenseRiskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(licenseRisk))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicenseRisk in the database
        List<LicenseRisk> licenseRiskList = licenseRiskRepository.findAll();
        assertThat(licenseRiskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLicenseRisk() throws Exception {
        int databaseSizeBeforeUpdate = licenseRiskRepository.findAll().size();
        licenseRisk.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicenseRiskMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(licenseRisk))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LicenseRisk in the database
        List<LicenseRisk> licenseRiskList = licenseRiskRepository.findAll();
        assertThat(licenseRiskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLicenseRisk() throws Exception {
        // Initialize the database
        licenseRiskRepository.saveAndFlush(licenseRisk);

        int databaseSizeBeforeDelete = licenseRiskRepository.findAll().size();

        // Delete the licenseRisk
        restLicenseRiskMockMvc
            .perform(delete(ENTITY_API_URL_ID, licenseRisk.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LicenseRisk> licenseRiskList = licenseRiskRepository.findAll();
        assertThat(licenseRiskList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
