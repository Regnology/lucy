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
import net.regnology.lucy.domain.GenericLicenseUrl;
import net.regnology.lucy.repository.GenericLicenseUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link GenericLicenseUrlResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GenericLicenseUrlResourceIT {

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/generic-license-urls";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GenericLicenseUrlRepository genericLicenseUrlRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGenericLicenseUrlMockMvc;

    private GenericLicenseUrl genericLicenseUrl;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GenericLicenseUrl createEntity(EntityManager em) {
        GenericLicenseUrl genericLicenseUrl = new GenericLicenseUrl().url(DEFAULT_URL);
        return genericLicenseUrl;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GenericLicenseUrl createUpdatedEntity(EntityManager em) {
        GenericLicenseUrl genericLicenseUrl = new GenericLicenseUrl().url(UPDATED_URL);
        return genericLicenseUrl;
    }

    @BeforeEach
    public void initTest() {
        genericLicenseUrl = createEntity(em);
    }

    @Test
    @Transactional
    void createGenericLicenseUrl() throws Exception {
        int databaseSizeBeforeCreate = genericLicenseUrlRepository.findAll().size();
        // Create the GenericLicenseUrl
        restGenericLicenseUrlMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(genericLicenseUrl))
            )
            .andExpect(status().isCreated());

        // Validate the GenericLicenseUrl in the database
        List<GenericLicenseUrl> genericLicenseUrlList = genericLicenseUrlRepository.findAll();
        assertThat(genericLicenseUrlList).hasSize(databaseSizeBeforeCreate + 1);
        GenericLicenseUrl testGenericLicenseUrl = genericLicenseUrlList.get(genericLicenseUrlList.size() - 1);
        assertThat(testGenericLicenseUrl.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    @Transactional
    void createGenericLicenseUrlWithExistingId() throws Exception {
        // Create the GenericLicenseUrl with an existing ID
        genericLicenseUrl.setId(1L);

        int databaseSizeBeforeCreate = genericLicenseUrlRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGenericLicenseUrlMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(genericLicenseUrl))
            )
            .andExpect(status().isBadRequest());

        // Validate the GenericLicenseUrl in the database
        List<GenericLicenseUrl> genericLicenseUrlList = genericLicenseUrlRepository.findAll();
        assertThat(genericLicenseUrlList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = genericLicenseUrlRepository.findAll().size();
        // set the field null
        genericLicenseUrl.setUrl(null);

        // Create the GenericLicenseUrl, which fails.

        restGenericLicenseUrlMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(genericLicenseUrl))
            )
            .andExpect(status().isBadRequest());

        List<GenericLicenseUrl> genericLicenseUrlList = genericLicenseUrlRepository.findAll();
        assertThat(genericLicenseUrlList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGenericLicenseUrls() throws Exception {
        // Initialize the database
        genericLicenseUrlRepository.saveAndFlush(genericLicenseUrl);

        // Get all the genericLicenseUrlList
        restGenericLicenseUrlMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(genericLicenseUrl.getId().intValue())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)));
    }

    @Test
    @Transactional
    void getGenericLicenseUrl() throws Exception {
        // Initialize the database
        genericLicenseUrlRepository.saveAndFlush(genericLicenseUrl);

        // Get the genericLicenseUrl
        restGenericLicenseUrlMockMvc
            .perform(get(ENTITY_API_URL_ID, genericLicenseUrl.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(genericLicenseUrl.getId().intValue()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL));
    }

    @Test
    @Transactional
    void getNonExistingGenericLicenseUrl() throws Exception {
        // Get the genericLicenseUrl
        restGenericLicenseUrlMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewGenericLicenseUrl() throws Exception {
        // Initialize the database
        genericLicenseUrlRepository.saveAndFlush(genericLicenseUrl);

        int databaseSizeBeforeUpdate = genericLicenseUrlRepository.findAll().size();

        // Update the genericLicenseUrl
        GenericLicenseUrl updatedGenericLicenseUrl = genericLicenseUrlRepository.findById(genericLicenseUrl.getId()).get();
        // Disconnect from session so that the updates on updatedGenericLicenseUrl are not directly saved in db
        em.detach(updatedGenericLicenseUrl);
        updatedGenericLicenseUrl.url(UPDATED_URL);

        restGenericLicenseUrlMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGenericLicenseUrl.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedGenericLicenseUrl))
            )
            .andExpect(status().isOk());

        // Validate the GenericLicenseUrl in the database
        List<GenericLicenseUrl> genericLicenseUrlList = genericLicenseUrlRepository.findAll();
        assertThat(genericLicenseUrlList).hasSize(databaseSizeBeforeUpdate);
        GenericLicenseUrl testGenericLicenseUrl = genericLicenseUrlList.get(genericLicenseUrlList.size() - 1);
        assertThat(testGenericLicenseUrl.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    void putNonExistingGenericLicenseUrl() throws Exception {
        int databaseSizeBeforeUpdate = genericLicenseUrlRepository.findAll().size();
        genericLicenseUrl.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGenericLicenseUrlMockMvc
            .perform(
                put(ENTITY_API_URL_ID, genericLicenseUrl.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(genericLicenseUrl))
            )
            .andExpect(status().isBadRequest());

        // Validate the GenericLicenseUrl in the database
        List<GenericLicenseUrl> genericLicenseUrlList = genericLicenseUrlRepository.findAll();
        assertThat(genericLicenseUrlList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGenericLicenseUrl() throws Exception {
        int databaseSizeBeforeUpdate = genericLicenseUrlRepository.findAll().size();
        genericLicenseUrl.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenericLicenseUrlMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(genericLicenseUrl))
            )
            .andExpect(status().isBadRequest());

        // Validate the GenericLicenseUrl in the database
        List<GenericLicenseUrl> genericLicenseUrlList = genericLicenseUrlRepository.findAll();
        assertThat(genericLicenseUrlList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGenericLicenseUrl() throws Exception {
        int databaseSizeBeforeUpdate = genericLicenseUrlRepository.findAll().size();
        genericLicenseUrl.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenericLicenseUrlMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(genericLicenseUrl))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GenericLicenseUrl in the database
        List<GenericLicenseUrl> genericLicenseUrlList = genericLicenseUrlRepository.findAll();
        assertThat(genericLicenseUrlList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGenericLicenseUrlWithPatch() throws Exception {
        // Initialize the database
        genericLicenseUrlRepository.saveAndFlush(genericLicenseUrl);

        int databaseSizeBeforeUpdate = genericLicenseUrlRepository.findAll().size();

        // Update the genericLicenseUrl using partial update
        GenericLicenseUrl partialUpdatedGenericLicenseUrl = new GenericLicenseUrl();
        partialUpdatedGenericLicenseUrl.setId(genericLicenseUrl.getId());

        partialUpdatedGenericLicenseUrl.url(UPDATED_URL);

        restGenericLicenseUrlMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGenericLicenseUrl.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGenericLicenseUrl))
            )
            .andExpect(status().isOk());

        // Validate the GenericLicenseUrl in the database
        List<GenericLicenseUrl> genericLicenseUrlList = genericLicenseUrlRepository.findAll();
        assertThat(genericLicenseUrlList).hasSize(databaseSizeBeforeUpdate);
        GenericLicenseUrl testGenericLicenseUrl = genericLicenseUrlList.get(genericLicenseUrlList.size() - 1);
        assertThat(testGenericLicenseUrl.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    void fullUpdateGenericLicenseUrlWithPatch() throws Exception {
        // Initialize the database
        genericLicenseUrlRepository.saveAndFlush(genericLicenseUrl);

        int databaseSizeBeforeUpdate = genericLicenseUrlRepository.findAll().size();

        // Update the genericLicenseUrl using partial update
        GenericLicenseUrl partialUpdatedGenericLicenseUrl = new GenericLicenseUrl();
        partialUpdatedGenericLicenseUrl.setId(genericLicenseUrl.getId());

        partialUpdatedGenericLicenseUrl.url(UPDATED_URL);

        restGenericLicenseUrlMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGenericLicenseUrl.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGenericLicenseUrl))
            )
            .andExpect(status().isOk());

        // Validate the GenericLicenseUrl in the database
        List<GenericLicenseUrl> genericLicenseUrlList = genericLicenseUrlRepository.findAll();
        assertThat(genericLicenseUrlList).hasSize(databaseSizeBeforeUpdate);
        GenericLicenseUrl testGenericLicenseUrl = genericLicenseUrlList.get(genericLicenseUrlList.size() - 1);
        assertThat(testGenericLicenseUrl.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    void patchNonExistingGenericLicenseUrl() throws Exception {
        int databaseSizeBeforeUpdate = genericLicenseUrlRepository.findAll().size();
        genericLicenseUrl.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGenericLicenseUrlMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, genericLicenseUrl.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(genericLicenseUrl))
            )
            .andExpect(status().isBadRequest());

        // Validate the GenericLicenseUrl in the database
        List<GenericLicenseUrl> genericLicenseUrlList = genericLicenseUrlRepository.findAll();
        assertThat(genericLicenseUrlList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGenericLicenseUrl() throws Exception {
        int databaseSizeBeforeUpdate = genericLicenseUrlRepository.findAll().size();
        genericLicenseUrl.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenericLicenseUrlMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(genericLicenseUrl))
            )
            .andExpect(status().isBadRequest());

        // Validate the GenericLicenseUrl in the database
        List<GenericLicenseUrl> genericLicenseUrlList = genericLicenseUrlRepository.findAll();
        assertThat(genericLicenseUrlList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGenericLicenseUrl() throws Exception {
        int databaseSizeBeforeUpdate = genericLicenseUrlRepository.findAll().size();
        genericLicenseUrl.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenericLicenseUrlMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(genericLicenseUrl))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GenericLicenseUrl in the database
        List<GenericLicenseUrl> genericLicenseUrlList = genericLicenseUrlRepository.findAll();
        assertThat(genericLicenseUrlList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGenericLicenseUrl() throws Exception {
        // Initialize the database
        genericLicenseUrlRepository.saveAndFlush(genericLicenseUrl);

        int databaseSizeBeforeDelete = genericLicenseUrlRepository.findAll().size();

        // Delete the genericLicenseUrl
        restGenericLicenseUrlMockMvc
            .perform(delete(ENTITY_API_URL_ID, genericLicenseUrl.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<GenericLicenseUrl> genericLicenseUrlList = genericLicenseUrlRepository.findAll();
        assertThat(genericLicenseUrlList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
