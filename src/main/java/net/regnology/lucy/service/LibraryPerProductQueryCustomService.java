package net.regnology.lucy.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import net.regnology.lucy.domain.*;
import net.regnology.lucy.repository.LibraryPerProductCustomRepository;
import net.regnology.lucy.service.criteria.LibraryPerProductCustomCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom service for executing complex queries for {@link LibraryPerProduct} entities in the database.
 * The main input is a {@link LibraryPerProductCustomCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LibraryPerProduct} or a {@link Page} of {@link LibraryPerProduct} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LibraryPerProductQueryCustomService extends LibraryPerProductQueryService {

    private final Logger log = LoggerFactory.getLogger(LibraryPerProductQueryCustomService.class);

    private final LibraryPerProductCustomRepository libraryPerProductRepository;

    public LibraryPerProductQueryCustomService(LibraryPerProductCustomRepository libraryPerProductRepository) {
        super(libraryPerProductRepository);
        this.libraryPerProductRepository = libraryPerProductRepository;
    }

    /**
     * Return a {@link List} of {@link LibraryPerProduct} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LibraryPerProduct> findByCriteria(LibraryPerProductCustomCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<LibraryPerProduct> specification = createSpecification(criteria);
        return libraryPerProductRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link LibraryPerProduct} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LibraryPerProduct> findByCriteria(LibraryPerProductCustomCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<LibraryPerProduct> specification = createSpecification(criteria);
        return libraryPerProductRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LibraryPerProductCustomCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<LibraryPerProduct> specification = createSpecification(criteria);
        return libraryPerProductRepository.count(specification);
    }

    /**
     * Function to convert {@link LibraryPerProductCustomCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<LibraryPerProduct> createSpecification(LibraryPerProductCustomCriteria criteria) {
        Specification<LibraryPerProduct> specification = Specification.where(null);

        /*
        Query returns duplicate rows for many to many. Solution is to initialise the specification with a distinct query
        !!! Solution is not working because distinct and orderBy is cannot work together, if the select statement
        doesn't contain the field to order by !!!
        */
        /*Specification<LibraryPerProduct> specification = (root, query, cb) -> {
            query.distinct(true);
            root.fetch(LibraryPerProduct_.library, JoinType.LEFT);
            return null;
        };*/

        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), LibraryPerProduct_.id));
            }
            if (criteria.getAddedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAddedDate(), LibraryPerProduct_.addedDate));
            }
            if (criteria.getAddedManually() != null) {
                specification = specification.and(buildSpecification(criteria.getAddedManually(), LibraryPerProduct_.addedManually));
            }
            if (criteria.getHideForPublishing() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getHideForPublishing(), LibraryPerProduct_.hideForPublishing));
            }
            if (criteria.getComment() != null) {
                specification = specification.and(buildSpecification(criteria.getComment(), LibraryPerProduct_.comment));
            }
            if (criteria.getLibraryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLibraryId(),
                            root -> root.join(LibraryPerProduct_.library, JoinType.LEFT).get(Library_.id)
                        )
                    );
            }
            if (criteria.getProductId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProductId(),
                            root -> root.join(LibraryPerProduct_.product, JoinType.LEFT).get(Product_.id)
                        )
                    );
            }
            if (criteria.getArtifactId() != null) {
                if (criteria.getArtifactId().getContains() != null) {
                    String artifactId = "%" + criteria.getArtifactId().getContains() + "%";
                    specification =
                        specification.and((root, criteriaQuery, criteriaBuilder) ->
                            criteriaBuilder.or(
                                criteriaBuilder.like(
                                    root.join(LibraryPerProduct_.library, JoinType.LEFT).get(Library_.artifactId),
                                    artifactId
                                ),
                                criteriaBuilder.like(root.join(LibraryPerProduct_.library, JoinType.LEFT).get(Library_.groupId), artifactId)
                            )
                        );
                }
            }
            if (criteria.getLicensesShortIdentifier() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLicensesShortIdentifier(),
                            root ->
                                root
                                    .join(LibraryPerProduct_.library, JoinType.LEFT)
                                    .join(Library_.licenses, JoinType.LEFT)
                                    .get(LicensePerLibrary_.license)
                                    .get(License_.shortIdentifier)
                        )
                    );
            }
            if (criteria.getLibraryRiskId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLibraryRiskId(),
                            root ->
                                root
                                    .join(LibraryPerProduct_.library, JoinType.LEFT)
                                    .join(Library_.libraryRisk, JoinType.LEFT)
                                    .get(LicenseRisk_.id)
                        )
                    );
            }
            if (criteria.getErrorLogMessage() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getErrorLogMessage(),
                            root ->
                                root
                                    .join(LibraryPerProduct_.library, JoinType.LEFT)
                                    .join(Library_.errorLogs, JoinType.LEFT)
                                    .get(LibraryErrorLog_.message)
                        )
                    );
            }
            if (criteria.getErrorLogStatus() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getErrorLogStatus(),
                            root ->
                                root
                                    .join(LibraryPerProduct_.library, JoinType.LEFT)
                                    .join(Library_.errorLogs, JoinType.LEFT)
                                    .get(LibraryErrorLog_.status)
                        )
                    );
            }
            if (criteria.getLibraryCreatedDate() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLibraryCreatedDate(),
                            root -> root.join(LibraryPerProduct_.library, JoinType.LEFT).get(Library_.createdDate)
                        )
                    );
            }
        }

        return specification;
    }
}
