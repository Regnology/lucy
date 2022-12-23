package net.regnology.lucy.repository;

import net.regnology.lucy.domain.User;
import org.springframework.stereotype.Repository;

/**
 * Custom Spring Data JPA repository for the {@link User} entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserCustomRepository extends UserRepository {}
