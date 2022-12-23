package net.regnology.lucy.repository;

import net.regnology.lucy.domain.LibraryErrorLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LibraryErrorLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LibraryErrorLogRepository extends JpaRepository<LibraryErrorLog, Long>, JpaSpecificationExecutor<LibraryErrorLog> {}
