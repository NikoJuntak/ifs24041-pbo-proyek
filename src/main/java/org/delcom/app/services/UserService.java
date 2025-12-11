package org.delcom.app.services;

import org.delcom.app.entities.User;
import org.delcom.app.entities.UserRole;
import org.delcom.app.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Inject PasswordEncoder agar kita bisa hash password saat create/update
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- CREATE ---
    @Transactional
    public User createUser(String fullName, String username, String rawPassword, UserRole role) {
        // Cek apakah username sudah ada
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username sudah digunakan: " + username);
        }

        User user = new User();
        user.setFullName(fullName);
        user.setUsername(username);
        // PENTING: Hash password sebelum simpan
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);

        return userRepository.save(user);
    }

    // --- READ ---
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan dengan username: " + username));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan dengan ID: " + id));
    }

    public List<User> getAllStaff() {
        return userRepository.findByRole(UserRole.STAFF);
    }

    @Transactional
    public void createStaff(String fullName, String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username sudah digunakan!");
        }
        
        User staff = new User();
        staff.setFullName(fullName);
        staff.setUsername(username);
        staff.setPassword(passwordEncoder.encode(password)); // Hash Password
        staff.setRole(UserRole.STAFF); // Paksa Role STAFF
        
        userRepository.save(staff);
    }

    // --- UPDATE DATA ---
    @Transactional
    public User updateUser(Long id, String newFullName, String newUsername) {
        User user = getUserById(id); // Akan throw error jika tidak ketemu

        // Cek jika username diganti, apakah username baru sudah dipakai orang lain?
        if (!user.getUsername().equals(newUsername) && userRepository.existsByUsername(newUsername)) {
            throw new RuntimeException("Username " + newUsername + " sudah digunakan user lain.");
        }

        user.setFullName(newFullName);
        user.setUsername(newUsername);
        
        return userRepository.save(user);
    }

    // --- UPDATE PASSWORD ---
    @Transactional
    public User updatePassword(Long id, String newRawPassword) {
        User user = getUserById(id);
        
        // Hash password baru
        user.setPassword(passwordEncoder.encode(newRawPassword));
        
        return userRepository.save(user);
    }

    // --- DELETE ---
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User tidak ditemukan");
        }
        userRepository.deleteById(id);
    }
}