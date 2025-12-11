package org.delcom.app.views;

import org.delcom.app.dto.auth.RegisterRequest;
import org.delcom.app.utils.ConstUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;




@Controller
@RequestMapping("/auth")
public class AuthView {

    @GetMapping("/login")
    public String loginPage() {
        // Jika user sudah login, tendang ke dashboard
        if (isAuthenticated()) {
            return "redirect:/dashboard";
        }
        // Mengembalikan nama file template login
        return ConstUtil.TEMPLATE_AUTH_LOGIN; 
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Cek apakah user sedang login
        boolean isLoggedIn = auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);

        if (isLoggedIn) {
            // Jika Admin yang login, dia mungkin mau nambah staff -> Biarkan akses (opsional)
            // Atau jika User biasa yang login, redirect ke shop agar tidak daftar ulang
            boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin) {
                return "redirect:/shop";
            }
        }

        // Jika belum login (Public), tampilkan form register pelanggan
        model.addAttribute("registerRequest", new RegisterRequest());
        return ConstUtil.TEMPLATE_AUTH_REGISTER;
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // 1. Bersihkan Security Context di Server
        SecurityContextHolder.clearContext();

        // 2. Hapus Cookie 'AUTH_TOKEN' dari sisi Server (Double protection)
        Cookie cookie = new Cookie("AUTH_TOKEN", null);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setMaxAge(0); // Set umur cookie jadi 0 (langsung mati)
        response.addCookie(cookie);

        // 3. Redirect ke halaman login
        return "redirect:/auth/login?logout=true";
    }

    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && 
               auth.isAuthenticated() && 
               !(auth instanceof AnonymousAuthenticationToken);
    }
}