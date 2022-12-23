package net.regnology.lucy.repository;

import java.util.List;
import java.util.Optional;
import net.regnology.lucy.domain.Library;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Library entity.
 */
@Repository
public interface LibraryRepository
    extends LibraryRepositoryWithBagRelationships, JpaRepository<Library, Long>, JpaSpecificationExecutor<Library> {
    @Query("select library from Library library where library.lastReviewedBy.login = ?#{principal.username}")
    List<Library> findByLastReviewedByIsCurrentUser();

    default Optional<Library> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Library> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Library> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct library from Library library left join fetch library.lastReviewedBy",
        countQuery = "select count(distinct library) from Library library"
    )
    Page<Library> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct library from Library library left join fetch library.lastReviewedBy")
    List<Library> findAllWithToOneRelationships();

    @Query("select library from Library library left join fetch library.lastReviewedBy where library.id =:id")
    Optional<Library> findOneWithToOneRelationships(@Param("id") Long id);
}
