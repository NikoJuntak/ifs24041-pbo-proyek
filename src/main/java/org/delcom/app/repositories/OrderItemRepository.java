package org.delcom.app.repositories;

import org.delcom.app.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Contoh: Mencari item berdasarkan Order ID (Biasanya sudah otomatis via Order.getItems())
    List<OrderItem> findByOrderId(Long orderId);

    // Query untuk Dashboard: 5 Produk Paling Laris
    // Memilih Product ID dan Menjumlahkan Quantity
    @Query("SELECT oi.product.name, SUM(oi.quantity) as totalQty " +
           "FROM OrderItem oi " +
           "GROUP BY oi.product.name " +
           "ORDER BY totalQty DESC " +
           "LIMIT 5")
    List<Object[]> findTopSellingProducts();
}