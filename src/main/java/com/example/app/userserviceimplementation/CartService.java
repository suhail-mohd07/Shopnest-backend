package com.example.app.userserviceimplementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.app.entities.CartItem;
import com.example.app.repositories.CartItemRepository;
import com.example.app.userservice.CartServiceContract;

@Service
public class CartService implements CartServiceContract {

    private final CartItemRepository cartItemRepository;

    public CartService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public void addToCart(Integer userId, Integer productId, Integer quantity) {
        int qty = (quantity == null || quantity <= 0) ? 1 : quantity;

        CartItem item = cartItemRepository
                .findByUserIdAndProductId(userId, productId)
                .orElse(null);

        if (item == null) {
            cartItemRepository.save(new CartItem(userId, productId, qty));
        } else {
            item.setQuantity(item.getQuantity() + qty);
            cartItemRepository.save(item);
        }
    }

    @Override
    public long getCartCount(Integer userId) {
        return cartItemRepository.countByUserId(userId);
    }
    
    @Override
    public List<CartItem> getCartItems(Integer userId) {
        return cartItemRepository.findByUserId(userId);
    }
    
    public void updateCartItemQuantity(Integer userId, Integer productId, Integer quantity) {
        int qty = (quantity == null) ? 0 : quantity;

        CartItem item = cartItemRepository
                .findByUserIdAndProductId(userId, productId)
                .orElse(null);

        if (item == null) return;

        if (qty <= 0) {
            cartItemRepository.deleteByUserIdAndProductId(userId, productId);
        } else {
            item.setQuantity(qty);
            cartItemRepository.save(item);
        }
    }

    @Override
    public void removeFromCart(Integer userId, Integer productId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
    }


}
