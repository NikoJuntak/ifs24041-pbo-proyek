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

        // --- STRICT WORKFLOW SEPARATION ---

        // 1. JIKA USER (PELANGGAN) -> TENDANG KE SHOP
        // Ini mencegah Error 500 karena User tidak punya dashboard data
        if (authUser.getRole() == UserRole.USER) {
            return "redirect:/shop";
        }

        // 2. JIKA ADMIN -> DASHBOARD STRATEGIS
        if (authUser.getRole() == UserRole.ADMIN) {
            try {
                Map<String, Object> summary = dashboardService.getSummaryData();
                model.addAttribute("summary", summary);

                Map<String, Object> chartData = dashboardService.getOrderStatusChartData();
                model.addAttribute("chartLabels", chartData.get("labels"));
                model.addAttribute("chartData", chartData.get("data"));
                
                return ConstUtil.TEMPLATE_DASHBOARD_ADMIN; // admin.html
            } catch (Exception e) {
                return "redirect:/error";
            }
        } 
        
        // 3. JIKA STAFF -> DASHBOARD OPERASIONAL
        else if (authUser.getRole() == UserRole.STAFF) {
            try {
                var newOrders = orderRepository.findByStatus(OrderStatus.NEW);
                model.addAttribute("newOrders", newOrders);
                model.addAttribute("newOrdersCount", newOrders != null ? newOrders.size() : 0);

                List<Product> allProducts = productRepository.findByIsDeletedFalse();
                List<Product> lowStockProducts = allProducts.stream()
                        .filter(p -> p.getStock() < 5)
                        .collect(Collectors.toList());
                model.addAttribute("lowStockProducts", lowStockProducts);

                return ConstUtil.TEMPLATE_DASHBOARD_STAFF; // staff.html
            } catch (Exception e) {
                return "redirect:/error";
            }
        }

        return "redirect:/auth/login"; // Fallback
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            UserDetails ud = (UserDetails) auth.getPrincipal();
            return userRepository.findByUsername(ud.getUsername()).orElse(null);
        }
        return null;
    }
}