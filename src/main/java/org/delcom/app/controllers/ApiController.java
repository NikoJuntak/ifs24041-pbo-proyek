package org.delcom.app.controllers;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.dto.OrderDetailResponse;
import org.delcom.app.entities.Order;
import org.delcom.app.entities.Product;
import org.delcom.app.services.OrderService;
import org.delcom.app.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final ProductService productService;
    private final OrderService orderService;

    public ApiController(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    // --- 1. DETAIL PRODUCT ---
    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProductDetail(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(new ApiResponse<>(200, "Detail Produk", product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    // --- 2. DETAIL ORDER (Include Items) ---
    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);

            // Mapping Entity ke DTO Response agar JSON bersih
            List<OrderDetailResponse.ItemDetail> items = order.getItems().stream()
                .map(item -> new OrderDetailResponse.ItemDetail(
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getProduct().getPrice(),
                        item.getSubtotal()
                )).collect(Collectors.toList());

            OrderDetailResponse response = new OrderDetailResponse(
                    order.getId(),
                    order.getCustomerName(),
                    order.getStatus().name(),
                    order.getTotalAmount(),
                    order.getOrderDate(),
                    items
            );

            return ResponseEntity.ok(new ApiResponse<>(200, "Detail Order", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }
}