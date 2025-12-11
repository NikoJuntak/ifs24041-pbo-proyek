package org.delcom.app.services;

import org.delcom.app.entities.OrderStatus;
import org.delcom.app.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final OrderRepository orderRepository;

    public DashboardService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Data untuk Kartu Statistik (Total Pendapatan, Jumlah Order, dll)
    public Map<String, Object> getSummaryData() {
        Map<String, Object> summary = new HashMap<>();

        // 1. Total Omzet (Hanya yang status DONE)
        BigDecimal totalRevenue = orderRepository.sumTotalAmountByStatus(OrderStatus.DONE);
        summary.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // 2. Hitung jumlah pesanan per status
        Long newOrders = orderRepository.countByStatus(OrderStatus.NEW);
        Long processingOrders = orderRepository.countByStatus(OrderStatus.PROCESSING);
        Long doneOrders = orderRepository.countByStatus(OrderStatus.DONE);
        Long shippedOrders = orderRepository.countByStatus(OrderStatus.SHIPPED);
        
        summary.put("countNew", newOrders != null ? newOrders : 0L);
        summary.put("countProcessing", processingOrders != null ? processingOrders : 0L);
        summary.put("countDone", doneOrders != null ? doneOrders : 0L);
        summary.put("countShipped", shippedOrders != null ? shippedOrders : 0L);
        summary.put("totalOrders", orderRepository.count());

        return summary;
    }

    // Data untuk Chart (Misal: Distribusi Status Order)
    // Format disesuaikan agar mudah dibaca Chart.js di frontend
    public Map<String, Object> getOrderStatusChartData() {
        Map<String, Object> chartData = new HashMap<>();
        
        String[] labels = {"New", "Processing", "Shipped", "Done"};
        
        Long newCount = orderRepository.countByStatus(OrderStatus.NEW);
        Long processingCount = orderRepository.countByStatus(OrderStatus.PROCESSING);
        Long shippedCount = orderRepository.countByStatus(OrderStatus.SHIPPED);
        Long doneCount = orderRepository.countByStatus(OrderStatus.DONE);
        
        long[] data = {
            newCount != null ? newCount : 0L,
            processingCount != null ? processingCount : 0L,
            shippedCount != null ? shippedCount : 0L,
            doneCount != null ? doneCount : 0L
        };

        chartData.put("labels", labels);
        chartData.put("data", data);
        
        return chartData;
    }
}