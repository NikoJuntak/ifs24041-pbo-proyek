package org.delcom.app.dto.auth;

import org.delcom.app.entities.UserRole;

public class RegisterRequest {
    private String fullName;
    private String username;
    private String password;
    private UserRole role; // Opsional, defaultnya nanti bisa di-set STAFF

    public RegisterRequest() {}

    public RegisterRequest(String fullName, String username, String password, UserRole role) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}