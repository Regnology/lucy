package net.regnology.lucy.service;

import net.regnology.lucy.domain.LicensePerLibrary;
import net.regnology.lucy.repository.LicensePerLibraryCustomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom service implementation for managing {@link LicensePerLibrary}.
 */
@Service
@Transactional
public class LicensePerLibraryCustomService extends LicensePerLibraryService {

    private final Logger log = LoggerFactory.getLogger(LicensePerLibraryCustomService.class);

    private final LicensePerLibraryCustomRepository licensePerLibraryRepository;

    public LicensePerLibraryCustomService(LicensePerLibraryCustomRepository licensePerLibraryRepository) {
        super(licensePerLibraryRepository);
        this.licensePerLibraryRepository = licensePerLibraryRepository;
    }

    /**
     * Delete all licensePerLibraries by library ID.
     *
     * @param id the id of the library.
     */
    public void deleteByLibraryId(Long id) {
        log.debug("Request to delete LicensePerLibrary where Library ID : {}", id);
        licensePerLibraryRepository.deleteByLibraryId(id);
    }
}
