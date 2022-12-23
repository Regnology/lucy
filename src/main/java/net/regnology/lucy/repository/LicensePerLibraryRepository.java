package net.regnology.lucy.repository;

import net.regnology.lucy.domain.LicensePerLibrary;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LicensePerLibrary entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LicensePerLibraryRepository extends JpaRepository<LicensePerLibrary, Long>, JpaSpecificationExecutor<LicensePerLibrary> {}
