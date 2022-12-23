package net.regnology.lucy.service;

import java.util.Optional;
import net.regnology.lucy.domain.LibraryErrorLog;
import net.regnology.lucy.repository.LibraryErrorLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link LibraryErrorLog}.
 */
@Service
@Transactional
public class LibraryErrorLogService {

    private final Logger log = LoggerFactory.getLogger(LibraryErrorLogService.class);

    private final LibraryErrorLogRepository libraryErrorLogRepository;

    public LibraryErrorLogService(LibraryErrorLogRepository libraryErrorLogRepository) {
        this.libraryErrorLogRepository = libraryErrorLogRepository;
    }

    /**
     * Save a libraryErrorLog.
     *
     * @param libraryErrorLog the entity to save.
     * @return the persisted entity.
     */
    public LibraryErrorLog save(LibraryErrorLog libraryErrorLog) {
        log.debug("Request to save LibraryErrorLog : {}", libraryErrorLog);
        return libraryErrorLogRepository.save(libraryErrorLog);
    }

    /**
     * Partially update a libraryErrorLog.
     *
     * @param libraryErrorLog the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LibraryErrorLog> partialUpdate(LibraryErrorLog libraryErrorLog) {
        log.debug("Request to partially update LibraryErrorLog : {}", libraryErrorLog);

        return libraryErrorLogRepository
            .findById(libraryErrorLog.getId())
            .map(existingLibraryErrorLog -> {
                if (libraryErrorLog.getMessage() != null) {
                    existingLibraryErrorLog.setMessage(libraryErrorLog.getMessage());
                }
                if (libraryErrorLog.getSeverity() != null) {
                    existingLibraryErrorLog.setSeverity(libraryErrorLog.getSeverity());
                }
                if (libraryErrorLog.getStatus() != null) {
                    existingLibraryErrorLog.setStatus(libraryErrorLog.getStatus());
                }
                if (libraryErrorLog.getTimestamp() != null) {
                    existingLibraryErrorLog.setTimestamp(libraryErrorLog.getTimestamp());
                }

                return existingLibraryErrorLog;
            })
            .map(libraryErrorLogRepository::save);
    }

    /**
     * Get all the libraryErrorLogs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LibraryErrorLog> findAll(Pageable pageable) {
        log.debug("Request to get all LibraryErrorLogs");
        return libraryErrorLogRepository.findAll(pageable);
    }

    /**
     * Get one libraryErrorLog by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LibraryErrorLog> findOne(Long id) {
        log.debug("Request to get LibraryErrorLog : {}", id);
        return libraryErrorLogRepository.findById(id);
    }

    /**
     * Delete the libraryErrorLog by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete LibraryErrorLog : {}", id);
        libraryErrorLogRepository.deleteById(id);
    }
}
