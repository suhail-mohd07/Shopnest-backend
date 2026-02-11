package com.example.app.entities;

public class CartItemResponse {

    private Integer cartItemId;
    private Integer productId;
    private String name;
    private double price;
    private Integer quantity;
    private int categoryId;

    public CartItemResponse() {}

    public CartItemResponse(Integer cartItemId,
                            Integer productId,
                            String name,
                            double price,
                            Integer quantity,
                            int categoryId) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.categoryId = categoryId;
    }

    public Integer getCartItemId() { return cartItemId; }
    public Integer getProductId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public int getCategoryId() { return categoryId; }

    public void setCartItemId(Integer cartItemId) { this.cartItemId = cartItemId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
}
