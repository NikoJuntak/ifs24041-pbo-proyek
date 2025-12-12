package org.delcom.app.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    @Test
    void testUserConstructorAndGetters() {
        User user = new User(
                "john_doe",
                "password123",
                "John Doe",
                UserRole.ADMIN
        );

        assertEquals("john_doe", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("John Doe", user.getFullName());
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    void testSetters() {
        User user = new User();

        user.setUsername("staff_user");
        user.setPassword("staffpassword");
        user.setFullName("Staff User");
        user.setRole(UserRole.STAFF);

        assertEquals("staff_user", user.getUsername());
        assertEquals("staffpassword", user.getPassword());
        assertEquals("Staff User", user.getFullName());
        assertEquals(UserRole.STAFF, user.getRole());
    }

    @Test
    void testSetIdAndGetId() {
        User user = new User();

        user.setId(50L);

        assertEquals(50L, user.getId());
    }

    @Test
    void testGetRoleName() {
        assertEquals("ROLE_ADMIN", UserRole.ADMIN.getRoleName());
        assertEquals("ROLE_STAFF", UserRole.STAFF.getRoleName());
        assertEquals("ROLE_USER", UserRole.USER.getRoleName());
    }

    @Test
    void testToStringContainsUsefulInfo() {
        User user = new User(
                "martin",
                "123456",
                "Martin Simanjuntak",
                UserRole.USER
        );

        String output = user.toString();

        assertTrue(output.contains("martin"));
        assertTrue(output.contains("Martin Simanjuntak"));
        assertTrue(output.contains("USER"));
    }
}
