package net.regnology.lucy.repository;

import java.util.Optional;
import net.regnology.lucy.domain.Requirement;
import org.springframework.stereotype.Repository;

/**
 * Custom Spring Data SQL repository for the Requirement entity.
 */
@Repository
public interface RequirementCustomRepository extends RequirementRepository {
    Optional<Requirement> findOneByShortText(String shortText);
}
