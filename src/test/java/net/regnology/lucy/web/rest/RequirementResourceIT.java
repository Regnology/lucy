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
import net.regnology.lucy.domain.Requirement;
import net.regnology.lucy.repository.RequirementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RequirementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RequirementResourceIT {

    private static final String DEFAULT_SHORT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/requirements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RequirementRepository requirementRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRequirementMockMvc;

    private Requirement requirement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Requirement createEntity(EntityManager em) {
        Requirement requirement = new Requirement().shortText(DEFAULT_SHORT_TEXT).description(DEFAULT_DESCRIPTION);
        return requirement;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Requirement createUpdatedEntity(EntityManager em) {
        Requirement requirement = new Requirement().shortText(UPDATED_SHORT_TEXT).description(UPDATED_DESCRIPTION);
        return requirement;
    }

    @BeforeEach
    public void initTest() {
        requirement = createEntity(em);
    }

    @Test
    @Transactional
    void createRequirement() throws Exception {
        int databaseSizeBeforeCreate = requirementRepository.findAll().size();
        // Create the Requirement
        restRequirementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(requirement)))
            .andExpect(status().isCreated());

        // Validate the Requirement in the database
        List<Requirement> requirementList = requirementRepository.findAll();
        assertThat(requirementList).hasSize(databaseSizeBeforeCreate + 1);
        Requirement testRequirement = requirementList.get(requirementList.size() - 1);
        assertThat(testRequirement.getShortText()).isEqualTo(DEFAULT_SHORT_TEXT);
        assertThat(testRequirement.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createRequirementWithExistingId() throws Exception {
        // Create the Requirement with an existing ID
        requirement.setId(1L);

        int databaseSizeBeforeCreate = requirementRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRequirementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(requirement)))
            .andExpect(status().isBadRequest());

        // Validate the Requirement in the database
        List<Requirement> requirementList = requirementRepository.findAll();
        assertThat(requirementList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkShortTextIsRequired() throws Exception {
        int databaseSizeBeforeTest = requirementRepository.findAll().size();
        // set the field null
        requirement.setShortText(null);

        // Create the Requirement, which fails.

        restRequirementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(requirement)))
            .andExpect(status().isBadRequest());

        List<Requirement> requirementList = requirementRepository.findAll();
        assertThat(requirementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRequirements() throws Exception {
        // Initialize the database
        requirementRepository.saveAndFlush(requirement);

        // Get all the requirementList
        restRequirementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(requirement.getId().intValue())))
            .andExpect(jsonPath("$.[*].shortText").value(hasItem(DEFAULT_SHORT_TEXT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getRequirement() throws Exception {
        // Initialize the database
        requirementRepository.saveAndFlush(requirement);

        // Get the requirement
        restRequirementMockMvc
            .perform(get(ENTITY_API_URL_ID, requirement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(requirement.getId().intValue()))
            .andExpect(jsonPath("$.shortText").value(DEFAULT_SHORT_TEXT))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingRequirement() throws Exception {
        // Get the requirement
        restRequirementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRequirement() throws Exception {
        // Initialize the database
        requirementRepository.saveAndFlush(requirement);

        int databaseSizeBeforeUpdate = requirementRepository.findAll().size();

        // Update the requirement
        Requirement updatedRequirement = requirementRepository.findById(requirement.getId()).get();
        // Disconnect from session so that the updates on updatedRequirement are not directly saved in db
        em.detach(updatedRequirement);
        updatedRequirement.shortText(UPDATED_SHORT_TEXT).description(UPDATED_DESCRIPTION);

        restRequirementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRequirement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRequirement))
            )
            .andExpect(status().isOk());

        // Validate the Requirement in the database
        List<Requirement> requirementList = requirementRepository.findAll();
        assertThat(requirementList).hasSize(databaseSizeBeforeUpdate);
        Requirement testRequirement = requirementList.get(requirementList.size() - 1);
        assertThat(testRequirement.getShortText()).isEqualTo(UPDATED_SHORT_TEXT);
        assertThat(testRequirement.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingRequirement() throws Exception {
        int databaseSizeBeforeUpdate = requirementRepository.findAll().size();
        requirement.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRequirementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, requirement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(requirement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Requirement in the database
        List<Requirement> requirementList = requirementRepository.findAll();
        assertThat(requirementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRequirement() throws Exception {
        int databaseSizeBeforeUpdate = requirementRepository.findAll().size();
        requirement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRequirementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(requirement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Requirement in the database
        List<Requirement> requirementList = requirementRepository.findAll();
        assertThat(requirementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRequirement() throws Exception {
        int databaseSizeBeforeUpdate = requirementRepository.findAll().size();
        requirement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRequirementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(requirement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Requirement in the database
        List<Requirement> requirementList = requirementRepository.findAll();
        assertThat(requirementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRequirementWithPatch() throws Exception {
        // Initialize the database
        requirementRepository.saveAndFlush(requirement);

        int databaseSizeBeforeUpdate = requirementRepository.findAll().size();

        // Update the requirement using partial update
        Requirement partialUpdatedRequirement = new Requirement();
        partialUpdatedRequirement.setId(requirement.getId());

        restRequirementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRequirement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRequirement))
            )
            .andExpect(status().isOk());

        // Validate the Requirement in the database
        List<Requirement> requirementList = requirementRepository.findAll();
        assertThat(requirementList).hasSize(databaseSizeBeforeUpdate);
        Requirement testRequirement = requirementList.get(requirementList.size() - 1);
        assertThat(testRequirement.getShortText()).isEqualTo(DEFAULT_SHORT_TEXT);
        assertThat(testRequirement.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateRequirementWithPatch() throws Exception {
        // Initialize the database
        requirementRepository.saveAndFlush(requirement);

        int databaseSizeBeforeUpdate = requirementRepository.findAll().size();

        // Update the requirement using partial update
        Requirement partialUpdatedRequirement = new Requirement();
        partialUpdatedRequirement.setId(requirement.getId());

        partialUpdatedRequirement.shortText(UPDATED_SHORT_TEXT).description(UPDATED_DESCRIPTION);

        restRequirementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRequirement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRequirement))
            )
            .andExpect(status().isOk());

        // Validate the Requirement in the database
        List<Requirement> requirementList = requirementRepository.findAll();
        assertThat(requirementList).hasSize(databaseSizeBeforeUpdate);
        Requirement testRequirement = requirementList.get(requirementList.size() - 1);
        assertThat(testRequirement.getShortText()).isEqualTo(UPDATED_SHORT_TEXT);
        assertThat(testRequirement.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingRequirement() throws Exception {
        int databaseSizeBeforeUpdate = requirementRepository.findAll().size();
        requirement.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRequirementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, requirement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(requirement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Requirement in the database
        List<Requirement> requirementList = requirementRepository.findAll();
        assertThat(requirementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRequirement() throws Exception {
        int databaseSizeBeforeUpdate = requirementRepository.findAll().size();
        requirement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRequirementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(requirement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Requirement in the database
        List<Requirement> requirementList = requirementRepository.findAll();
        assertThat(requirementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRequirement() throws Exception {
        int databaseSizeBeforeUpdate = requirementRepository.findAll().size();
        requirement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRequirementMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(requirement))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Requirement in the database
        List<Requirement> requirementList = requirementRepository.findAll();
        assertThat(requirementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRequirement() throws Exception {
        // Initialize the database
        requirementRepository.saveAndFlush(requirement);

        int databaseSizeBeforeDelete = requirementRepository.findAll().size();

        // Delete the requirement
        restRequirementMockMvc
            .perform(delete(ENTITY_API_URL_ID, requirement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Requirement> requirementList = requirementRepository.findAll();
        assertThat(requirementList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
