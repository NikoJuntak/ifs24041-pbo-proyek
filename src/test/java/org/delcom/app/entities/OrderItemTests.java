package org.delcom.app.entities;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class OrderItemTests {

    // Helper untuk membuat Product
    private Product createProduct(String name, BigDecimal price) {
        return new Product(name, "Kategori", price, 10, "Deskripsi produk");
    }

    // Helper untuk membuat Order
    private Order createOrder() {
        return new Order("Niko", OrderStatus.NEW, LocalDateTime.now());
    }

    @Test
    void testConstructorWithoutOrder() {
        Product product = createProduct("Mouse", new BigDecimal("150000"));
        OrderItem item = new OrderItem(product, 2, new BigDecimal("300000"));

        assertEquals(product, item.getProduct());
        assertEquals(2, item.getQuantity());
        assertEquals(new BigDecimal("300000"), item.getSubtotal());
        assertNull(item.getOrder());  // karena belum ditambahkan ke order
    }

    @Test
    void testConstructorWithOrder() {
        Product product = createProduct("Keyboard", new BigDecimal("200000"));
        Order order = createOrder();

        OrderItem item = new OrderItem(order, product, 1, new BigDecimal("200000"));

        assertEquals(order, item.getOrder());
        assertEquals(product, item.getProduct());
        assertEquals(1, item.getQuantity());
        assertEquals(new BigDecimal("200000"), item.getSubtotal());
    }

    @Test
    void testSetters() {
        Product product = createProduct("Monitor", new BigDecimal("1000000"));
        OrderItem item = new OrderItem();

        item.setProduct(product);
        item.setQuantity(5);
        item.setSubtotal(new BigDecimal("5000000"));

        assertEquals(product, item.getProduct());
        assertEquals(5, item.getQuantity());
        assertEquals(new BigDecimal("5000000"), item.getSubtotal());
    }

    @Test
    void testSetOrder() {
        Product product = createProduct("Laptop", new BigDecimal("15000000"));
        OrderItem item = new OrderItem(product, 1, new BigDecimal("15000000"));

        Order order = createOrder();
        item.setOrder(order);

        assertEquals(order, item.getOrder());
    }

    @Test
    void testAddItemThroughOrder() {
        // menguji relasi bi-directional melalui Order.addItem()
        Product product = createProduct("SSD", new BigDecimal("500000"));
        Order order = createOrder();
        OrderItem item = new OrderItem(product, 1, new BigDecimal("500000"));

        order.addItem(item);

        assertEquals(order, item.getOrder());
        assertTrue(order.getItems().contains(item));
    }

    @Test
    void testSetAndGetId() {
        OrderItem item = new OrderItem();

        item.setId(100L);

        assertEquals(100L, item.getId());
    }

}
