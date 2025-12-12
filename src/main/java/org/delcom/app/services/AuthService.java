package org.delcom.app.services;

import org.delcom.app.dto.auth.JwtResponse;
import org.delcom.app.dto.auth.LoginRequest;
import org.delcom.app.dto.auth.RegisterRequest;
import org.delcom.app.entities.User;
import org.delcom.app.entities.UserRole;
import org.delcom.app.repositories.UserRepository;
import org.delcom.app.utils.JwtUtils; 
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(AuthenticationManager authenticationManager, 
                       UserRepository userRepository, 
                       PasswordEncoder passwordEncoder, 
                       JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // --- Logika Login ---
    public JwtResponse authenticateUser(LoginRequest request) {
        // 1. Cek Username & Password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Ambil UserDetails untuk info tambahan
        org.springframework.security.core.userdetails.UserDetails userDetails = 
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
        
        // 3. Generate Token (SEKARANG CUKUP KIRIM USERNAME)
        String jwt = jwtUtils.generateToken(userDetails.getUsername());

        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return new JwtResponse(jwt, userDetails.getUsername(), role);
    }

    // --- Logika Register ---
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        // Default role STAFF jika tidak diisi
        UserRole roleToUse = (request.getRole() != null) ? request.getRole() : UserRole.STAFF;

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()), // Hash Password
                request.getFullName(),
                roleToUse
        );

        return userRepository.save(user);
    }
}