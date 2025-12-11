package org.delcom.app.services;

import org.delcom.app.entities.OrderItem;
import org.delcom.app.repositories.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    // 1. Mengambil detail item berdasarkan ID Order
    // Berguna jika admin ingin melihat "Apa saja isi Order #1001?"
    public List<OrderItem> getItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    // 2. Data Statistik: Produk Terlaris (Top Selling)
    // Mengambil data mentah Object[] dari Repository dan mengubahnya jadi Map agar rapi di JSON
    public List<Map<String, Object>> getTopSellingProducts() {
        List<Object[]> rawData = orderItemRepository.findTopSellingProducts();
        
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rawData) {
            Map<String, Object> item = new HashMap<>();
            // row[0] adalah nama produk (String)
            // row[1] adalah total quantity (Long/Number)
            item.put("productName", row[0]); 
            item.put("totalSold", row[1]);
            
            result.add(item);
        }

        return result;
    }
}