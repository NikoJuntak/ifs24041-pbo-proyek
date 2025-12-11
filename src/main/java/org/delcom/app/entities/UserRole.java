package org.delcom.app.entities;

public enum UserRole {
    ADMIN,  // Akses penuh: Manajemen Produk, User, Dashboard, dan Status Order
    STAFF;  // Akses terbatas: Hanya proses Order dan lihat Dashboard (Tidak bisa hapus produk/user)

    // Helper untuk Spring Security nanti
    public String getRoleName() {
        return "ROLE_" + this.name();
    }
}