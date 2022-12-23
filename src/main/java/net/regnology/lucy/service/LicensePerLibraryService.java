package net.regnology.lucy.service;

import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.domain.LicensePerLibrary;
import net.regnology.lucy.repository.LicensePerLibraryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link LicensePerLibrary}.
 */
@Service
@Transactional
public class LicensePerLibraryService {

    private final Logger log = LoggerFactory.getLogger(LicensePerLibraryService.class);

    private final LicensePerLibraryRepository licensePerLibraryRepository;

    public LicensePerLibraryService(LicensePerLibraryRepository licensePerLibraryRepository) {
        this.licensePerLibraryRepository = licensePerLibraryRepository;
    }

    /**
     * Save a licensePerLibrary.
     *
     * @param licensePerLibrary the entity to save.
     * @return the persisted entity.
     */
    public LicensePerLibrary save(LicensePerLibrary licensePerLibrary) {
        log.debug("Request to save LicensePerLibrary : {}", licensePerLibrary);
        return licensePerLibraryRepository.save(licensePerLibrary);
    }

    /**
     * Save a licensePerLibrary.
     *
     * @param licensePerLibraries entities to save.
     * @return the persisted entities.
     */
    public List<LicensePerLibrary> saveAll(SortedSet<LicensePerLibrary> licensePerLibraries, Long libraryId) {
        log.debug("Request to save LicensePerLibraries : {}", licensePerLibraries);
        for (LicensePerLibrary lpp : licensePerLibraries) {
            lpp.setLibrary(new Library().id(libraryId));
        }
        return licensePerLibraryRepository.saveAll(licensePerLibraries);
    }

    /**
     * Save and flush a sorted Set of licensePerLibraries.
     *
     * @param licensePerLibraries entities to save.
     * @param libraryId Library ID for which these licensePerLibraries should be saved
     * @return the persisted entities.
     */
    public List<LicensePerLibrary> saveAllAndFlush(SortedSet<LicensePerLibrary> licensePerLibraries, Long libraryId) {
        log.debug("Request to save LicensePerLibraries : {}", licensePerLibraries);
        for (LicensePerLibrary lpp : licensePerLibraries) {
            lpp.setLibrary(new Library().id(libraryId));
        }
        return licensePerLibraryRepository.saveAllAndFlush(licensePerLibraries);
    }

    /**
     * Partially update a licensePerLibrary.
     *
     * @param licensePerLibrary the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LicensePerLibrary> partialUpdate(LicensePerLibrary licensePerLibrary) {
        log.debug("Request to partially update LicensePerLibrary : {}", licensePerLibrary);

        return licensePerLibraryRepository
            .findById(licensePerLibrary.getId())
            .map(existingLicensePerLibrary -> {
                if (licensePerLibrary.getLinkType() != null) {
                    existingLicensePerLibrary.setLinkType(licensePerLibrary.getLinkType());
                }

                return existingLicensePerLibrary;
            })
            .map(licensePerLibraryRepository::save);
    }

    /**
     * Get all the licensePerLibraries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LicensePerLibrary> findAll(Pageable pageable) {
        log.debug("Request to get all LicensePerLibraries");
        return licensePerLibraryRepository.findAll(pageable);
    }

    /**
     * Get one licensePerLibrary by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LicensePerLibrary> findOne(Long id) {
        log.debug("Request to get LicensePerLibrary : {}", id);
        return licensePerLibraryRepository.findById(id);
    }

    /**
     * Delete the licensePerLibrary by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete LicensePerLibrary : {}", id);
        licensePerLibraryRepository.deleteById(id);
    }
}
