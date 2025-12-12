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

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Cek apakah tabel user kosong?
            if (userRepository.count() == 0) {
                System.out.println("... Database kosong. Membuat Default Admin ...");

                
                User admin = new User(
                        "admin",                                
                        passwordEncoder.encode("admin123"),     
                        "Super Administrator",        
                        UserRole.ADMIN                         
                );

                userRepository.save(admin);
                
                System.out.println("... Default Admin berhasil dibuat! ...");
                System.out.println("Username: admin");
                System.out.println("Password: admin123");
            }
        };
    }
}