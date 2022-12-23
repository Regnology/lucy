package net.regnology.lucy.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import net.regnology.lucy.IntegrationTest;
import net.regnology.lucy.domain.Upload;
import net.regnology.lucy.domain.enumeration.EntityUploadChoice;
import net.regnology.lucy.repository.UploadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link UploadResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UploadResourceIT {

    private static final byte[] DEFAULT_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILE_CONTENT_TYPE = "image/png";

    private static final EntityUploadChoice DEFAULT_ENTITY_TO_UPLOAD = EntityUploadChoice.PRODUCT;
    private static final EntityUploadChoice UPDATED_ENTITY_TO_UPLOAD = EntityUploadChoice.LIBRARY;

    private static final Integer DEFAULT_RECORD = 1;
    private static final Integer UPDATED_RECORD = 2;

    private static final Boolean DEFAULT_OVERWRITE_DATA = false;
    private static final Boolean UPDATED_OVERWRITE_DATA = true;

    private static final LocalDate DEFAULT_UPLOADED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPLOADED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/uploads";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UploadRepository uploadRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUploadMockMvc;

    private Upload upload;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Upload createEntity(EntityManager em) {
        Upload upload = new Upload()
            .file(DEFAULT_FILE)
            .fileContentType(DEFAULT_FILE_CONTENT_TYPE)
            .entityToUpload(DEFAULT_ENTITY_TO_UPLOAD)
            .record(DEFAULT_RECORD)
            .overwriteData(DEFAULT_OVERWRITE_DATA)
            .uploadedDate(DEFAULT_UPLOADED_DATE);
        return upload;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Upload createUpdatedEntity(EntityManager em) {
        Upload upload = new Upload()
            .file(UPDATED_FILE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .entityToUpload(UPDATED_ENTITY_TO_UPLOAD)
            .record(UPDATED_RECORD)
            .overwriteData(UPDATED_OVERWRITE_DATA)
            .uploadedDate(UPDATED_UPLOADED_DATE);
        return upload;
    }

    @BeforeEach
    public void initTest() {
        upload = createEntity(em);
    }

    @Test
    @Transactional
    void createUpload() throws Exception {
        int databaseSizeBeforeCreate = uploadRepository.findAll().size();
        // Create the Upload
        restUploadMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(upload)))
            .andExpect(status().isCreated());

        // Validate the Upload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeCreate + 1);
        Upload testUpload = uploadList.get(uploadList.size() - 1);
        assertThat(testUpload.getFile()).isEqualTo(DEFAULT_FILE);
        assertThat(testUpload.getFileContentType()).isEqualTo(DEFAULT_FILE_CONTENT_TYPE);
        assertThat(testUpload.getEntityToUpload()).isEqualTo(DEFAULT_ENTITY_TO_UPLOAD);
        assertThat(testUpload.getRecord()).isEqualTo(DEFAULT_RECORD);
        assertThat(testUpload.getOverwriteData()).isEqualTo(DEFAULT_OVERWRITE_DATA);
        assertThat(testUpload.getUploadedDate()).isEqualTo(DEFAULT_UPLOADED_DATE);
    }

    @Test
    @Transactional
    void createUploadWithExistingId() throws Exception {
        // Create the Upload with an existing ID
        upload.setId(1L);

        int databaseSizeBeforeCreate = uploadRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUploadMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(upload)))
            .andExpect(status().isBadRequest());

        // Validate the Upload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllUploads() throws Exception {
        // Initialize the database
        uploadRepository.saveAndFlush(upload);

        // Get all the uploadList
        restUploadMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(upload.getId().intValue())))
            .andExpect(jsonPath("$.[*].fileContentType").value(hasItem(DEFAULT_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].file").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE))))
            .andExpect(jsonPath("$.[*].entityToUpload").value(hasItem(DEFAULT_ENTITY_TO_UPLOAD.toString())))
            .andExpect(jsonPath("$.[*].record").value(hasItem(DEFAULT_RECORD)))
            .andExpect(jsonPath("$.[*].overwriteData").value(hasItem(DEFAULT_OVERWRITE_DATA.booleanValue())))
            .andExpect(jsonPath("$.[*].uploadedDate").value(hasItem(DEFAULT_UPLOADED_DATE.toString())));
    }

    @Test
    @Transactional
    void getUpload() throws Exception {
        // Initialize the database
        uploadRepository.saveAndFlush(upload);

        // Get the upload
        restUploadMockMvc
            .perform(get(ENTITY_API_URL_ID, upload.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(upload.getId().intValue()))
            .andExpect(jsonPath("$.fileContentType").value(DEFAULT_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.file").value(Base64Utils.encodeToString(DEFAULT_FILE)))
            .andExpect(jsonPath("$.entityToUpload").value(DEFAULT_ENTITY_TO_UPLOAD.toString()))
            .andExpect(jsonPath("$.record").value(DEFAULT_RECORD))
            .andExpect(jsonPath("$.overwriteData").value(DEFAULT_OVERWRITE_DATA.booleanValue()))
            .andExpect(jsonPath("$.uploadedDate").value(DEFAULT_UPLOADED_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUpload() throws Exception {
        // Get the upload
        restUploadMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUpload() throws Exception {
        // Initialize the database
        uploadRepository.saveAndFlush(upload);

        int databaseSizeBeforeUpdate = uploadRepository.findAll().size();

        // Update the upload
        Upload updatedUpload = uploadRepository.findById(upload.getId()).get();
        // Disconnect from session so that the updates on updatedUpload are not directly saved in db
        em.detach(updatedUpload);
        updatedUpload
            .file(UPDATED_FILE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .entityToUpload(UPDATED_ENTITY_TO_UPLOAD)
            .record(UPDATED_RECORD)
            .overwriteData(UPDATED_OVERWRITE_DATA)
            .uploadedDate(UPDATED_UPLOADED_DATE);

        restUploadMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUpload.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUpload))
            )
            .andExpect(status().isOk());

        // Validate the Upload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeUpdate);
        Upload testUpload = uploadList.get(uploadList.size() - 1);
        assertThat(testUpload.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testUpload.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);
        assertThat(testUpload.getEntityToUpload()).isEqualTo(UPDATED_ENTITY_TO_UPLOAD);
        assertThat(testUpload.getRecord()).isEqualTo(UPDATED_RECORD);
        assertThat(testUpload.getOverwriteData()).isEqualTo(UPDATED_OVERWRITE_DATA);
        assertThat(testUpload.getUploadedDate()).isEqualTo(UPDATED_UPLOADED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingUpload() throws Exception {
        int databaseSizeBeforeUpdate = uploadRepository.findAll().size();
        upload.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUploadMockMvc
            .perform(
                put(ENTITY_API_URL_ID, upload.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(upload))
            )
            .andExpect(status().isBadRequest());

        // Validate the Upload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUpload() throws Exception {
        int databaseSizeBeforeUpdate = uploadRepository.findAll().size();
        upload.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUploadMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(upload))
            )
            .andExpect(status().isBadRequest());

        // Validate the Upload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUpload() throws Exception {
        int databaseSizeBeforeUpdate = uploadRepository.findAll().size();
        upload.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUploadMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(upload)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Upload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUploadWithPatch() throws Exception {
        // Initialize the database
        uploadRepository.saveAndFlush(upload);

        int databaseSizeBeforeUpdate = uploadRepository.findAll().size();

        // Update the upload using partial update
        Upload partialUpdatedUpload = new Upload();
        partialUpdatedUpload.setId(upload.getId());

        partialUpdatedUpload
            .file(UPDATED_FILE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .entityToUpload(UPDATED_ENTITY_TO_UPLOAD)
            .record(UPDATED_RECORD);

        restUploadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUpload.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUpload))
            )
            .andExpect(status().isOk());

        // Validate the Upload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeUpdate);
        Upload testUpload = uploadList.get(uploadList.size() - 1);
        assertThat(testUpload.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testUpload.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);
        assertThat(testUpload.getEntityToUpload()).isEqualTo(UPDATED_ENTITY_TO_UPLOAD);
        assertThat(testUpload.getRecord()).isEqualTo(UPDATED_RECORD);
        assertThat(testUpload.getOverwriteData()).isEqualTo(DEFAULT_OVERWRITE_DATA);
        assertThat(testUpload.getUploadedDate()).isEqualTo(DEFAULT_UPLOADED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateUploadWithPatch() throws Exception {
        // Initialize the database
        uploadRepository.saveAndFlush(upload);

        int databaseSizeBeforeUpdate = uploadRepository.findAll().size();

        // Update the upload using partial update
        Upload partialUpdatedUpload = new Upload();
        partialUpdatedUpload.setId(upload.getId());

        partialUpdatedUpload
            .file(UPDATED_FILE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .entityToUpload(UPDATED_ENTITY_TO_UPLOAD)
            .record(UPDATED_RECORD)
            .overwriteData(UPDATED_OVERWRITE_DATA)
            .uploadedDate(UPDATED_UPLOADED_DATE);

        restUploadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUpload.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUpload))
            )
            .andExpect(status().isOk());

        // Validate the Upload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeUpdate);
        Upload testUpload = uploadList.get(uploadList.size() - 1);
        assertThat(testUpload.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testUpload.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);
        assertThat(testUpload.getEntityToUpload()).isEqualTo(UPDATED_ENTITY_TO_UPLOAD);
        assertThat(testUpload.getRecord()).isEqualTo(UPDATED_RECORD);
        assertThat(testUpload.getOverwriteData()).isEqualTo(UPDATED_OVERWRITE_DATA);
        assertThat(testUpload.getUploadedDate()).isEqualTo(UPDATED_UPLOADED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingUpload() throws Exception {
        int databaseSizeBeforeUpdate = uploadRepository.findAll().size();
        upload.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUploadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, upload.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(upload))
            )
            .andExpect(status().isBadRequest());

        // Validate the Upload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUpload() throws Exception {
        int databaseSizeBeforeUpdate = uploadRepository.findAll().size();
        upload.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUploadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(upload))
            )
            .andExpect(status().isBadRequest());

        // Validate the Upload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUpload() throws Exception {
        int databaseSizeBeforeUpdate = uploadRepository.findAll().size();
        upload.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUploadMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(upload)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Upload in the database
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUpload() throws Exception {
        // Initialize the database
        uploadRepository.saveAndFlush(upload);

        int databaseSizeBeforeDelete = uploadRepository.findAll().size();

        // Delete the upload
        restUploadMockMvc
            .perform(delete(ENTITY_API_URL_ID, upload.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Upload> uploadList = uploadRepository.findAll();
        assertThat(uploadList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
