package org.delcom.app.repositories;

import org.delcom.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Mencari user berdasarkan username (penting untuk login)
    Optional<User> findByUsername(String username);

    // Cek apakah username sudah dipakai (penting untuk register)
    boolean existsByUsername(String username);
}