package org.delcom.app.controllers;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.dto.OrderRequestDto;
import org.delcom.app.entities.Order;
import org.delcom.app.entities.OrderItem;
import org.delcom.app.entities.OrderStatus;
import org.delcom.app.services.OrderService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
            Order order = orderService.createOrder(request);
            return ResponseEntity.ok(new ApiResponse<>(200, "Pesanan berhasil dibuat", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}/invoice")
    public ResponseEntity<?> getInvoice(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            
            // Enhanced invoice data
            Map<String, Object> invoiceData = new HashMap<>();
            invoiceData.put("id", order.getId());
            invoiceData.put("customerName", order.getCustomerName());
            invoiceData.put("orderDate", order.getOrderDate());
            invoiceData.put("status", order.getStatus().toString());
            invoiceData.put("totalAmount", order.getTotalAmount());
            
            // Items dengan detail produk
            List<Map<String, Object>> items = new ArrayList<>();
            
            for (OrderItem item : order.getItems()) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("productName", item.getProduct() != null ? item.getProduct().getName() : "Produk tidak ditemukan");
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("pricePerItem", item.getProduct() != null ? item.getProduct().getPrice() : BigDecimal.ZERO);
                itemMap.put("subtotal", item.getSubtotal());
                items.add(itemMap);
            }
            
            invoiceData.put("items", items);
            
            return ResponseEntity.ok(new ApiResponse<>(200, "Invoice data", invoiceData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            
            // Membuat response yang lebih clean
            Map<String, Object> response = new HashMap<>();
            response.put("id", order.getId());
            response.put("customerName", order.getCustomerName());
            response.put("status", order.getStatus().toString());
            response.put("totalAmount", order.getTotalAmount());
            response.put("orderDate", order.getOrderDate());
            
            List<Map<String, Object>> items = new ArrayList<>();
            for (OrderItem item : order.getItems()) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("productName", item.getProduct() != null ? item.getProduct().getName() : "Unknown");
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("price", item.getProduct() != null ? item.getProduct().getPrice() : BigDecimal.ZERO);
                itemMap.put("subtotal", item.getSubtotal());
                items.add(itemMap);
            }
            response.put("items", items);
            
            return ResponseEntity.ok(new ApiResponse<>(200, "Order detail", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PostMapping("/update-status")
    public ResponseEntity<?> updateStatus(@RequestParam Long orderId, 
                                          @RequestParam OrderStatus status,
                                          HttpServletRequest request) {
        try {
            Order updatedOrder = orderService.updateStatus(orderId, status);
            
            String referer = request.getHeader("Referer");
            String redirectUrl = (referer != null) ? referer : "/dashboard";

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, redirectUrl)
                    .build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Error: " + e.getMessage(), null));
        }
    }

    @GetMapping("/export-csv")
    public ResponseEntity<byte[]> exportCsv() {
        List<Order> orders = orderService.getAllOrders();
        
        StringBuilder csv = new StringBuilder();
        // Header CSV dengan UTF-8 BOM untuk Excel
        csv.append("\uFEFF"); // UTF-8 BOM
        csv.append("ID Order;Nama Pelanggan;Tanggal Order;Total Belanja;Status;Jumlah Item\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Order order : orders) {
            csv.append(order.getId()).append(";");
            csv.append("\"").append(order.getCustomerName()).append("\";");
            csv.append(order.getOrderDate().format(formatter)).append(";");
            csv.append(order.getTotalAmount()).append(";");
            csv.append(order.getStatus()).append(";");
            csv.append(order.getItems() != null ? order.getItems().size() : 0).append("\n");
        }

        byte[] csvBytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=laporan-transaksi.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvBytes);
    }
    
    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenue() {
        try {
            BigDecimal totalRevenue = orderService.getTotalRevenue();
            Map<String, Object> response = new HashMap<>();
            response.put("totalRevenue", totalRevenue);
            response.put("formattedRevenue", "Rp " + String.format("%,.0f", totalRevenue));
            return ResponseEntity.ok(new ApiResponse<>(200, "Total revenue", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }
}