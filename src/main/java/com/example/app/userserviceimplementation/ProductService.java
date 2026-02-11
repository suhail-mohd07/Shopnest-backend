package com.example.app.userserviceimplementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.app.entities.Product;
import com.example.app.repositories.ProductRepository;
import com.example.app.userservice.ProductServiceContract;

@Service
public class ProductService implements ProductServiceContract {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(int categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
}
