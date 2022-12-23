package net.regnology.lucy.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import net.regnology.lucy.domain.*;
import net.regnology.lucy.repository.LibraryRepository;
import net.regnology.lucy.service.criteria.LibraryCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Library} entities in the database.
 * The main input is a {@link LibraryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Library} or a {@link Page} of {@link Library} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LibraryQueryService extends QueryService<Library> {

    private final Logger log = LoggerFactory.getLogger(LibraryQueryService.class);

    private final LibraryRepository libraryRepository;

    public LibraryQueryService(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }

    /**
     * Return a {@link List} of {@link Library} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Library> findByCriteria(LibraryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Library> specification = createSpecification(criteria);
        return libraryRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Library} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Library> findByCriteria(LibraryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Library> specification = createSpecification(criteria);
        return libraryRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LibraryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Library> specification = createSpecification(criteria);
        return libraryRepository.count(specification);
    }

    /**
     * Function to convert {@link LibraryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Library> createSpecification(LibraryCriteria criteria) {
        Specification<Library> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Library_.id));
            }
            if (criteria.getGroupId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getGroupId(), Library_.groupId));
            }
            if (criteria.getArtifactId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getArtifactId(), Library_.artifactId));
            }
            if (criteria.getVersion() != null) {
                specification = specification.and(buildStringSpecification(criteria.getVersion(), Library_.version));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), Library_.type));
            }
            if (criteria.getOriginalLicense() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOriginalLicense(), Library_.originalLicense));
            }
            if (criteria.getLicenseUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLicenseUrl(), Library_.licenseUrl));
            }
            if (criteria.getSourceCodeUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSourceCodeUrl(), Library_.sourceCodeUrl));
            }
            if (criteria.getpUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getpUrl(), Library_.pUrl));
            }
            if (criteria.getCopyright() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCopyright(), Library_.copyright));
            }
            if (criteria.getCompliance() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCompliance(), Library_.compliance));
            }
            if (criteria.getComplianceComment() != null) {
                specification = specification.and(buildStringSpecification(criteria.getComplianceComment(), Library_.complianceComment));
            }
            if (criteria.getComment() != null) {
                specification = specification.and(buildStringSpecification(criteria.getComment(), Library_.comment));
            }
            if (criteria.getReviewed() != null) {
                specification = specification.and(buildSpecification(criteria.getReviewed(), Library_.reviewed));
            }
            if (criteria.getReviewedDeepScan() != null) {
                specification = specification.and(buildSpecification(criteria.getReviewedDeepScan(), Library_.reviewedDeepScan));
            }
            if (criteria.getLastReviewedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastReviewedDate(), Library_.lastReviewedDate));
            }
            if (criteria.getLastReviewedDeepScanDate() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getLastReviewedDeepScanDate(), Library_.lastReviewedDeepScanDate));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Library_.createdDate));
            }
            if (criteria.getHideForPublishing() != null) {
                specification = specification.and(buildSpecification(criteria.getHideForPublishing(), Library_.hideForPublishing));
            }
            if (criteria.getMd5() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMd5(), Library_.md5));
            }
            if (criteria.getSha1() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSha1(), Library_.sha1));
            }
            if (criteria.getLicenseId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLicenseId(),
                            root -> root.join(Library_.licenses, JoinType.LEFT).get(LicensePerLibrary_.id)
                        )
                    );
            }
            if (criteria.getErrorLogId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getErrorLogId(),
                            root -> root.join(Library_.errorLogs, JoinType.LEFT).get(LibraryErrorLog_.id)
                        )
                    );
            }
            if (criteria.getLastReviewedById() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLastReviewedById(),
                            root -> root.join(Library_.lastReviewedBy, JoinType.LEFT).get(User_.id)
                        )
                    );
            }
            if (criteria.getLastReviewedDeepScanById() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLastReviewedDeepScanById(),
                            root -> root.join(Library_.lastReviewedDeepScanBy, JoinType.LEFT).get(User_.id)
                        )
                    );
            }
            if (criteria.getLicenseToPublishId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLicenseToPublishId(),
                            root -> root.join(Library_.licenseToPublishes, JoinType.LEFT).get(License_.id)
                        )
                    );
            }
            if (criteria.getLicenseOfFilesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLicenseOfFilesId(),
                            root -> root.join(Library_.licenseOfFiles, JoinType.LEFT).get(License_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
