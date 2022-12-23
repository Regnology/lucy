package net.regnology.lucy.service;

import java.util.Optional;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.repository.LibraryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Library}.
 */
@Service
@Transactional
public class LibraryService {

    private final Logger log = LoggerFactory.getLogger(LibraryService.class);

    private final LibraryRepository libraryRepository;

    public LibraryService(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }

    /**
     * Save a library.
     *
     * @param library the entity to save.
     * @return the persisted entity.
     */
    public Library save(Library library) {
        log.debug("Request to save Library : {}", library);

        return libraryRepository.save(library);
    }

    /**
     * Partially update a library.
     *
     * @param library the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Library> partialUpdate(Library library) {
        log.debug("Request to partially update Library : {}", library);

        return libraryRepository
            .findById(library.getId())
            .map(existingLibrary -> {
                if (library.getGroupId() != null) {
                    existingLibrary.setGroupId(library.getGroupId());
                }
                if (library.getArtifactId() != null) {
                    existingLibrary.setArtifactId(library.getArtifactId());
                }
                if (library.getVersion() != null) {
                    existingLibrary.setVersion(library.getVersion());
                }
                if (library.getType() != null) {
                    existingLibrary.setType(library.getType());
                }
                if (library.getOriginalLicense() != null) {
                    existingLibrary.setOriginalLicense(library.getOriginalLicense());
                }
                if (library.getLicenseUrl() != null) {
                    existingLibrary.setLicenseUrl(library.getLicenseUrl());
                }
                if (library.getLicenseText() != null) {
                    existingLibrary.setLicenseText(library.getLicenseText());
                }
                if (library.getSourceCodeUrl() != null) {
                    existingLibrary.setSourceCodeUrl(library.getSourceCodeUrl());
                }
                if (library.getpUrl() != null) {
                    existingLibrary.setpUrl(library.getpUrl());
                }
                if (library.getCopyright() != null) {
                    existingLibrary.setCopyright(library.getCopyright());
                }
                if (library.getCompliance() != null) {
                    existingLibrary.setCompliance(library.getCompliance());
                }
                if (library.getComplianceComment() != null) {
                    existingLibrary.setComplianceComment(library.getComplianceComment());
                }
                if (library.getComment() != null) {
                    existingLibrary.setComment(library.getComment());
                }
                if (library.getReviewed() != null) {
                    existingLibrary.setReviewed(library.getReviewed());
                }
                if (library.getLastReviewedDate() != null) {
                    existingLibrary.setLastReviewedDate(library.getLastReviewedDate());
                }
                if (library.getReviewedDeepScan() != null) {
                    existingLibrary.setReviewedDeepScan(library.getReviewedDeepScan());
                }
                if (library.getLastReviewedDeepScanDate() != null) {
                    existingLibrary.setLastReviewedDeepScanDate(library.getLastReviewedDeepScanDate());
                }
                if (library.getCreatedDate() != null) {
                    existingLibrary.setCreatedDate(library.getCreatedDate());
                }
                if (library.getHideForPublishing() != null) {
                    existingLibrary.setHideForPublishing(library.getHideForPublishing());
                }
                if (library.getMd5() != null) {
                    existingLibrary.setMd5(library.getMd5());
                }
                if (library.getSha1() != null) {
                    existingLibrary.setSha1(library.getSha1());
                }

                return existingLibrary;
            })
            .map(libraryRepository::save);
    }

    /**
     * Get all the libraries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Library> findAll(Pageable pageable) {
        log.debug("Request to get all Libraries");
        return libraryRepository.findAll(pageable);
    }

    /**
     * Get all the libraries with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Library> findAllWithEagerRelationships(Pageable pageable) {
        return libraryRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one library by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Library> findOne(Long id) {
        log.debug("Request to get Library : {}", id);
        return libraryRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the library by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Library : {}", id);
        libraryRepository.deleteById(id);
    }
}
