package org.delcom.app.entities;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTests {

    @Test
    void testConstructorAndGetters() {
        Product product = new Product(
                "Laptop",
                "Elektronik",
                new BigDecimal("5999000"),
                10,
                "Laptop gaming ringan"
        );

        assertEquals("Laptop", product.getName());
        assertEquals("Elektronik", product.getCategory());
        assertEquals(new BigDecimal("5999000"), product.getPrice());
        assertEquals(10, product.getStock());
        assertEquals("Laptop gaming ringan", product.getDescription());
    }

    @Test
    void testSettersIncludingId() {
        Product product = new Product();

        product.setId(100L);
        product.setName("Keyboard");
        product.setCategory("Aksesoris");
        product.setPrice(new BigDecimal("250000"));
        product.setStock(50);
        product.setDescription("Keyboard mechanical");
        product.setImage("image.jpg");
        product.setDeleted(true);

        assertEquals(100L, product.getId());
        assertEquals("Keyboard", product.getName());
        assertEquals("Aksesoris", product.getCategory());
        assertEquals(new BigDecimal("250000"), product.getPrice());
        assertEquals(50, product.getStock());
        assertEquals("Keyboard mechanical", product.getDescription());
        assertEquals("image.jpg", product.getImage());
        assertTrue(product.isDeleted());
    }

    @Test
    void testLifecycleOnCreate() {
        Product product = new Product();
        product.onCreate();

        assertNotNull(product.getCreatedAt());
        assertNotNull(product.getUpdatedAt());
        assertTrue(product.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testLifecycleOnUpdate() throws InterruptedException {
        Product product = new Product();
        product.onCreate();

        LocalDateTime createdAtBefore = product.getCreatedAt();
        LocalDateTime updatedAtBefore = product.getUpdatedAt();

        Thread.sleep(20);

        product.onUpdate();

        assertEquals(createdAtBefore, product.getCreatedAt()); // createdAt harus tetap
        assertTrue(product.getUpdatedAt().isAfter(updatedAtBefore)); // updatedAt berubah
    }

    @Test
    void testSetUpdatedAt() {
        Product product = new Product();
        product.onCreate();

        LocalDateTime newUpdateTime = LocalDateTime.now().plusMinutes(10);
        product.setUpdatedAt(newUpdateTime);

        assertEquals(newUpdateTime, product.getUpdatedAt());
    }

    @Test
    void testSetCreatedAt() {
        Product product = new Product();

        LocalDateTime time = LocalDateTime.now().minusDays(1); 

        product.setCreatedAt(time);

        assertEquals(time, product.getCreatedAt());
    }

}
