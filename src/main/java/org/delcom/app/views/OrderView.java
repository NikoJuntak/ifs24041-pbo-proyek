package org.delcom.app.views;

import org.delcom.app.entities.User;
import org.delcom.app.repositories.UserRepository;
import org.delcom.app.services.OrderService;
import org.delcom.app.services.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orders")
public class OrderView {

    private final OrderService orderService;
    private final ProductService productService; // Tambahkan ini
    private final UserRepository userRepository;

    public OrderView(OrderService orderService, 
                     ProductService productService, 
                     UserRepository userRepository) {
        this.orderService = orderService;
        this.productService = productService;
        this.userRepository = userRepository;
    }

    // LIST ORDERS
    @GetMapping
    public String listOrders(Model model) {
        User authUser = getAuthenticatedUser();
        model.addAttribute("auth", authUser);
        model.addAttribute("orders", orderService.getAllOrders());
        return "pages/orders/list";
    }

    // FORM CREATE ORDER (Ini yang tadi saya tambahkan)
    @GetMapping("/create")
    public String createOrderForm(Model model) {
        User authUser = getAuthenticatedUser();
        model.addAttribute("auth", authUser);
        
        // Kirim list produk untuk dropdown
        model.addAttribute("products", productService.getAllActiveProducts());
        
        return "pages/orders/form-create";
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