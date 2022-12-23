package net.regnology.lucy.repository;

import net.regnology.lucy.domain.GenericLicenseUrl;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the GenericLicenseUrl entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GenericLicenseUrlRepository extends JpaRepository<GenericLicenseUrl, Long> {}
