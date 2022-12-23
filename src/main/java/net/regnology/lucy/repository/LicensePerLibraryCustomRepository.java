package net.regnology.lucy.repository;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Custom Spring Data SQL repository for the LicensePerLibrary entity.
 */
@Repository
public interface LicensePerLibraryCustomRepository extends LicensePerLibraryRepository {
    @Transactional
    @Modifying
    @Query("delete from LicensePerLibrary lpl where lpl.library.id = :libraryId")
    void deleteByLibraryId(@Param("libraryId") Long libraryId);
}
