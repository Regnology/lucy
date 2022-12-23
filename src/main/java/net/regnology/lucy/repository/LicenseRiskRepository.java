package net.regnology.lucy.repository;

import net.regnology.lucy.domain.LicenseRisk;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LicenseRisk entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LicenseRiskRepository extends JpaRepository<LicenseRisk, Long> {}
