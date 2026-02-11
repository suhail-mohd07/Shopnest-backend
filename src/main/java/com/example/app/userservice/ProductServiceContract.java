package com.example.app.userservice;

import java.util.List;
import com.example.app.entities.Product;

public interface ProductServiceContract {

    List<Product> getAllProducts();

    List<Product> getProductsByCategory(int categoryId);
}
