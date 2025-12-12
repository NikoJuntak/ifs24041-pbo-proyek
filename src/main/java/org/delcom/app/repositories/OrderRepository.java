package org.delcom.app.repositories;

import org.delcom.app.entities.Order;
import org.delcom.app.entities.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 1. Mencari order milik user tertentu (History Belanja) - Opsional jika ada fitur pelanggan login
    // List<Order> findByCustomerName(String customerName);

    // 2. Mencari order berdasarkan Status (Misal: Tampilkan semua order 'NEW')
    List<Order> findByStatus(OrderStatus status);

    // 3. Mencari order dalam rentang tanggal tertentu (Filter Dashboard)
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // --- QUERY KHUSUS UNTUK DASHBOARD ---

    // 4. Menghitung Total Omzet (Hanya dari order yang statusnya DONE)
    // COALESCE digunakan agar jika tidak ada data, return 0 (bukan null)
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") OrderStatus status);

    // 5. Menghitung Jumlah Order berdasarkan status (untuk Pie Chart)
    long countByStatus(OrderStatus status);
}