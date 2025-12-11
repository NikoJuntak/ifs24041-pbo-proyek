package org.delcom.app.views;

import org.delcom.app.dto.auth.RegisterRequest;
import org.delcom.app.utils.ConstUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

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
        
        // 1. Jika belum login -> Boleh Register (User Baru Mandiri)
        // 2. Jika sudah login DAN role-nya ADMIN -> Boleh Register (Nambah Staff)
        // 3. Jika login TAPI bukan admin -> Redirect Dashboard
        
        boolean isLoggedIn = auth != null && 
                           auth.isAuthenticated() && 
                           !(auth instanceof AnonymousAuthenticationToken);
        
        boolean isAdmin = false;
        
        // Periksa role admin dengan pengecekan null yang aman
        if (isLoggedIn) {
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            if (authorities != null) {
                isAdmin = authorities.stream()
                    .anyMatch(a -> a.getAuthority() != null && 
                                  a.getAuthority().equals("ROLE_ADMIN"));
            }
        }

        if (isLoggedIn && !isAdmin) {
            return "redirect:/dashboard";
        }

        // Kirim object kosong untuk form
        model.addAttribute("registerRequest", new RegisterRequest());
        
        // Tambahkan informasi tambahan untuk template
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isLoggedIn", isLoggedIn);
        
        return ConstUtil.TEMPLATE_AUTH_REGISTER;
    }

    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && 
               auth.isAuthenticated() && 
               !(auth instanceof AnonymousAuthenticationToken);
    }
}