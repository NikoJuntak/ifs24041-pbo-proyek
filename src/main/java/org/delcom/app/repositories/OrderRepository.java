package org.delcom.app.repositories;

import org.delcom.app.entities.Order;
import org.delcom.app.entities.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Mencari order berdasarkan status
    List<Order> findByStatus(OrderStatus status);
    
    // Semua order diurutkan dari tanggal terbaru
    List<Order> findAllByOrderByOrderDateDesc();
    
    // Total revenue hanya dari order yang statusnya DONE
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'DONE'")
    BigDecimal getTotalRevenue();
    
    // Count berdasarkan status (dibutuhkan DashboardService)
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") OrderStatus status);
    
    // Sum total amount berdasarkan status (dibutuhkan DashboardService)
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") OrderStatus status);
    
    // Get total orders (dibutuhkan DashboardService)
    @Query("SELECT COUNT(o) FROM Order o")
    long count();
}