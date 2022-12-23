package net.regnology.lucy.service;

import net.regnology.lucy.domain.LibraryErrorLog;
import net.regnology.lucy.repository.LibraryErrorLogCustomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom service implementation for managing {@link LibraryErrorLog}.
 */
@Service
@Transactional
public class LibraryErrorLogCustomService extends LibraryErrorLogService {

    public LibraryErrorLogCustomService(LibraryErrorLogCustomRepository libraryErrorLogRepository) {
        super(libraryErrorLogRepository);
    }
}
