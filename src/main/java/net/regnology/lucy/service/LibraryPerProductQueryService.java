package net.regnology.lucy.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import net.regnology.lucy.domain.LibraryPerProduct;
import net.regnology.lucy.domain.LibraryPerProduct_;
import net.regnology.lucy.domain.Library_;
import net.regnology.lucy.domain.Product_;
import net.regnology.lucy.repository.LibraryPerProductRepository;
import net.regnology.lucy.service.criteria.LibraryPerProductCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link LibraryPerProduct} entities in the database.
 * The main input is a {@link LibraryPerProductCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LibraryPerProduct} or a {@link Page} of {@link LibraryPerProduct} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LibraryPerProductQueryService extends QueryService<LibraryPerProduct> {

    private final Logger log = LoggerFactory.getLogger(LibraryPerProductQueryService.class);

    private final LibraryPerProductRepository libraryPerProductRepository;

    public LibraryPerProductQueryService(LibraryPerProductRepository libraryPerProductRepository) {
        this.libraryPerProductRepository = libraryPerProductRepository;
    }

    /**
     * Return a {@link List} of {@link LibraryPerProduct} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LibraryPerProduct> findByCriteria(LibraryPerProductCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<LibraryPerProduct> specification = createSpecification(criteria);
        return libraryPerProductRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link LibraryPerProduct} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LibraryPerProduct> findByCriteria(LibraryPerProductCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<LibraryPerProduct> specification = createSpecification(criteria);
        return libraryPerProductRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LibraryPerProductCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<LibraryPerProduct> specification = createSpecification(criteria);
        return libraryPerProductRepository.count(specification);
    }

    /**
     * Function to convert {@link LibraryPerProductCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<LibraryPerProduct> createSpecification(LibraryPerProductCriteria criteria) {
        Specification<LibraryPerProduct> specification = Specification.where(null);
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
        }
        return specification;
    }
}
