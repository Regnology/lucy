package net.regnology.lucy.repository;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import net.regnology.lucy.domain.License;
import org.hibernate.annotations.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class LicenseRepositoryWithBagRelationshipsImpl implements LicenseRepositoryWithBagRelationships {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Optional<License> fetchBagRelationships(Optional<License> license) {
        return license.map(this::fetchRequirements);
    }

    @Override
    public Page<License> fetchBagRelationships(Page<License> licenses) {
        return new PageImpl<>(fetchBagRelationships(licenses.getContent()), licenses.getPageable(), licenses.getTotalElements());
    }

    @Override
    public List<License> fetchBagRelationships(List<License> licenses) {
        return Optional.of(licenses).map(this::fetchRequirements).get();
    }

    License fetchRequirements(License result) {
        return entityManager
            .createQuery(
                "select license from License license left join fetch license.requirements where license is :license",
                License.class
            )
            .setParameter("license", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<License> fetchRequirements(List<License> licenses) {
        return entityManager
            .createQuery(
                "select distinct license from License license left join fetch license.requirements where license in :licenses",
                License.class
            )
            .setParameter("licenses", licenses)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
