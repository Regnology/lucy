package net.regnology.lucy.repository;

import net.regnology.lucy.domain.Upload;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Upload entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UploadRepository extends JpaRepository<Upload, Long> {}
