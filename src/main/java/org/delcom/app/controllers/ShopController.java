package org.delcom.app.controllers;

import org.delcom.app.entities.User;
import org.delcom.app.repositories.OrderRepository;
import org.delcom.app.repositories.ProductRepository;
import org.delcom.app.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/shop")
public class ShopController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public ShopController(ProductRepository productRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // 1. Halaman Katalog (Belanja)
    @GetMapping
    public String catalog(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) return "redirect:/auth/login";

        model.addAttribute("auth", user);
        // Tampilkan produk yang belum dihapus
        model.addAttribute("products", productRepository.findByIsDeletedFalse());
        
        return "pages/shop/catalog"; // Kita akan buat file ini di langkah 3
    }

    // 2. Halaman Riwayat Pesanan
    @GetMapping("/my-orders")
    public String myOrders(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) return "redirect:/auth/login";

        model.addAttribute("auth", user);
        
        // Filter order berdasarkan nama customer (sesuai logika sederhana kita)
        var myOrders = orderRepository.findAll().stream()
                .filter(o -> o.getCustomerName().equals(user.getFullName()))
                .toList();

        model.addAttribute("orders", myOrders);
        return "pages/shop/my-orders";
    }

    private User getAuthenticatedUser() {
        try {
            var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                return userRepository.findByUsername(((UserDetails) principal).getUsername()).orElse(null);
            }
        } catch (Exception e) { return null; }
        return null;
    }
}