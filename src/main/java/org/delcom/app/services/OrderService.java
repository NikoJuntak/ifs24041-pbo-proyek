package org.delcom.app.services;

import org.delcom.app.dto.OrderRequestDto;
import org.delcom.app.entities.*;
import org.delcom.app.repositories.OrderRepository;
import org.delcom.app.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }
    
    @Transactional
    public Order createOrder(OrderRequestDto request) {
        // Validasi
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Item pesanan tidak boleh kosong");
        }
        
        // Create order
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setStatus(OrderStatus.NEW); // Default NEW sesuai enum Anda
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(BigDecimal.ZERO); // Akan dihitung setelah items
        
        // Process items
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (var itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan: " + itemRequest.getProductId()));
            
            // Stock validation
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new RuntimeException("Stok " + product.getName() + " tidak cukup. Stok tersedia: " + product.getStock());
            }
            
            // Update stock
            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);
            
            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(product.getPrice()); // Simpan harga per item
            
            // Hitung subtotal
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            orderItem.setSubtotal(subtotal);
            
            order.addItem(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }
        
        // Update total amount
        order.setTotalAmount(totalAmount);
        
        return orderRepository.save(order);
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }
    
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order tidak ditemukan dengan ID: " + id));
    }
    
    @Transactional
    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
    
    public BigDecimal getTotalRevenue() {
        // Hanya hitung dari order dengan status DONE sesuai enum Anda
        BigDecimal revenue = orderRepository.getTotalRevenue();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
}