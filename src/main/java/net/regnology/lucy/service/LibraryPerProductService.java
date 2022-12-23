package net.regnology.lucy.service;

import java.util.Optional;
import net.regnology.lucy.domain.LibraryPerProduct;
import net.regnology.lucy.repository.LibraryPerProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link LibraryPerProduct}.
 */
@Service
@Transactional
public class LibraryPerProductService {

    private final Logger log = LoggerFactory.getLogger(LibraryPerProductService.class);

    private final LibraryPerProductRepository libraryPerProductRepository;

    public LibraryPerProductService(LibraryPerProductRepository libraryPerProductRepository) {
        this.libraryPerProductRepository = libraryPerProductRepository;
    }

    /**
     * Save a libraryPerProduct.
     *
     * @param libraryPerProduct the entity to save.
     * @return the persisted entity.
     */
    public LibraryPerProduct save(LibraryPerProduct libraryPerProduct) {
        log.debug("Request to save LibraryPerProduct : {}", libraryPerProduct);
        return libraryPerProductRepository.save(libraryPerProduct);
    }

    /**
     * Partially update a libraryPerProduct.
     *
     * @param libraryPerProduct the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LibraryPerProduct> partialUpdate(LibraryPerProduct libraryPerProduct) {
        log.debug("Request to partially update LibraryPerProduct : {}", libraryPerProduct);

        return libraryPerProductRepository
            .findById(libraryPerProduct.getId())
            .map(existingLibraryPerProduct -> {
                if (libraryPerProduct.getAddedDate() != null) {
                    existingLibraryPerProduct.setAddedDate(libraryPerProduct.getAddedDate());
                }
                if (libraryPerProduct.getAddedManually() != null) {
                    existingLibraryPerProduct.setAddedManually(libraryPerProduct.getAddedManually());
                }
                if (libraryPerProduct.getHideForPublishing() != null) {
                    existingLibraryPerProduct.setHideForPublishing(libraryPerProduct.getHideForPublishing());
                }
                if (libraryPerProduct.getComment() != null) {
                    existingLibraryPerProduct.setComment(libraryPerProduct.getComment());
                }

                return existingLibraryPerProduct;
            })
            .map(libraryPerProductRepository::save);
    }

    /**
     * Get all the libraryPerProducts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LibraryPerProduct> findAll(Pageable pageable) {
        log.debug("Request to get all LibraryPerProducts");
        return libraryPerProductRepository.findAll(pageable);
    }

    /**
     * Get one libraryPerProduct by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LibraryPerProduct> findOne(Long id) {
        log.debug("Request to get LibraryPerProduct : {}", id);
        return libraryPerProductRepository.findById(id);
    }

    /**
     * Delete the libraryPerProduct by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete LibraryPerProduct : {}", id);
        libraryPerProductRepository.deleteById(id);
    }
}
