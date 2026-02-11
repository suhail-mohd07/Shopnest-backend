package com.example.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.app.entities.Order;

public interface OrderRepository extends JpaRepository<Order, String> {
}
