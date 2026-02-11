package com.example.app.userservice;

import java.util.List;

import com.example.app.entities.CartItem;

public interface CartServiceContract {
    void addToCart(Integer userId, Integer productId, Integer quantity);
    long getCartCount(Integer userId);
    List<CartItem> getCartItems(Integer userId);
    
   
    void updateCartItemQuantity(Integer userId, Integer productId, Integer quantity);

    void removeFromCart(Integer userId, Integer productId);


}
