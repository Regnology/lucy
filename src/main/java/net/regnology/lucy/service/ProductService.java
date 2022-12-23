package net.regnology.lucy.service;

import java.util.Optional;
import net.regnology.lucy.domain.Product;
import net.regnology.lucy.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Product}.
 */
@Service
@Transactional
public class ProductService {

    private final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Save a product.
     *
     * @param product the entity to save.
     * @return the persisted entity.
     */
    public Product save(Product product) {
        log.debug("Request to save Product : {}", product);
        return productRepository.save(product);
    }

    /**
     * Partially update a product.
     *
     * @param product the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Product> partialUpdate(Product product) {
        log.debug("Request to partially update Product : {}", product);

        return productRepository
            .findById(product.getId())
            .map(existingProduct -> {
                if (product.getName() != null) {
                    existingProduct.setName(product.getName());
                }
                if (product.getIdentifier() != null) {
                    existingProduct.setIdentifier(product.getIdentifier());
                }
                if (product.getVersion() != null) {
                    existingProduct.setVersion(product.getVersion());
                }
                if (product.getCreatedDate() != null) {
                    existingProduct.setCreatedDate(product.getCreatedDate());
                }
                if (product.getLastUpdatedDate() != null) {
                    existingProduct.setLastUpdatedDate(product.getLastUpdatedDate());
                }
                if (product.getTargetUrl() != null) {
                    existingProduct.setTargetUrl(product.getTargetUrl());
                }
                if (product.getUploadState() != null) {
                    existingProduct.setUploadState(product.getUploadState());
                }
                if (product.getDisclaimer() != null) {
                    existingProduct.setDisclaimer(product.getDisclaimer());
                }
                if (product.getDelivered() != null) {
                    existingProduct.setDelivered(product.getDelivered());
                }
                if (product.getDeliveredDate() != null) {
                    existingProduct.setDeliveredDate(product.getDeliveredDate());
                }
                if (product.getContact() != null) {
                    existingProduct.setContact(product.getContact());
                }
                if (product.getComment() != null) {
                    existingProduct.setComment(product.getComment());
                }
                if (product.getPreviousProductId() != null) {
                    existingProduct.setPreviousProductId(product.getPreviousProductId());
                }
                if (product.getUploadFilter() != null) {
                    existingProduct.setUploadFilter(product.getUploadFilter());
                }

                return existingProduct;
            })
            .map(productRepository::save);
    }

    /**
     * Get all the products.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Product> findAll(Pageable pageable) {
        log.debug("Request to get all Products");
        return productRepository.findAll(pageable);
    }

    /**
     * Get one product by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Product> findOne(Long id) {
        log.debug("Request to get Product : {}", id);
        return productRepository.findById(id);
    }

    /**
     * Delete the product by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Product : {}", id);
        productRepository.deleteById(id);
    }
}
