package com.agricultecommerce.service;

import com.agricultecommerce.entity.Cart;
import com.agricultecommerce.entity.CartItem;
import com.agricultecommerce.entity.Product;
import com.agricultecommerce.entity.User;
import com.agricultecommerce.exception.BadRequestException;
import com.agricultecommerce.exception.ResourceNotFoundException;
import com.agricultecommerce.repository.CartItemRepository;
import com.agricultecommerce.repository.CartRepository;
import com.agricultecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getCartByUser(User user) {
        return cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setCartItems(new HashSet<>());
            return cartRepository.save(cart);
        });
    }

    @Transactional
    public CartItem addItemToCart(User user, Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }
        
        Cart cart = getCartByUser(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (product.getStock() < quantity) {
            throw new BadRequestException("Insufficient stock available");
        }

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            if (product.getStock() < newQuantity) {
                throw new BadRequestException("Insufficient stock available");
            }
            item.setQuantity(newQuantity);
            item.setPrice(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
            return cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            return cartItemRepository.save(item);
        }
    }

    @Transactional
    public void updateCartItemQuantity(Long itemId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }
        
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        
        Product product = item.getProduct();
        if (product.getStock() < quantity) {
            throw new BadRequestException("Insufficient stock available");
        }
        
        item.setQuantity(quantity);
        item.setPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        cartItemRepository.save(item);
    }

    public void removeItemFromCart(Long itemId) {
        if (!cartItemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Cart item not found");
        }
        cartItemRepository.deleteById(itemId);
    }
}
