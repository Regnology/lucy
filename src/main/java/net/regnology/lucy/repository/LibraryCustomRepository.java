package net.regnology.lucy.repository;

import java.util.List;
import java.util.Optional;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.domain.License;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Custom Spring Data SQL repository for the Library entity.
 */
@Repository
public interface LibraryCustomRepository extends LibraryRepository, LibraryRepositoryWithBagRelationships {
    @Query(
        value = "select distinct library from Library library left join fetch library.licenseToPublishes left join fetch library.licenseOfFiles",
        countQuery = "select count(distinct library) from Library library"
    )
    Page<Library> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct library from Library library left join fetch library.licenseToPublishes left join fetch library.licenseOfFiles")
    List<Library> findAllWithEagerRelationships();

    @Query(
        "select library from Library library left join fetch library.licenseToPublishes left join fetch library.licenseOfFiles where library.id =:id"
    )
    Optional<Library> findOneWithEagerRelationships(@Param("id") Long id);

    @Query(
        "select library from Library library where lower(library.groupId) = lower(:groupId) and lower(library.artifactId) = lower(:artifactId) and lower(library.version) = lower(:version)"
    )
    Optional<Library> findByGroupIdAndArtifactIdAndVersion(
        @Param("groupId") String groupId,
        @Param("artifactId") String artifactId,
        @Param("version") String version
    );

    @Query(
        "select distinct library from Library library where library.licenseUrl is null or library.licenseUrl = '' or library.sourceCodeUrl is null or library.sourceCodeUrl = ''"
    )
    List<Library> findAllWhereUrlIsEmpty();

    @Query("select distinct library from Library library where library.md5 = lower(:hash) or library.sha1 = lower(:hash)")
    List<Library> findByHash(@Param("hash") String hash);

    List<Library> findByCopyrightNull();

    @Query(
        "select distinct library from Library library left join fetch library.licenseOfFiles " +
        "where :license member of library.licenseToPublishes or :license member of library.licenseOfFiles"
    )
    List<Library> findAllByLicenseToPublishAndLicenseOfFiles(@Param("license") License license);
}
