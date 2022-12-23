package net.regnology.lucy.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import net.regnology.lucy.domain.LibraryErrorLog;
import net.regnology.lucy.domain.LibraryErrorLog_;
import net.regnology.lucy.domain.Library_;
import net.regnology.lucy.repository.LibraryErrorLogRepository;
import net.regnology.lucy.service.criteria.LibraryErrorLogCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link LibraryErrorLog} entities in the database.
 * The main input is a {@link LibraryErrorLogCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LibraryErrorLog} or a {@link Page} of {@link LibraryErrorLog} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LibraryErrorLogQueryService extends QueryService<LibraryErrorLog> {

    private final Logger log = LoggerFactory.getLogger(LibraryErrorLogQueryService.class);

    private final LibraryErrorLogRepository libraryErrorLogRepository;

    public LibraryErrorLogQueryService(LibraryErrorLogRepository libraryErrorLogRepository) {
        this.libraryErrorLogRepository = libraryErrorLogRepository;
    }

    /**
     * Return a {@link List} of {@link LibraryErrorLog} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LibraryErrorLog> findByCriteria(LibraryErrorLogCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<LibraryErrorLog> specification = createSpecification(criteria);
        return libraryErrorLogRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link LibraryErrorLog} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LibraryErrorLog> findByCriteria(LibraryErrorLogCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<LibraryErrorLog> specification = createSpecification(criteria);
        return libraryErrorLogRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LibraryErrorLogCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<LibraryErrorLog> specification = createSpecification(criteria);
        return libraryErrorLogRepository.count(specification);
    }

    /**
     * Function to convert {@link LibraryErrorLogCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<LibraryErrorLog> createSpecification(LibraryErrorLogCriteria criteria) {
        Specification<LibraryErrorLog> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), LibraryErrorLog_.id));
            }
            if (criteria.getMessage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMessage(), LibraryErrorLog_.message));
            }
            if (criteria.getSeverity() != null) {
                specification = specification.and(buildSpecification(criteria.getSeverity(), LibraryErrorLog_.severity));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), LibraryErrorLog_.status));
            }
            if (criteria.getTimestamp() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTimestamp(), LibraryErrorLog_.timestamp));
            }
            if (criteria.getLibraryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLibraryId(),
                            root -> root.join(LibraryErrorLog_.library, JoinType.LEFT).get(Library_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
