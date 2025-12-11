package org.delcom.app.controllers;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.dto.OrderRequestDto;
import org.delcom.app.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDto request) {
        try {
            orderService.createOrder(request);
            // ApiResponse constructor(int status, String message, Object data)
            return ResponseEntity.ok(new ApiResponse<>(200, "Pesanan berhasil dibuat", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }
}