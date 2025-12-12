package org.delcom.app.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTests {

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order("Niko", OrderStatus.NEW, LocalDateTime.now());
    }

    // Dummy Product minimal untuk membantu pembuatan OrderItem
    private Product createDummyProduct(String name, BigDecimal price) {
        Product p = new Product(name, "Kategori", price, 10, "Deskripsi");
        return p;
    }

    @Test
    void testConstructorBasic() {
        assertEquals("Niko", order.getCustomerName());
        assertEquals(OrderStatus.NEW, order.getStatus());
        assertNotNull(order.getOrderDate());
        assertEquals(BigDecimal.ZERO, order.getTotalAmount());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    void testSetterAndGetter() {
        order.setCustomerName("Amos");
        order.setStatus(OrderStatus.SHIPPED);
        order.setTotalAmount(new BigDecimal("250000"));

        assertEquals("Amos", order.getCustomerName());
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(new BigDecimal("250000"), order.getTotalAmount());
    }

    @Test
    void testAddItemBiDirectional() {
        Product product = createDummyProduct("Laptop", new BigDecimal("15000000"));
        OrderItem item = new OrderItem(product, 1, new BigDecimal("15000000"));

        order.addItem(item);

        // Item bertambah
        assertEquals(1, order.getItems().size());
        assertEquals(item, order.getItems().get(0));

        // Relasi terhubung dua arah
        assertEquals(order, item.getOrder());
    }

    @Test
    void testAddMultipleItems() {
        Product p1 = createDummyProduct("Keyboard", new BigDecimal("200000"));
        Product p2 = createDummyProduct("Mouse", new BigDecimal("150000"));

        OrderItem item1 = new OrderItem(p1, 2, new BigDecimal("400000"));
        OrderItem item2 = new OrderItem(p2, 1, new BigDecimal("150000"));

        order.addItem(item1);
        order.addItem(item2);

        assertEquals(2, order.getItems().size());
        assertEquals(order, item1.getOrder());
        assertEquals(order, item2.getOrder());
    }

    @Test
    void testUpdateTotalAmountManually() {
        order.setTotalAmount(new BigDecimal("999999"));
        assertEquals(new BigDecimal("999999"), order.getTotalAmount());
    }

    @Test
    void testEmptyConstructor() {
        Order emptyOrder = new Order();

        assertNull(emptyOrder.getId());
        assertNull(emptyOrder.getCustomerName());
        assertNull(emptyOrder.getStatus());
        assertNull(emptyOrder.getOrderDate());
        assertNotNull(emptyOrder.getItems());  
        assertTrue(emptyOrder.getItems().isEmpty());
    }

    @Test
    void testSetAndGetId() {
        order.setId(123L);
        assertEquals(123L, order.getId());
    }

    @Test
    void testSetOrderDate() {
        LocalDateTime time = LocalDateTime.of(2025, 1, 1, 12, 0);
        order.setOrderDate(time);

        assertEquals(time, order.getOrderDate());
    }

    @Test
    void testSetItems() {
        Product p = createDummyProduct("Flashdisk", new BigDecimal("100000"));
        OrderItem item = new OrderItem(p, 3, new BigDecimal("300000"));

        // Buat list items manual
        java.util.List<OrderItem> newList = new java.util.ArrayList<>();
        newList.add(item);

        order.setItems(newList);

        assertEquals(1, order.getItems().size());
        assertEquals(item, order.getItems().get(0));
    }

    @Test
    void testGetId() {
        order.setId(999L);
        assertEquals(999L, order.getId());
    }

}
