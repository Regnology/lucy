package net.regnology.lucy.repository;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import net.regnology.lucy.domain.LicenseConflict;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Custom Spring Data SQL repository for the LicenseConflict entity.
 */
@Repository
public interface LicenseConflictCustomRepository extends LicenseConflictRepository {
    @Query("select licenseConflict from LicenseConflict licenseConflict where licenseConflict.firstLicenseConflict.id = :licenseId")
    List<LicenseConflict> findLicenseConflictsByLicenseId(@Param("licenseId") Long licenseId);

    @Query(
        "select licenseConflict from LicenseConflict licenseConflict where licenseConflict.firstLicenseConflict.id = :licenseId " +
        "and licenseConflict.compatibility = net.regnology.lucy.domain.enumeration.CompatibilityState.Incompatible"
    )
    List<LicenseConflict> findIncompatibleLicenseConflictsByLicenseId(@Param("licenseId") Long licenseId);

    @Query(
        "select licenseConflict from LicenseConflict licenseConflict where licenseConflict.firstLicenseConflict.id = :firstLicenseId " +
        "and licenseConflict.secondLicenseConflict.id = :secondLicenseId " +
        "and licenseConflict.compatibility = net.regnology.lucy.domain.enumeration.CompatibilityState.Incompatible"
    )
    Optional<LicenseConflict> findIncompatibleLicenseConflict(
        @Param("firstLicenseId") Long firstLicenseId,
        @Param("secondLicenseId") Long secondLicenseId
    );

    @Transactional
    @Modifying
    @Query(
        "delete from LicenseConflict licenseConflict where licenseConflict.firstLicenseConflict.id = :licenseId or " +
        "licenseConflict.secondLicenseConflict.id = :licenseId"
    )
    void deleteByLicenseId(@Param("licenseId") Long licenseId);
}
