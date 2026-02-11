package com.example.app.usercontrollers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.app.entities.AddToCartRequest;
import com.example.app.entities.CartItem;
import com.example.app.entities.CartItemResponse;
import com.example.app.entities.Product;
import com.example.app.entities.UpdateCartRequest;
import com.example.app.entities.User;
import com.example.app.repositories.ProductRepository;
import com.example.app.repositories.UserRepository;
import com.example.app.userservice.AuthServiceContract;
import com.example.app.userservice.CartServiceContract;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CartController {

    private final CartServiceContract cartService;
    private final AuthServiceContract authService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartController(CartServiceContract cartService,
                          AuthServiceContract authService,
                          UserRepository userRepository,
                          ProductRepository productRepository) {
        this.cartService = cartService;
        this.authService = authService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest req,
                                       HttpServletRequest request) {

        String token = getCookieValue(request, "authToken");

        if (token == null || !authService.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        if (req.getProductId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "productId is missing"));
        }

        String username = authService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Integer qty = (req.getQuantity() == null || req.getQuantity() <= 0) ? 1 : req.getQuantity();

        cartService.addToCart(user.getUserId(), req.getProductId(), qty);

        long count = cartService.getCartCount(user.getUserId());
        return ResponseEntity.ok(Map.of("message", "Added to cart", "count", count));
    }

    @GetMapping("/items/count")
    public ResponseEntity<?> cartCount(HttpServletRequest request) {
        String token = getCookieValue(request, "authToken");

        if (token == null || !authService.validateToken(token)) {
            return ResponseEntity.ok(Map.of("count", 0));
        }

        String username = authService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(Map.of("count", cartService.getCartCount(user.getUserId())));
    }

    @GetMapping("/items")
    public ResponseEntity<?> getCartItems(HttpServletRequest request) {

        String token = getCookieValue(request, "authToken");
        if (token == null || !authService.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String username = authService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> items = cartService.getCartItems(user.getUserId());

        List<CartItemResponse> result = items.stream().map(i -> {
            Product p = productRepository.findById(i.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            return new CartItemResponse(
                    i.getId(),
                    p.getProductId(),
                    p.getName(),
                    p.getPrice(),
                    i.getQuantity(),
                    p.getCategoryId()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie c : cookies) {
            if (cookieName.equals(c.getName())) return c.getValue();
        }
        return null;
    }
    
    @PutMapping("/update")
    public ResponseEntity<?> updateQuantity(@RequestBody com.example.app.entities.UpdateCartRequest req,
                                           HttpServletRequest request) {

        String token = getCookieValue(request, "authToken");
        if (token == null || !authService.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        if (req.getProductId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "productId is missing"));
        }

        String username = authService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Integer qty = (req.getQuantity() == null) ? 0 : req.getQuantity();

        cartService.updateCartItemQuantity(user.getUserId(), req.getProductId(), qty);

        long count = cartService.getCartCount(user.getUserId());
        return ResponseEntity.ok(Map.of("message", "Quantity updated", "count", count));
    }


}
