package net.regnology.lucy.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import net.regnology.lucy.domain.Library_;
import net.regnology.lucy.domain.LicensePerLibrary;
import net.regnology.lucy.domain.LicensePerLibrary_;
import net.regnology.lucy.domain.License_;
import net.regnology.lucy.repository.LicensePerLibraryRepository;
import net.regnology.lucy.service.criteria.LicensePerLibraryCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link LicensePerLibrary} entities in the database.
 * The main input is a {@link LicensePerLibraryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LicensePerLibrary} or a {@link Page} of {@link LicensePerLibrary} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LicensePerLibraryQueryService extends QueryService<LicensePerLibrary> {

    private final Logger log = LoggerFactory.getLogger(LicensePerLibraryQueryService.class);

    private final LicensePerLibraryRepository licensePerLibraryRepository;

    public LicensePerLibraryQueryService(LicensePerLibraryRepository licensePerLibraryRepository) {
        this.licensePerLibraryRepository = licensePerLibraryRepository;
    }

    /**
     * Return a {@link List} of {@link LicensePerLibrary} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LicensePerLibrary> findByCriteria(LicensePerLibraryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<LicensePerLibrary> specification = createSpecification(criteria);
        return licensePerLibraryRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link LicensePerLibrary} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LicensePerLibrary> findByCriteria(LicensePerLibraryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<LicensePerLibrary> specification = createSpecification(criteria);
        return licensePerLibraryRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LicensePerLibraryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<LicensePerLibrary> specification = createSpecification(criteria);
        return licensePerLibraryRepository.count(specification);
    }

    /**
     * Function to convert {@link LicensePerLibraryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<LicensePerLibrary> createSpecification(LicensePerLibraryCriteria criteria) {
        Specification<LicensePerLibrary> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), LicensePerLibrary_.id));
            }
            if (criteria.getOrderId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOrderId(), LicensePerLibrary_.orderId));
            }
            if (criteria.getLinkType() != null) {
                specification = specification.and(buildSpecification(criteria.getLinkType(), LicensePerLibrary_.linkType));
            }
            if (criteria.getLicenseId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLicenseId(),
                            root -> root.join(LicensePerLibrary_.license, JoinType.LEFT).get(License_.id)
                        )
                    );
            }
            if (criteria.getLibraryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLibraryId(),
                            root -> root.join(LicensePerLibrary_.library, JoinType.LEFT).get(Library_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
