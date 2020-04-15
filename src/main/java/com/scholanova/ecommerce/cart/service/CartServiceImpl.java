package com.scholanova.ecommerce.cart.service;

import com.scholanova.ecommerce.cart.entity.Cart;
import com.scholanova.ecommerce.cart.exception.CartException;
import com.scholanova.ecommerce.product.repository.ProductRepository;
import com.scholanova.ecommerce.product.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CartServiceImpl implements CartService{

    private ProductRepository productRepository;

    public CartServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Cart addProductToCart(Cart cart, Long productId, int quantity) throws CartException {
        try {
            Product product = productRepository.findById(productId).orElseThrow(() -> new CartException("Impossible d'ajouter le produit au panier"));
            return cart.addProduct(product, quantity);
        } catch ( Exception e) {
            throw new CartException(e.getMessage());
        }
    }

    @Override
    public Cart changeProductQuantity(Cart cart, Long productId, int quantity) throws CartException {
        try {
            Product product = productRepository.findById(productId).orElseThrow(() -> new CartException("Impossible de changer la quantité"));
            return cart.changeProductQuantity(product, quantity);
        } catch ( Exception e ) {
            throw new CartException(e.getMessage());
        }
    }
}
