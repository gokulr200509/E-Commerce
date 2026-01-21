package com.agricultecommerce.service;

import com.agricultecommerce.entity.Cart;
import com.agricultecommerce.entity.CartItem;
import com.agricultecommerce.entity.Order;
import com.agricultecommerce.entity.OrderItem;
import com.agricultecommerce.entity.Product;
import com.agricultecommerce.entity.User;
import com.agricultecommerce.exception.BadRequestException;
import com.agricultecommerce.exception.ResourceNotFoundException;
import com.agricultecommerce.repository.CartRepository;
import com.agricultecommerce.repository.OrderItemRepository;
import com.agricultecommerce.repository.OrderRepository;
import com.agricultecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order createOrderFromCart(User user, String shippingAddress) {
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new BadRequestException("Shipping address is required");
        }
        
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);
        order.setShippingAddress(shippingAddress);

        Set<OrderItem> orderItems = new HashSet<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new BadRequestException("Insufficient stock for " + product.getName());
            }
            
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            orderItems.add(orderItem);
            total = total.add(orderItem.getPrice());
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        cart.getCartItems().clear();
        cartRepository.save(cart);
        
        return savedOrder;
    }

    @Transactional
    public Order buyNow(User user, Long productId, Integer quantity, String shippingAddress) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }
        
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new BadRequestException("Shipping address is required");
        }
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (product.getStock() < quantity) {
            throw new BadRequestException("Insufficient stock for " + product.getName());
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);
        order.setShippingAddress(shippingAddress);

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        order.setOrderItems(Set.of(item));
        order.setTotalAmount(item.getPrice());

        Order savedOrder = orderRepository.save(order);
        orderItemRepository.save(item);
        
        return savedOrder;
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserId(user.getId());
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order updateOrderStatus(Long id, Order.Status status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
