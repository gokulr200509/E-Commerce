package com.agricultecommerce.controller;

import com.agricultecommerce.entity.Cart;
import com.agricultecommerce.entity.CartItem;
import com.agricultecommerce.entity.User;
import com.agricultecommerce.service.CartService;
import com.agricultecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
        Cart cart = cartService.getCartByUser(user);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addItem(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam Long productId, 
                                            @RequestParam Integer quantity) {
        User user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
        CartItem item = cartService.addItemToCart(user, productId, quantity);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<CartItem> updateItemQuantity(@PathVariable Long itemId,
                                                        @RequestParam Integer quantity) {
        cartService.updateCartItemQuantity(itemId, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long itemId) {
        cartService.removeItemFromCart(itemId);
        return ResponseEntity.noContent().build();
    }
}
