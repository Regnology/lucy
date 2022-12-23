package net.regnology.lucy.service;

import java.util.List;
import net.regnology.lucy.domain.Product;
import net.regnology.lucy.repository.ProductCustomRepository;
import net.regnology.lucy.service.criteria.ProductCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom service for executing complex queries for {@link Product} entities in the database.
 * The main input is a {@link ProductCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Product} or a {@link Page} of {@link Product} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductQueryCustomService extends ProductQueryService {

    public ProductQueryCustomService(ProductCustomRepository productRepository) {
        super(productRepository);
    }
}
