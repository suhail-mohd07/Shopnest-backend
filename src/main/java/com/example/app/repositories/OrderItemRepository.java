package com.example.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.app.entities.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
}
