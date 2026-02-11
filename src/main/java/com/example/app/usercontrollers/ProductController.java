package com.example.app.usercontrollers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.app.entities.Product;
import com.example.app.userservice.ProductServiceContract;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ProductController {

    private final ProductServiceContract productService;

    public ProductController(ProductServiceContract productService) {
        this.productService = productService;
    }

    // ✅ Get all products
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // ✅ Get products by category
    @GetMapping("/category/{id}")
    public List<Product> getByCategory(@PathVariable int id) {
        return productService.getProductsByCategory(id);
    }
}
