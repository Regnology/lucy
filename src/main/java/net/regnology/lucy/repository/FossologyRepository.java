package net.regnology.lucy.repository;

import net.regnology.lucy.domain.Fossology;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Fossology entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FossologyRepository extends JpaRepository<Fossology, Long> {}
