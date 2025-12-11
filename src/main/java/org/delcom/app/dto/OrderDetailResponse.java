package org.delcom.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDetailResponse {
    private Long id;
    private String customerName;
    private String status;
    private BigDecimal totalAmount;
    private String orderDate; // String agar format tanggal rapi di frontend
    private List<ItemDetail> items;

    // Constructor, Getter, Setter
    public OrderDetailResponse(Long id, String customerName, String status, BigDecimal totalAmount, LocalDateTime orderDate, List<ItemDetail> items) {
        this.id = id;
        this.customerName = customerName;
        this.status = status;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate.toString(); // Atau format pakai DateTimeFormatter
        this.items = items;
    }

    // Getters...
    public Long getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getOrderDate() { return orderDate; }
    public List<ItemDetail> getItems() { return items; }

    // Inner Class untuk Item
    public static class ItemDetail {
        private String productName;
        private Integer quantity;
        private BigDecimal pricePerItem;
        private BigDecimal subtotal;

        public ItemDetail(String productName, Integer quantity, BigDecimal pricePerItem, BigDecimal subtotal) {
            this.productName = productName;
            this.quantity = quantity;
            this.pricePerItem = pricePerItem;
            this.subtotal = subtotal;
        }

        // Getters...
        public String getProductName() { return productName; }
        public Integer getQuantity() { return quantity; }
        public BigDecimal getPricePerItem() { return pricePerItem; }
        public BigDecimal getSubtotal() { return subtotal; }
    }
}