package com.agricultecommerce.service;

import com.agricultecommerce.entity.Product;
import com.agricultecommerce.exception.ResourceNotFoundException;
import com.agricultecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Page<Product> getProducts(Integer page, Integer size, Long categoryId, String search, String sortBy) {
        Pageable pageable;
        if (sortBy != null && !sortBy.isEmpty()) {
            String[] sortParams = sortBy.split(",");
            String field = sortParams[0];
            Sort.Direction direction = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
            pageable = PageRequest.of(page, size, Sort.by(direction, field));
        } else {
            pageable = PageRequest.of(page, size);
        }
        return productRepository.findProducts(categoryId, search, pageable);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }
}
