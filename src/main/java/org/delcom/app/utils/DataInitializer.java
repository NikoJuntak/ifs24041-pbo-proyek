package org.delcom.app.utils;

import org.delcom.app.entities.User;
import org.delcom.app.entities.UserRole;
import org.delcom.app.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    // KITA HAPUS definisi Bean PasswordEncoder di sini.
    // Spring Boot cukup mengambilnya dari SecurityConfig secara otomatis (Dependency Injection).

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Cek apakah tabel user kosong?
            if (userRepository.count() == 0) {
                System.out.println("... Database kosong. Membuat Default Admin ...");

                // PERBAIKAN: Menggunakan Constructor (Karena tidak pakai Lombok @Builder)
                User admin = new User(
                        "admin",                                // username
                        passwordEncoder.encode("admin123"),     // password (hashed)
                        "Super Administrator",                  // fullName
                        UserRole.ADMIN                          // role
                );

                userRepository.save(admin);
                
                System.out.println("... Default Admin berhasil dibuat! ...");
                System.out.println("Username: admin");
                System.out.println("Password: admin123");
            }
        };
    }
}