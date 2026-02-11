package com.example.app.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategoryId(int categoryId);
}
