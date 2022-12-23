package net.regnology.lucy.repository;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import net.regnology.lucy.domain.Library;
import org.hibernate.annotations.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class LibraryRepositoryWithBagRelationshipsImpl implements LibraryRepositoryWithBagRelationships {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Optional<Library> fetchBagRelationships(Optional<Library> library) {
        return library.map(this::fetchLicenseToPublishes).map(this::fetchLicenseOfFiles);
    }

    @Override
    public Page<Library> fetchBagRelationships(Page<Library> libraries) {
        return new PageImpl<>(fetchBagRelationships(libraries.getContent()), libraries.getPageable(), libraries.getTotalElements());
    }

    @Override
    public List<Library> fetchBagRelationships(List<Library> libraries) {
        return Optional.of(libraries).map(this::fetchLicenseToPublishes).map(this::fetchLicenseOfFiles).get();
    }

    Library fetchLicenseToPublishes(Library result) {
        return entityManager
            .createQuery(
                "select library from Library library left join fetch library.licenseToPublishes where library is :library",
                Library.class
            )
            .setParameter("library", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Library> fetchLicenseToPublishes(List<Library> libraries) {
        return entityManager
            .createQuery(
                "select distinct library from Library library left join fetch library.licenseToPublishes where library in :libraries",
                Library.class
            )
            .setParameter("libraries", libraries)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }

    Library fetchLicenseOfFiles(Library result) {
        return entityManager
            .createQuery(
                "select library from Library library left join fetch library.licenseOfFiles where library is :library",
                Library.class
            )
            .setParameter("library", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Library> fetchLicenseOfFiles(List<Library> libraries) {
        return entityManager
            .createQuery(
                "select distinct library from Library library left join fetch library.licenseOfFiles where library in :libraries",
                Library.class
            )
            .setParameter("libraries", libraries)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
