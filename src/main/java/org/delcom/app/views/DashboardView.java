package org.delcom.app.views;

import org.delcom.app.entities.OrderStatus;
import org.delcom.app.entities.Product;
import org.delcom.app.entities.User;
import org.delcom.app.entities.UserRole;
import org.delcom.app.repositories.OrderRepository;
import org.delcom.app.repositories.ProductRepository;
import org.delcom.app.repositories.UserRepository;
import org.delcom.app.services.DashboardService;
import org.delcom.app.utils.ConstUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardView {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public DashboardView(DashboardService dashboardService, UserRepository userRepository, 
                         OrderRepository orderRepository, ProductRepository productRepository) {
        this.dashboardService = dashboardService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User authUser = getAuthenticatedUser();
        if (authUser == null) return "redirect:/auth/login";
        model.addAttribute("auth", authUser);

        // --- ROUTING BERDASARKAN ROLE ---

        if (authUser.getRole() == UserRole.ADMIN) {
            // 1. DATA UNTUK ADMIN (Statistik & Uang)
            Map<String, Object> summary = dashboardService.getSummaryData();
            model.addAttribute("summary", summary);

            Map<String, Object> chartData = dashboardService.getOrderStatusChartData();
            model.addAttribute("chartLabels", chartData.get("labels"));
            model.addAttribute("chartData", chartData.get("data"));

            return ConstUtil.TEMPLATE_DASHBOARD_ADMIN; // Arahkan ke admin.html
        } 
        else {
            // 2. DATA UNTUK STAFF (Tugas & Stok)
            // Staff butuh: List Pesanan Baru & Peringatan Stok
            // Staff TIDAK butuh data keuangan/grafik (lebih ringan)
            
            List<Product> allProducts = productRepository.findByIsDeletedFalse();
            
            // 1. Order yg harus diproses (NEW)
            var newOrders = orderRepository.findByStatus(OrderStatus.NEW);
            
            // 2. Stok Menipis
            List<Product> lowStockProducts = allProducts.stream()
                    .filter(p -> p.getStock() < 5)
                    .collect(Collectors.toList());

            model.addAttribute("newOrders", newOrders);
            model.addAttribute("newOrdersCount", newOrders.size());
            model.addAttribute("lowStockProducts", lowStockProducts);

            // Return ke View Staff
            return ConstUtil.TEMPLATE_DASHBOARD_STAFF; // Arahkan ke staff.html
        }
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        try {
            UserDetails ud = (UserDetails) auth.getPrincipal();
            return userRepository.findByUsername(ud.getUsername()).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}