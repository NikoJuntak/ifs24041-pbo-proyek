package org.delcom.app.controllers;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.dto.auth.JwtResponse;
import org.delcom.app.dto.auth.LoginRequest;
import org.delcom.app.dto.auth.RegisterRequest;
import org.delcom.app.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // <--- PENTING: Gunakan RestController untuk API
@RequestMapping("/api/auth") // <--- PENTING: Prefix URL harus sesuai dengan yang dipanggil JS
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Endpoint Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            JwtResponse jwtResponse = authService.authenticateUser(request);
            
            // Mengembalikan JSON sukses
            return ResponseEntity.ok(new ApiResponse<>(200, "Login Berhasil", jwtResponse));
        
        } catch (Exception e) {
            // Mengembalikan JSON gagal
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(401, "Username atau Password salah", null));
        }
    }

    // Endpoint Register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            authService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(201, "Registrasi Berhasil", null));
        
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, "Error: " + e.getMessage(), null));
        }
    }
}