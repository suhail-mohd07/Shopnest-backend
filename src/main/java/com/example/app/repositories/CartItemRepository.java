package com.example.app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.entities.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    Optional<CartItem> findByUserIdAndProductId(Integer userId, Integer productId);

    List<CartItem> findByUserId(Integer userId);

    long countByUserId(Integer userId);

    // ✅ ADD THIS
    @Transactional
    void deleteByUserIdAndProductId(Integer userId, Integer productId);
    
    @Transactional
    void deleteByUserId(Integer userId);
}