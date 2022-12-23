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
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.domain.License;
import net.regnology.lucy.domain.LicensePerLibrary;
import net.regnology.lucy.domain.enumeration.LinkType;
import net.regnology.lucy.repository.LicensePerLibraryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link LicensePerLibraryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LicensePerLibraryResourceIT {

    private static final LinkType DEFAULT_LINK_TYPE = LinkType.AND;
    private static final LinkType UPDATED_LINK_TYPE = LinkType.OR;

    private static final String ENTITY_API_URL = "/api/license-per-libraries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LicensePerLibraryRepository licensePerLibraryRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLicensePerLibraryMockMvc;

    private LicensePerLibrary licensePerLibrary;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LicensePerLibrary createEntity(EntityManager em) {
        LicensePerLibrary licensePerLibrary = new LicensePerLibrary().linkType(DEFAULT_LINK_TYPE);
        return licensePerLibrary;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LicensePerLibrary createUpdatedEntity(EntityManager em) {
        LicensePerLibrary licensePerLibrary = new LicensePerLibrary().linkType(UPDATED_LINK_TYPE);
        return licensePerLibrary;
    }

    @BeforeEach
    public void initTest() {
        licensePerLibrary = createEntity(em);
    }

    @Test
    @Transactional
    void createLicensePerLibrary() throws Exception {
        int databaseSizeBeforeCreate = licensePerLibraryRepository.findAll().size();
        // Create the LicensePerLibrary
        restLicensePerLibraryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(licensePerLibrary))
            )
            .andExpect(status().isCreated());

        // Validate the LicensePerLibrary in the database
        List<LicensePerLibrary> licensePerLibraryList = licensePerLibraryRepository.findAll();
        assertThat(licensePerLibraryList).hasSize(databaseSizeBeforeCreate + 1);
        LicensePerLibrary testLicensePerLibrary = licensePerLibraryList.get(licensePerLibraryList.size() - 1);
        Assertions.assertThat(testLicensePerLibrary.getLinkType()).isEqualTo(DEFAULT_LINK_TYPE);
    }

    @Test
    @Transactional
    void createLicensePerLibraryWithExistingId() throws Exception {
        // Create the LicensePerLibrary with an existing ID
        licensePerLibrary.setId(1L);

        int databaseSizeBeforeCreate = licensePerLibraryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLicensePerLibraryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(licensePerLibrary))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicensePerLibrary in the database
        List<LicensePerLibrary> licensePerLibraryList = licensePerLibraryRepository.findAll();
        assertThat(licensePerLibraryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllLicensePerLibraries() throws Exception {
        // Initialize the database
        licensePerLibraryRepository.saveAndFlush(licensePerLibrary);

        // Get all the licensePerLibraryList
        restLicensePerLibraryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(licensePerLibrary.getId().intValue())))
            .andExpect(jsonPath("$.[*].linkType").value(hasItem(DEFAULT_LINK_TYPE.toString())));
    }

    @Test
    @Transactional
    void getLicensePerLibrary() throws Exception {
        // Initialize the database
        licensePerLibraryRepository.saveAndFlush(licensePerLibrary);

        // Get the licensePerLibrary
        restLicensePerLibraryMockMvc
            .perform(get(ENTITY_API_URL_ID, licensePerLibrary.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(licensePerLibrary.getId().intValue()))
            .andExpect(jsonPath("$.linkType").value(DEFAULT_LINK_TYPE.toString()));
    }

    @Test
    @Transactional
    void getLicensePerLibrariesByIdFiltering() throws Exception {
        // Initialize the database
        licensePerLibraryRepository.saveAndFlush(licensePerLibrary);

        Long id = licensePerLibrary.getId();

        defaultLicensePerLibraryShouldBeFound("id.equals=" + id);
        defaultLicensePerLibraryShouldNotBeFound("id.notEquals=" + id);

        defaultLicensePerLibraryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultLicensePerLibraryShouldNotBeFound("id.greaterThan=" + id);

        defaultLicensePerLibraryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultLicensePerLibraryShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLicensePerLibrariesByLinkTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        licensePerLibraryRepository.saveAndFlush(licensePerLibrary);

        // Get all the licensePerLibraryList where linkType equals to DEFAULT_LINK_TYPE
        defaultLicensePerLibraryShouldBeFound("linkType.equals=" + DEFAULT_LINK_TYPE);

        // Get all the licensePerLibraryList where linkType equals to UPDATED_LINK_TYPE
        defaultLicensePerLibraryShouldNotBeFound("linkType.equals=" + UPDATED_LINK_TYPE);
    }

    @Test
    @Transactional
    void getAllLicensePerLibrariesByLinkTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        licensePerLibraryRepository.saveAndFlush(licensePerLibrary);

        // Get all the licensePerLibraryList where linkType not equals to DEFAULT_LINK_TYPE
        defaultLicensePerLibraryShouldNotBeFound("linkType.notEquals=" + DEFAULT_LINK_TYPE);

        // Get all the licensePerLibraryList where linkType not equals to UPDATED_LINK_TYPE
        defaultLicensePerLibraryShouldBeFound("linkType.notEquals=" + UPDATED_LINK_TYPE);
    }

    @Test
    @Transactional
    void getAllLicensePerLibrariesByLinkTypeIsInShouldWork() throws Exception {
        // Initialize the database
        licensePerLibraryRepository.saveAndFlush(licensePerLibrary);

        // Get all the licensePerLibraryList where linkType in DEFAULT_LINK_TYPE or UPDATED_LINK_TYPE
        defaultLicensePerLibraryShouldBeFound("linkType.in=" + DEFAULT_LINK_TYPE + "," + UPDATED_LINK_TYPE);

        // Get all the licensePerLibraryList where linkType equals to UPDATED_LINK_TYPE
        defaultLicensePerLibraryShouldNotBeFound("linkType.in=" + UPDATED_LINK_TYPE);
    }

    @Test
    @Transactional
    void getAllLicensePerLibrariesByLinkTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        licensePerLibraryRepository.saveAndFlush(licensePerLibrary);

        // Get all the licensePerLibraryList where linkType is not null
        defaultLicensePerLibraryShouldBeFound("linkType.specified=true");

        // Get all the licensePerLibraryList where linkType is null
        defaultLicensePerLibraryShouldNotBeFound("linkType.specified=false");
    }

    @Test
    @Transactional
    void getAllLicensePerLibrariesByLicenseIsEqualToSomething() throws Exception {
        // Initialize the database
        licensePerLibraryRepository.saveAndFlush(licensePerLibrary);
        License license;
        if (TestUtil.findAll(em, License.class).isEmpty()) {
            license = LicenseResourceIT.createEntity(em);
            em.persist(license);
            em.flush();
        } else {
            license = TestUtil.findAll(em, License.class).get(0);
        }
        em.persist(license);
        em.flush();
        licensePerLibrary.setLicense(license);
        licensePerLibraryRepository.saveAndFlush(licensePerLibrary);
        Long licenseId = license.getId();

        // Get all the licensePerLibraryList where license equals to licenseId
        defaultLicensePerLibraryShouldBeFound("licenseId.equals=" + licenseId);

        // Get all the licensePerLibraryList where license equals to (licenseId + 1)
        defaultLicensePerLibraryShouldNotBeFound("licenseId.equals=" + (licenseId + 1));
    }

    @Test
    @Transactional
    void getAllLicensePerLibrariesByLibraryIsEqualToSomething() throws Exception {
        // Initialize the database
        licensePerLibraryRepository.saveAndFlush(licensePerLibrary);
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
        licensePerLibrary.setLibrary(library);
        licensePerLibraryRepository.saveAndFlush(licensePerLibrary);
        Long libraryId = library.getId();

        // Get all the licensePerLibraryList where library equals to libraryId
        defaultLicensePerLibraryShouldBeFound("libraryId.equals=" + libraryId);

        // Get all the licensePerLibraryList where library equals to (libraryId + 1)
        defaultLicensePerLibraryShouldNotBeFound("libraryId.equals=" + (libraryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLicensePerLibraryShouldBeFound(String filter) throws Exception {
        restLicensePerLibraryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(licensePerLibrary.getId().intValue())))
            .andExpect(jsonPath("$.[*].linkType").value(hasItem(DEFAULT_LINK_TYPE.toString())));

        // Check, that the count call also returns 1
        restLicensePerLibraryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLicensePerLibraryShouldNotBeFound(String filter) throws Exception {
        restLicensePerLibraryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLicensePerLibraryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLicensePerLibrary() throws Exception {
        // Get the licensePerLibrary
        restLicensePerLibraryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLicensePerLibrary() throws Exception {
        // Initialize the database
        licensePerLibraryRepository.saveAndFlush(licensePerLibrary);

        int databaseSizeBeforeUpdate = licensePerLibraryRepository.findAll().size();

        // Update the licensePerLibrary
        LicensePerLibrary updatedLicensePerLibrary = licensePerLibraryRepository.findById(licensePerLibrary.getId()).get();
        // Disconnect from session so that the updates on updatedLicensePerLibrary are not directly saved in db
        em.detach(updatedLicensePerLibrary);
        updatedLicensePerLibrary.linkType(UPDATED_LINK_TYPE);

        restLicensePerLibraryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLicensePerLibrary.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLicensePerLibrary))
            )
            .andExpect(status().isOk());

        // Validate the LicensePerLibrary in the database
        List<LicensePerLibrary> licensePerLibraryList = licensePerLibraryRepository.findAll();
        assertThat(licensePerLibraryList).hasSize(databaseSizeBeforeUpdate);
        LicensePerLibrary testLicensePerLibrary = licensePerLibraryList.get(licensePerLibraryList.size() - 1);
        Assertions.assertThat(testLicensePerLibrary.getLinkType()).isEqualTo(UPDATED_LINK_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingLicensePerLibrary() throws Exception {
        int databaseSizeBeforeUpdate = licensePerLibraryRepository.findAll().size();
        licensePerLibrary.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLicensePerLibraryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, licensePerLibrary.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(licensePerLibrary))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicensePerLibrary in the database
        List<LicensePerLibrary> licensePerLibraryList = licensePerLibraryRepository.findAll();
        assertThat(licensePerLibraryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLicensePerLibrary() throws Exception {
        int databaseSizeBeforeUpdate = licensePerLibraryRepository.findAll().size();
        licensePerLibrary.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicensePerLibraryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(licensePerLibrary))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicensePerLibrary in the database
        List<LicensePerLibrary> licensePerLibraryList = licensePerLibraryRepository.findAll();
        assertThat(licensePerLibraryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLicensePerLibrary() throws Exception {
        int databaseSizeBeforeUpdate = licensePerLibraryRepository.findAll().size();
        licensePerLibrary.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicensePerLibraryMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(licensePerLibrary))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LicensePerLibrary in the database
        List<LicensePerLibrary> licensePerLibraryList = licensePerLibraryRepository.findAll();
        assertThat(licensePerLibraryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLicensePerLibraryWithPatch() throws Exception {
        // Initialize the database
        licensePerLibraryRepository.saveAndFlush(licensePerLibrary);

        int databaseSizeBeforeUpdate = licensePerLibraryRepository.findAll().size();

        // Update the licensePerLibrary using partial update
        LicensePerLibrary partialUpdatedLicensePerLibrary = new LicensePerLibrary();
        partialUpdatedLicensePerLibrary.setId(licensePerLibrary.getId());

        partialUpdatedLicensePerLibrary.linkType(UPDATED_LINK_TYPE);

        restLicensePerLibraryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLicensePerLibrary.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLicensePerLibrary))
            )
            .andExpect(status().isOk());

        // Validate the LicensePerLibrary in the database
        List<LicensePerLibrary> licensePerLibraryList = licensePerLibraryRepository.findAll();
        assertThat(licensePerLibraryList).hasSize(databaseSizeBeforeUpdate);
        LicensePerLibrary testLicensePerLibrary = licensePerLibraryList.get(licensePerLibraryList.size() - 1);
        Assertions.assertThat(testLicensePerLibrary.getLinkType()).isEqualTo(UPDATED_LINK_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateLicensePerLibraryWithPatch() throws Exception {
        // Initialize the database
        licensePerLibraryRepository.saveAndFlush(licensePerLibrary);

        int databaseSizeBeforeUpdate = licensePerLibraryRepository.findAll().size();

        // Update the licensePerLibrary using partial update
        LicensePerLibrary partialUpdatedLicensePerLibrary = new LicensePerLibrary();
        partialUpdatedLicensePerLibrary.setId(licensePerLibrary.getId());

        partialUpdatedLicensePerLibrary.linkType(UPDATED_LINK_TYPE);

        restLicensePerLibraryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLicensePerLibrary.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLicensePerLibrary))
            )
            .andExpect(status().isOk());

        // Validate the LicensePerLibrary in the database
        List<LicensePerLibrary> licensePerLibraryList = licensePerLibraryRepository.findAll();
        assertThat(licensePerLibraryList).hasSize(databaseSizeBeforeUpdate);
        LicensePerLibrary testLicensePerLibrary = licensePerLibraryList.get(licensePerLibraryList.size() - 1);
        Assertions.assertThat(testLicensePerLibrary.getLinkType()).isEqualTo(UPDATED_LINK_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingLicensePerLibrary() throws Exception {
        int databaseSizeBeforeUpdate = licensePerLibraryRepository.findAll().size();
        licensePerLibrary.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLicensePerLibraryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, licensePerLibrary.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(licensePerLibrary))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicensePerLibrary in the database
        List<LicensePerLibrary> licensePerLibraryList = licensePerLibraryRepository.findAll();
        assertThat(licensePerLibraryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLicensePerLibrary() throws Exception {
        int databaseSizeBeforeUpdate = licensePerLibraryRepository.findAll().size();
        licensePerLibrary.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicensePerLibraryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(licensePerLibrary))
            )
            .andExpect(status().isBadRequest());

        // Validate the LicensePerLibrary in the database
        List<LicensePerLibrary> licensePerLibraryList = licensePerLibraryRepository.findAll();
        assertThat(licensePerLibraryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLicensePerLibrary() throws Exception {
        int databaseSizeBeforeUpdate = licensePerLibraryRepository.findAll().size();
        licensePerLibrary.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLicensePerLibraryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(licensePerLibrary))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LicensePerLibrary in the database
        List<LicensePerLibrary> licensePerLibraryList = licensePerLibraryRepository.findAll();
        assertThat(licensePerLibraryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLicensePerLibrary() throws Exception {
        // Initialize the database
        licensePerLibraryRepository.saveAndFlush(licensePerLibrary);

        int databaseSizeBeforeDelete = licensePerLibraryRepository.findAll().size();

        // Delete the licensePerLibrary
        restLicensePerLibraryMockMvc
            .perform(delete(ENTITY_API_URL_ID, licensePerLibrary.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LicensePerLibrary> licensePerLibraryList = licensePerLibraryRepository.findAll();
        assertThat(licensePerLibraryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
