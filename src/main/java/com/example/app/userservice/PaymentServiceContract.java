package com.example.app.userservice;

import java.math.BigDecimal;

public interface PaymentServiceContract {
    String createRazorpayOrder(BigDecimal amount, String receiptId);
    boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature);
}
