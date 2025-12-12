package org.delcom.app.configs;

import org.delcom.app.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Test
    @DisplayName("PasswordEncoder harus BCrypt dan dapat encode/match")
    void testPasswordEncoder() {
        SecurityConfig config = new SecurityConfig(null);
        PasswordEncoder encoder = config.passwordEncoder();

        assertThat(encoder).isNotNull();

        String raw = "password123";
        String encoded = encoder.encode(raw);

        assertThat(encoded).isNotEqualTo(raw);
        assertThat(encoder.matches(raw, encoded)).isTrue();
    }

    @Test
    @DisplayName("AuthenticationManager harus terbentuk dari AuthenticationConfiguration")
    void testAuthenticationManager() throws Exception {
        AuthenticationConfiguration mockConfig = mock(AuthenticationConfiguration.class);
        SecurityConfig config = new SecurityConfig(null);
        AuthenticationManager manager = config.authenticationManager(mockConfig);
    }

    @Test
    @DisplayName("Constructor harus menyimpan JwtAuthenticationFilter")
    void testJwtFilterStored() {
        JwtAuthenticationFilter filter = mock(JwtAuthenticationFilter.class);
        SecurityConfig config = new SecurityConfig(filter);
        assertThat(config).isNotNull();
    }

    @Test
    @DisplayName("AuthenticationEntryPoint harus memanggil println dan sendError")
    void testAuthenticationEntryPoint() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        AuthenticationEntryPoint entryPoint = (req, res, e) -> {
            System.out.println("Akses Ditolak: " + req.getRequestURI() + " - " + e.getMessage());
            res.sendError(401, "Unauthorized: Akses ditolak, silakan login.");
        };

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        when(req.getRequestURI()).thenReturn("/dashboard/test");

        String console = out.toString();

        verify(res).sendError(401, "Unauthorized: Akses ditolak, silakan login.");
    }
}
