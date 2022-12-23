package net.regnology.lucy.service;

import java.util.Optional;
import net.regnology.lucy.domain.Upload;
import net.regnology.lucy.repository.UploadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Upload}.
 */
@Service
@Transactional
public class UploadService {

    private final Logger log = LoggerFactory.getLogger(UploadService.class);

    private final UploadRepository uploadRepository;

    public UploadService(UploadRepository uploadRepository) {
        this.uploadRepository = uploadRepository;
    }

    /**
     * Save a upload.
     *
     * @param upload the entity to save.
     * @return the persisted entity.
     */
    public Upload save(Upload upload) {
        log.debug("Request to save Upload : {}", upload);
        return uploadRepository.save(upload);
    }

    /**
     * Partially update a upload.
     *
     * @param upload the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Upload> partialUpdate(Upload upload) {
        log.debug("Request to partially update Upload : {}", upload);

        return uploadRepository
            .findById(upload.getId())
            .map(existingUpload -> {
                if (upload.getFile() != null) {
                    existingUpload.setFile(upload.getFile());
                }
                if (upload.getFileContentType() != null) {
                    existingUpload.setFileContentType(upload.getFileContentType());
                }
                if (upload.getEntityToUpload() != null) {
                    existingUpload.setEntityToUpload(upload.getEntityToUpload());
                }
                if (upload.getRecord() != null) {
                    existingUpload.setRecord(upload.getRecord());
                }
                if (upload.getOverwriteData() != null) {
                    existingUpload.setOverwriteData(upload.getOverwriteData());
                }
                if (upload.getUploadedDate() != null) {
                    existingUpload.setUploadedDate(upload.getUploadedDate());
                }

                return existingUpload;
            })
            .map(uploadRepository::save);
    }

    /**
     * Get all the uploads.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Upload> findAll(Pageable pageable) {
        log.debug("Request to get all Uploads");
        return uploadRepository.findAll(pageable);
    }

    /**
     * Get one upload by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Upload> findOne(Long id) {
        log.debug("Request to get Upload : {}", id);
        return uploadRepository.findById(id);
    }

    /**
     * Delete the upload by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Upload : {}", id);
        uploadRepository.deleteById(id);
    }
}
