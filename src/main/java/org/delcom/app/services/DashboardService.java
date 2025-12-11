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
        summary.put("totalRevenue", totalRevenue);

        // 2. Hitung jumlah pesanan per status
        long newOrders = orderRepository.countByStatus(OrderStatus.NEW);
        long processingOrders = orderRepository.countByStatus(OrderStatus.PROCESSING);
        long doneOrders = orderRepository.countByStatus(OrderStatus.DONE);
        
        summary.put("countNew", newOrders);
        summary.put("countProcessing", processingOrders);
        summary.put("countDone", doneOrders);
        summary.put("totalOrders", orderRepository.count());

        return summary;
    }

    // Data untuk Chart (Misal: Distribusi Status Order)
    // Format disesuaikan agar mudah dibaca Chart.js di frontend
    public Map<String, Object> getOrderStatusChartData() {
        Map<String, Object> chartData = new HashMap<>();
        
        String[] labels = {"New", "Processing", "Shipped", "Done"};
        long[] data = {
            orderRepository.countByStatus(OrderStatus.NEW),
            orderRepository.countByStatus(OrderStatus.PROCESSING),
            orderRepository.countByStatus(OrderStatus.SHIPPED),
            orderRepository.countByStatus(OrderStatus.DONE)
        };

        chartData.put("labels", labels);
        chartData.put("data", data);
        
        return chartData;
    }
}