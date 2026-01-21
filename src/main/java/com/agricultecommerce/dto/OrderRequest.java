package com.agricultecommerce.dto;

public class OrderRequest {
    private String shippingAddress;

    public OrderRequest() {
    }

    public OrderRequest(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
