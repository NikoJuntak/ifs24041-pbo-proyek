package org.delcom.app.services;

import org.delcom.app.dto.OrderRequestDto;
import org.delcom.app.entities.*;
import org.delcom.app.repositories.OrderRepository;
import org.delcom.app.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<Order> getAllOrders() {
        // Mengurutkan dari yang terbaru
        return orderRepository.findAll(); 
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pesanan tidak ditemukan: " + id));
    }

    // --- LOGIKA PEMBUATAN PESANAN ---
    @Transactional
    public Order createOrder(OrderRequestDto request) {
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW); // Default status awal

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (OrderRequestDto.OrderItemDto itemDto : request.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan ID: " + itemDto.getProductId()));

            // 1. Validasi Stok
            if (product.getStock() < itemDto.getQuantity()) {
                throw new RuntimeException("Stok tidak cukup untuk produk: " + product.getName());
            }

            // 2. Kurangi Stok Produk
            product.setStock(product.getStock() - itemDto.getQuantity());
            productRepository.save(product);

            // 3. Hitung Subtotal
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            grandTotal = grandTotal.add(subtotal);

            // 4. Buat Entity OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setSubtotal(subtotal);
            
            // Link ke Order (Penting untuk relasi OneToMany)
            order.addItem(orderItem); 
        }

        order.setTotalAmount(grandTotal);
        return orderRepository.save(order);
    }

    // --- LOGIKA UPDATE STATUS ---
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        
        // Validasi Alur Status
        if (!isValidTransition(order.getStatus(), newStatus)) {
            throw new RuntimeException("Perubahan status tidak valid dari " + order.getStatus() + " ke " + newStatus);
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    // Helper: Validasi Alur (NEW -> PROCESSING -> SHIPPED -> DONE)
    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        if (current == next) return true; // Tidak berubah
        
        return switch (current) {
            case NEW -> next == OrderStatus.PROCESSING;
            case PROCESSING -> next == OrderStatus.SHIPPED;
            case SHIPPED -> next == OrderStatus.DONE;
            case DONE -> false; // Tidak bisa berubah lagi kalau sudah DONE
            default -> false;
        };
    }
}