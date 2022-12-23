package net.regnology.lucy.repository;

import java.util.List;
import java.util.Optional;
import net.regnology.lucy.domain.License;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the License entity.
 */
@Repository
public interface LicenseRepository
    extends LicenseRepositoryWithBagRelationships, JpaRepository<License, Long>, JpaSpecificationExecutor<License> {
    @Query("select license from License license where license.lastReviewedBy.login = ?#{principal.username}")
    List<License> findByLastReviewedByIsCurrentUser();

    default Optional<License> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<License> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<License> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct license from License license left join fetch license.lastReviewedBy left join fetch license.licenseRisk",
        countQuery = "select count(distinct license) from License license"
    )
    Page<License> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct license from License license left join fetch license.lastReviewedBy left join fetch license.licenseRisk")
    List<License> findAllWithToOneRelationships();

    @Query(
        "select license from License license left join fetch license.lastReviewedBy left join fetch license.licenseRisk where license.id =:id"
    )
    Optional<License> findOneWithToOneRelationships(@Param("id") Long id);
}
