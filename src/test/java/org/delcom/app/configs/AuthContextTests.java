package org.delcom.app.configs;

import org.delcom.app.entities.User;
import org.delcom.app.entities.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthContextTests {

    @Test
    void testDefaultState() {
        AuthContext authContext = new AuthContext();

        assertNull(authContext.getAuthUser());       // default null
        assertFalse(authContext.isAuthenticated());  // belum login
    }

    @Test
    void testSetAndGetAuthUser() {
        AuthContext authContext = new AuthContext();

        User user = new User();
        user.setId(1L);
        user.setUsername("niko");
        user.setPassword("123456");
        user.setFullName("Niko Amos");
        user.setRole(UserRole.USER);

        authContext.setAuthUser(user);

        assertEquals(user, authContext.getAuthUser());
        assertTrue(authContext.isAuthenticated());
    }

    @Test
    void testIsAuthenticatedFalseWhenUserRemoved() {
        AuthContext authContext = new AuthContext();

        User user = new User("martin", "pass", "Martin Simanjuntak", UserRole.ADMIN);
        authContext.setAuthUser(user);

        assertTrue(authContext.isAuthenticated());

        // logout (set null)
        authContext.setAuthUser(null);

        assertFalse(authContext.isAuthenticated());
        assertNull(authContext.getAuthUser());
    }

    @Test
    void testChangeAuthUser() {
        AuthContext authContext = new AuthContext();

        User user1 = new User("user1", "123", "User Satu", UserRole.USER);
        User user2 = new User("user2", "456", "User Dua", UserRole.STAFF);

        authContext.setAuthUser(user1);
        assertEquals(user1, authContext.getAuthUser());

        authContext.setAuthUser(user2);
        assertEquals(user2, authContext.getAuthUser()); // diganti user
        assertNotEquals(user1, authContext.getAuthUser());
    }
}
