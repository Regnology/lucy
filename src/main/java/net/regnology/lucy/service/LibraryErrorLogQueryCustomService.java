package net.regnology.lucy.service;

import java.util.List;
import net.regnology.lucy.domain.LibraryErrorLog;
import net.regnology.lucy.repository.LibraryErrorLogCustomRepository;
import net.regnology.lucy.service.criteria.LibraryErrorLogCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom service for executing complex queries for {@link LibraryErrorLog} entities in the database.
 * The main input is a {@link LibraryErrorLogCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LibraryErrorLog} or a {@link Page} of {@link LibraryErrorLog} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LibraryErrorLogQueryCustomService extends LibraryErrorLogQueryService {

    public LibraryErrorLogQueryCustomService(LibraryErrorLogCustomRepository libraryErrorLogRepository) {
        super(libraryErrorLogRepository);
    }
}
