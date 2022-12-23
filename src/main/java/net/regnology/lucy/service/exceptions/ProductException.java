package net.regnology.lucy.service.exceptions;

import net.regnology.lucy.domain.Product;

public class ProductException extends Exception {

    private static final long serialVersionUID = 1L;
    private Product product;

    public ProductException(String message) {
        super(message);
    }

    public ProductException(String message, Product product) {
        super(message);
        this.product = product;
    }

    public Product getProduct() {
        return this.product;
    }
}
