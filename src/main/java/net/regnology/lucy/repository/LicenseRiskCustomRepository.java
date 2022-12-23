package net.regnology.lucy.repository;

import java.util.List;
import java.util.Optional;
import net.regnology.lucy.domain.LicenseRisk;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Custom Spring Data SQL repository for the LicenseRisk entity.
 */
@Repository
public interface LicenseRiskCustomRepository extends LicenseRiskRepository {
    Optional<LicenseRisk> findOneByName(String name);

    @Override
    @Query("select licenseRisk from LicenseRisk licenseRisk order by licenseRisk.level")
    List<LicenseRisk> findAll();
}
