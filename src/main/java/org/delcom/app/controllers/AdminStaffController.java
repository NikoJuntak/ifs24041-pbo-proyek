package org.delcom.app.controllers;

import org.delcom.app.entities.User;
import org.delcom.app.repositories.UserRepository;
import org.delcom.app.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/staff")
@PreAuthorize("hasRole('ADMIN')") // Kunci hanya untuk Admin
public class AdminStaffController {

    private final UserService userService;
    private final UserRepository userRepository;

    public AdminStaffController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // 1. List Staff
    @GetMapping
    public String listStaff(Model model) {
        model.addAttribute("auth", getAuthenticatedUser());
        model.addAttribute("staffList", userService.getAllStaff());
        return "pages/admin/staff/list";
    }

    // 2. Form Tambah Staff
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("auth", getAuthenticatedUser());
        model.addAttribute("staff", new User());
        return "pages/admin/staff/form";
    }

    // 3. Simpan Staff
    @PostMapping("/save")
    public String saveStaff(@ModelAttribute User staff) {
        try {
            // Kita pakai raw password dari form sementara untuk di-encode di service
            userService.createStaff(staff.getFullName(), staff.getUsername(), staff.getPassword());
            return "redirect:/admin/staff?success";
        } catch (Exception e) {
            return "redirect:/admin/staff/create?error=" + e.getMessage();
        }
    }

    // 4. Hapus Staff
    @GetMapping("/delete/{id}")
    public String deleteStaff(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/staff";
    }

    private User getAuthenticatedUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(((UserDetails) principal).getUsername()).orElse(null);
    }
}