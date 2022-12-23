package net.regnology.lucy.service;

import java.util.List;
import net.regnology.lucy.domain.LicensePerLibrary;
import net.regnology.lucy.repository.LicensePerLibraryCustomRepository;
import net.regnology.lucy.service.criteria.LicensePerLibraryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom service for executing complex queries for {@link LicensePerLibrary} entities in the database.
 * The main input is a {@link LicensePerLibraryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LicensePerLibrary} or a {@link Page} of {@link LicensePerLibrary} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LicensePerLibraryQueryCustomService extends LicensePerLibraryQueryService {

    public LicensePerLibraryQueryCustomService(LicensePerLibraryCustomRepository licensePerLibraryRepository) {
        super(licensePerLibraryRepository);
    }
}
