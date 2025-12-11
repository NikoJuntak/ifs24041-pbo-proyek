package org.delcom.app.entities;

public enum OrderStatus {
    NEW,        // Pesanan baru masuk
    PROCESSING, // Sedang disiapkan
    SHIPPED,    // Dikirim kurir
    DONE        // Selesai/Diterima
}