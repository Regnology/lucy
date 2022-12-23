package net.regnology.lucy.repository;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import net.regnology.lucy.domain.Library;
import net.regnology.lucy.domain.LibraryPerProduct;
import net.regnology.lucy.domain.statistics.CountOccurrences;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Custom Spring Data SQL repository for the LibraryPerProduct entity.
 */
@Repository
public interface LibraryPerProductCustomRepository extends LibraryPerProductRepository {
    @Query(value = "select distinct lpp from LibraryPerProduct lpp where lpp.product.id = :productId")
    List<LibraryPerProduct> findAllByProductId(@Param("productId") Long productId);

    @Query(
        value = "select distinct lpp.library from LibraryPerProduct lpp where lpp.product.id = :productId order by lpp.library.artifactId"
    )
    List<Library> findAllLibrariesByProductId(@Param("productId") Long productId);

    @Query(
        value = "select distinct lpp from LibraryPerProduct lpp where lpp.product.id = :productId",
        countQuery = "select count(distinct lpp.id) from LibraryPerProduct lpp where lpp.product.id = :productId"
    )
    Page<LibraryPerProduct> findAllByProductId(Pageable pageable, @Param("productId") Long productId);

    @Query("select lpp from LibraryPerProduct lpp where lpp.product.id = :productId and lpp.library.id = :libraryId")
    Optional<LibraryPerProduct> findByProductIdAndLibraryId(@Param("productId") Long productId, @Param("libraryId") Long libraryId);

    @Query(
        "select new net.regnology.lucy.domain.statistics.CountOccurrences(licenses.license.shortIdentifier, count(*)) " +
        "from LibraryPerProduct lpp left join lpp.library.licenses licenses where lpp.product.id = :productId " +
        "group by licenses.license.shortIdentifier order by count(*) desc"
    )
    List<CountOccurrences> countDistributedLicensesByProductId(@Param("productId") Long productId);

    /*
        SELECT license.short_identifier, COUNT(license.short_identifier) FROM LIBRARY_PER_PRODUCT
LEFT JOIN LIBRARY
ON library_per_product.library_id= library.id
LEFT JOIN LICENSE_PER_LIBRARY
ON library.id = license_per_library.library_id
LEFT JOIN LICENSE
ON license_per_library.license_id = license.id
WHERE library_per_product.product_id = '19'
GROUP BY license.short_identifier
;
         */

    @Query("select count(distinct lpp) from LibraryPerProduct lpp where lpp.product.id = :productId")
    long countLibrariesByProductId(@Param("productId") Long productId);

    @Query("select count(distinct lpp) from LibraryPerProduct lpp where lpp.product.id = :productId and lpp.library.reviewed = true")
    long countReviewedLibrariesByProductId(@Param("productId") Long productId);

    @Query("select distinct lpp from LibraryPerProduct lpp where lpp.product.id = :productId and lpp.addedManually = true")
    List<LibraryPerProduct> findLibraryPerProductsByProductIdAndAddedManuallyIsTrue(@Param("productId") Long productId);

    @Query(
        "select lpp1.library from LibraryPerProduct lpp1 where lpp1.product.id = :firstProductId and not exists " +
        "(select lpp2.library from LibraryPerProduct lpp2 where lpp2.product.id = :secondProductId and lpp2.library.id = lpp1.library.id)"
    )
    List<Library> onlyLibrariesFromFirstProductWithoutIntersection(
        @Param("firstProductId") Long firstProductId,
        @Param("secondProductId") Long secondProductId
    );

    @Query(
        "select lpp1.library from LibraryPerProduct lpp1 where lpp1.product.id = :firstProductId and exists " +
        "(select lpp2.library from LibraryPerProduct lpp2 where lpp2.product.id = :secondProductId and lpp2.library.id = lpp1.library.id)"
    )
    List<Library> libraryIntersectionOfProducts(
        @Param("firstProductId") Long firstProductId,
        @Param("secondProductId") Long secondProductId
    );

    @Transactional
    @Modifying
    @Query("delete from LibraryPerProduct lpp where lpp.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);

    @Transactional
    @Modifying
    @Query("delete from LibraryPerProduct lpp where lpp.product.id = :productId and lpp.addedManually = false")
    void deleteByProductIdAndNotAddedManually(@Param("productId") Long productId);
}
