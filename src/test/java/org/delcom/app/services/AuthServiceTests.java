package org.delcom.app.services;

import org.delcom.app.dto.auth.JwtResponse;
import org.delcom.app.dto.auth.LoginRequest;
import org.delcom.app.dto.auth.RegisterRequest;
import org.delcom.app.entities.User;
import org.delcom.app.entities.UserRole;
import org.delcom.app.repositories.UserRepository;
import org.delcom.app.utils.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Test
    @DisplayName("Login: authenticateUser harus return JWTResponse valid")
    void testAuthenticateUser() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtUtils jwtUtils = mock(JwtUtils.class);

        AuthService service = new AuthService(authenticationManager, userRepository, encoder, jwtUtils);

        LoginRequest req = new LoginRequest();
        req.setUsername("alice");
        req.setPassword("123");

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        org.springframework.security.core.userdetails.UserDetails springUser =
                org.springframework.security.core.userdetails.User
                        .withUsername("alice")
                        .password("encoded")
                        .roles("ADMIN")
                        .build();

        when(auth.getPrincipal()).thenReturn(springUser);
        when(jwtUtils.generateToken("alice")).thenReturn("TOKEN123");

        JwtResponse res = service.authenticateUser(req);

        assertThat(res.getToken()).isEqualTo("TOKEN123");
        assertThat(res.getUsername()).isEqualTo("alice");
        assertThat(res.getRole()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("Register: throw error jika username sudah dipakai")
    void testRegisterUserAlreadyExists() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        UserRepository repo = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtUtils jwt = mock(JwtUtils.class);

        AuthService service = new AuthService(authenticationManager, repo, encoder, jwt);

        RegisterRequest req = new RegisterRequest();
        req.setUsername("john");

        when(repo.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> service.registerUser(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error: Username is already taken!");
    }

    @Test
    @DisplayName("Register: user baru disimpan dengan password terenkripsi")
    void testRegisterUserSuccess() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        UserRepository repo = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtUtils jwt = mock(JwtUtils.class);

        AuthService service = new AuthService(authenticationManager, repo, encoder, jwt);

        RegisterRequest req = new RegisterRequest();
        req.setUsername("bob");
        req.setPassword("123");
        req.setFullName("Bob Marley");
        req.setRole(UserRole.ADMIN);

        when(repo.existsByUsername("bob")).thenReturn(false);
        when(encoder.encode("123")).thenReturn("ENCODED");
        when(repo.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User saved = service.registerUser(req);

        assertThat(saved.getUsername()).isEqualTo("bob");
        assertThat(saved.getFullName()).isEqualTo("Bob Marley");
        assertThat(saved.getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    @DisplayName("Register: default role adalah STAFF ketika request.role null")
    void testRegisterUserDefaultRole() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        UserRepository repo = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtUtils jwt = mock(JwtUtils.class);

        AuthService service = new AuthService(authenticationManager, repo, encoder, jwt);

        RegisterRequest req = new RegisterRequest();
        req.setUsername("neo");
        req.setPassword("matrix");
        req.setFullName("Neo");

        when(repo.existsByUsername("neo")).thenReturn(false);
        when(encoder.encode("matrix")).thenReturn("HASHED");
        when(repo.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User saved = service.registerUser(req);

        assertThat(saved.getRole()).isEqualTo(UserRole.STAFF);
    }
}
