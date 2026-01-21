package com.agricultecommerce.controller;

import com.agricultecommerce.dto.OrderRequest;
import com.agricultecommerce.entity.Order;
import com.agricultecommerce.service.OrderService;
import com.agricultecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@AuthenticationPrincipal UserDetails userDetails,
                                                             @RequestBody OrderRequest request) {
        com.agricultecommerce.entity.User user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
        Order order = orderService.createOrderFromCart(user, request.getShippingAddress());
        return ResponseEntity.ok(Map.of(
            "message", "Order placed successfully",
            "orderId", order.getId(),
            "totalAmount", order.getTotalAmount(),
            "status", order.getStatus()
        ));
    }

    @PostMapping("/buy-now")
    public ResponseEntity<Map<String, Object>> buyNow(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestParam Long productId,
                                                       @RequestParam Integer quantity,
                                                       @RequestBody OrderRequest request) {
        com.agricultecommerce.entity.User user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
        Order order = orderService.buyNow(user, productId, quantity, request.getShippingAddress());
        return ResponseEntity.ok(Map.of(
            "message", "Order placed successfully",
            "orderId", order.getId(),
            "totalAmount", order.getTotalAmount(),
            "status", order.getStatus()
        ));
    }

    @GetMapping
    public List<Order> getUserOrders(@AuthenticationPrincipal UserDetails userDetails) {
        com.agricultecommerce.entity.User user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
        return orderService.getOrdersByUser(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestParam Order.Status status) {
        Order order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(order);
    }
}
