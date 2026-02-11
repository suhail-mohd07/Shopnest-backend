package com.example.app.usercontrollers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.app.entities.*;
import com.example.app.repositories.*;
import com.example.app.userservice.AuthServiceContract;
import com.example.app.userservice.PaymentServiceContract;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PaymentController {

    private final AuthServiceContract authService;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentServiceContract paymentService;

    public PaymentController(AuthServiceContract authService,
                             UserRepository userRepository,
                             CartItemRepository cartItemRepository,
                             ProductRepository productRepository,
                             OrderRepository orderRepository,
                             OrderItemRepository orderItemRepository,
                             PaymentServiceContract paymentService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentService = paymentService;
    }

    // ✅ Create order (PENDING) + create Razorpay order id
    @PostMapping("/create")
    public ResponseEntity<?> createPaymentOrder(HttpServletRequest request) {

        String token = getCookieValue(request, "authToken");
        if (token == null || !authService.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String username = authService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getUserId());
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty"));
        }

        // calculate total from DB products (don’t trust frontend amount)
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            Product p = productRepository.findById(ci.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + ci.getProductId()));
            BigDecimal line = BigDecimal.valueOf(p.getPrice()).multiply(BigDecimal.valueOf(ci.getQuantity()));
            total = total.add(line);
        }

        String ourOrderId = "ORD_" + UUID.randomUUID();

        Order order = new Order();
        order.setOrderId(ourOrderId);
        order.setUserId(user.getUserId());
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PENDING);

        orderRepository.save(order);

        // save order_items
        for (CartItem ci : cartItems) {
            Product p = productRepository.findById(ci.getProductId()).orElseThrow();

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProductId(ci.getProductId());
            oi.setQuantity(ci.getQuantity());

            BigDecimal pricePerUnit = BigDecimal.valueOf(p.getPrice());
            BigDecimal lineTotal = pricePerUnit.multiply(BigDecimal.valueOf(ci.getQuantity()));

            oi.setPricePerUnit(pricePerUnit);
            oi.setTotalPrice(lineTotal);

            orderItemRepository.save(oi);
        }

        // create Razorpay order
        String razorpayOrderId = paymentService.createRazorpayOrder(total, ourOrderId);

        // If you added razorpay_order_id column in DB, you can store it
        // order.setRazorpayOrderId(razorpayOrderId);
        // orderRepository.save(order);

        return ResponseEntity.ok(Map.of(
                "orderId", ourOrderId,
                "razorpayOrderId", razorpayOrderId,
                "amount", total
        ));
    }

    // ✅ Verify payment + mark SUCCESS/FAILED + clear cart
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody VerifyPaymentRequest req,
                                          HttpServletRequest request) {

        String token = getCookieValue(request, "authToken");
        if (token == null || !authService.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String username = authService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(req.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }

        boolean ok = paymentService.verifySignature(
                req.getRazorpayOrderId(),
                req.getRazorpayPaymentId(),
                req.getRazorpaySignature()
        );

        if (ok) {
            order.setStatus(OrderStatus.SUCCESS);
            orderRepository.save(order);

            // clear cart after successful payment
            cartItemRepository.deleteByUserId(user.getUserId());

            return ResponseEntity.ok(Map.of("message", "Payment verified successfully"));
        } else {
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);

            return ResponseEntity.status(400).body(Map.of("error", "Payment verification failed"));
        }
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (cookieName.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
