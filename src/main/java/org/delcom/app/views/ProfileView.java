package org.delcom.app.views;

import org.delcom.app.entities.User;
import org.delcom.app.repositories.UserRepository;
import org.delcom.app.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileView {

    private final UserService userService;
    private final UserRepository userRepository;

    public ProfileView(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String profilePage(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        
        model.addAttribute("auth", user);
        return "pages/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String fullName, 
                                @RequestParam String password, 
                                Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        // Update Nama
        user.setFullName(fullName);
        
        // Update Password (Jika diisi)
        if (password != null && !password.isEmpty()) {
            userService.updatePassword(user.getId(), password);
        } else {
            // Jika nama saja, simpan lewat repo manual agar password tidak berubah
            userRepository.save(user);
        }

        return "redirect:/dashboard";
    }
}