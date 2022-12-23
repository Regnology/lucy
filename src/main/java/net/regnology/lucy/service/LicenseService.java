package net.regnology.lucy.service;

import java.util.Optional;
import net.regnology.lucy.domain.License;
import net.regnology.lucy.repository.LicenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link License}.
 */
@Service
@Transactional
public class LicenseService {

    private final Logger log = LoggerFactory.getLogger(LicenseService.class);

    private final LicenseRepository licenseRepository;

    public LicenseService(LicenseRepository licenseRepository) {
        this.licenseRepository = licenseRepository;
    }

    /**
     * Save a license.
     *
     * @param license the entity to save.
     * @return the persisted entity.
     */
    public License save(License license) {
        log.debug("Request to save License : {}", license);
        return licenseRepository.save(license);
    }

    /**
     * Partially update a license.
     *
     * @param license the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<License> partialUpdate(License license) {
        log.debug("Request to partially update License : {}", license);

        return licenseRepository
            .findById(license.getId())
            .map(existingLicense -> {
                if (license.getFullName() != null) {
                    existingLicense.setFullName(license.getFullName());
                }
                if (license.getShortIdentifier() != null) {
                    existingLicense.setShortIdentifier(license.getShortIdentifier());
                }
                if (license.getSpdxIdentifier() != null) {
                    existingLicense.setSpdxIdentifier(license.getSpdxIdentifier());
                }
                if (license.getUrl() != null) {
                    existingLicense.setUrl(license.getUrl());
                }
                if (license.getGenericLicenseText() != null) {
                    existingLicense.setGenericLicenseText(license.getGenericLicenseText());
                }
                if (license.getOther() != null) {
                    existingLicense.setOther(license.getOther());
                }
                if (license.getReviewed() != null) {
                    existingLicense.setReviewed(license.getReviewed());
                }
                if (license.getLastReviewedDate() != null) {
                    existingLicense.setLastReviewedDate(license.getLastReviewedDate());
                }

                return existingLicense;
            })
            .map(licenseRepository::save);
    }

    /**
     * Get all the licenses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<License> findAll(Pageable pageable) {
        log.debug("Request to get all Licenses");
        return licenseRepository.findAll(pageable);
    }

    /**
     * Get all the licenses with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<License> findAllWithEagerRelationships(Pageable pageable) {
        return licenseRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one license by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<License> findOne(Long id) {
        log.debug("Request to get License : {}", id);
        return licenseRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the license by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete License : {}", id);
        licenseRepository.deleteById(id);
    }
}
