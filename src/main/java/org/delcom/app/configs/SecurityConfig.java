package org.delcom.app.configs;

import org.delcom.app.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // PENTING: Import ini

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            
            // Exception Handling
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    System.out.println("Akses Ditolak: " + req.getRequestURI() + " - " + e.getMessage());
                    res.sendError(401, "Unauthorized: Akses ditolak, silakan login.");
                })
            )

            // Session Management
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Authorization Rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", 
                    "/index", 
                    "/auth/**", 
                    "/api/auth/**", 
                    "/css/**", 
                    "/js/**", 
                    "/assets/**", 
                    "/uploads/**", 
                    "/error"
                ).permitAll()
                
                .requestMatchers("/dashboard/**").authenticated()
                .requestMatchers("/products/**").authenticated()
                .requestMatchers("/orders/**").authenticated()
                .requestMatchers("/api/**").authenticated()
                
                .anyRequest().authenticated()
            );

        // --- PERBAIKAN UTAMA DI SINI ---
        // Kita harus memasukkan filter JWT kita sebelum filter UsernamePassword bawaan Spring
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}