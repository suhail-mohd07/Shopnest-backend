package com.example.app.entities;

public class AddToCartRequest {
    private Integer productId;
    private Integer quantity;

    public AddToCartRequest() {}

    public Integer getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
