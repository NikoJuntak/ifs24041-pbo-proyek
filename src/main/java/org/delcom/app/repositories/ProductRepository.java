package org.delcom.app.repositories;

import org.delcom.app.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 1. Menampilkan semua produk yang TIDAK dihapus
    List<Product> findByIsDeletedFalse();

    // 2. Mencari produk berdasarkan nama (Search Bar) yang aktif
    List<Product> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name);

    // 3. Filter berdasarkan kategori yang aktif
    List<Product> findByCategoryAndIsDeletedFalse(String category);
}