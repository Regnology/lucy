package net.regnology.lucy.repository;

import java.util.List;
import java.util.Optional;
import net.regnology.lucy.domain.License;
import net.regnology.lucy.service.dto.LicenseConflictSimpleDTO;
import net.regnology.lucy.service.dto.LicenseConflictWithRiskDTO;
import net.regnology.lucy.service.dto.LicenseSimpleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Custom Spring Data SQL repository for the License entity.
 */
@Repository
public interface LicenseCustomRepository extends LicenseRepository, LicenseRepositoryWithBagRelationships {
    @Query("select license from License license where license.lastReviewedBy.login = ?#{principal.username}")
    List<License> findByLastReviewedByIsCurrentUser();

    @Query(
        value = "select distinct license from License license left join fetch license.requirements",
        countQuery = "select count(distinct license) from License license"
    )
    Page<License> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct license from License license left join fetch license.requirements")
    List<License> findAllWithEagerRelationships();

    @Query("select license from License license left join fetch license.requirements where license.id =:id")
    Optional<License> findOneWithEagerRelationships(@Param("id") Long id);

    @Query("select license from License license where license.shortIdentifier = :shortId")
    Optional<License> findOneByShortIdentifier(@Param("shortId") String shortIdentifier);

    /* @Query("select conflicts from License license left join license.licenseConflicts conflicts where license.id =:id order by conflicts.secondLicenseConflict.shortIdentifier")
    List<LicenseConflict> fetchLicenseConflicts(@Param("id") Long id);*/

    @Query(
        value = "select new net.regnology.lucy.service.dto.LicenseConflictSimpleDTO(" +
        "conflicts.id, conflicts.secondLicenseConflict.id, conflicts.secondLicenseConflict.fullName, " +
        "conflicts.secondLicenseConflict.shortIdentifier, conflicts.compatibility, conflicts.comment) " +
        "from License license left join license.licenseConflicts conflicts " +
        "where license.id =:id order by conflicts.secondLicenseConflict.shortIdentifier"
    )
    List<LicenseConflictSimpleDTO> fetchLicenseConflicts(@Param("id") Long id);

    @Query(
        value = "select new net.regnology.lucy.service.dto.LicenseConflictWithRiskDTO(" +
        "conflicts.id, conflicts.secondLicenseConflict.id, conflicts.secondLicenseConflict.fullName, " +
        "conflicts.secondLicenseConflict.shortIdentifier, conflicts.compatibility, conflicts.comment, conflicts.secondLicenseConflict.licenseRisk) " +
        "from License license left join license.licenseConflicts conflicts " +
        "where license.id =:id order by conflicts.secondLicenseConflict.shortIdentifier"
    )
    List<LicenseConflictWithRiskDTO> fetchLicenseConflictsWithRisk(@Param("id") Long id);

    @Query(
        "select new net.regnology.lucy.service.dto.LicenseSimpleDTO(license.id, license.fullName, license.shortIdentifier) " +
        "from License license order by license.shortIdentifier"
    )
    List<LicenseSimpleDTO> findAllSimpleDTO();

    @Query(
        value = "select distinct license from License license left join fetch license.licenseConflicts",
        countQuery = "select count(distinct license) from License license"
    )
    List<License> findAllWithLicenseConflicts();
}
