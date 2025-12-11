package org.delcom.app.views;

import org.delcom.app.entities.Product;
import org.delcom.app.entities.User;
import org.delcom.app.entities.UserRole;
import org.delcom.app.repositories.UserRepository;
import org.delcom.app.services.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/products")
public class ProductView {

    private final ProductService productService;
    private final UserRepository userRepository;

    public ProductView(ProductService productService, UserRepository userRepository) {
        this.productService = productService;
        this.userRepository = userRepository;
    }

    // LIST PRODUK (Dipisah berdasarkan Role)
    @GetMapping
    public String listProducts(Model model) {
        User authUser = getAuthenticatedUser();
        if (authUser == null) return "redirect:/auth/login";
        
        model.addAttribute("auth", authUser);
        model.addAttribute("products", productService.getAllActiveProducts());

        // ROUTING BERDASARKAN ROLE
        if (authUser.getRole() == UserRole.ADMIN) {
            return "pages/admin/products"; // File HTML Admin
        } else {
            return "pages/staff/products"; // File HTML Staff
        }
    }

    @GetMapping("/create")
    public String createProductForm(Model model) {
        model.addAttribute("auth", getAuthenticatedUser());
        model.addAttribute("product", new Product()); // Objek kosong
        model.addAttribute("pageTitle", "Tambah Produk Baru");
        return "pages/products/form"; 
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("auth", getAuthenticatedUser());
        try {
            Product product = productService.getProductById(id);
            model.addAttribute("product", product); // Objek dari DB
            model.addAttribute("pageTitle", "Edit Produk: " + product.getName());
            return "pages/products/form";
        } catch (Exception e) {
            return "redirect:/products";
        }
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product, 
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            if (product.getId() == null) {
                // LOGIC CREATE
                productService.createProduct(product, imageFile);
            } else {
                // LOGIC UPDATE
                productService.updateProduct(product.getId(), product, imageFile);
            }
        } catch (Exception e) {
            System.err.println("Gagal menyimpan produk: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @PreAuthorize("hasRole('ADMIN')") 
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
        } catch (Exception e) {
            System.err.println("Gagal menghapus produk: " + e.getMessage());
        }
        // Redirect kembali ke daftar produk setelah dihapus
        return "redirect:/products";
    }

    // ... sisa method save, edit, delete tetap sama ...
    
    // ... private helper getAuthenticatedUser ...
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            UserDetails ud = (UserDetails) auth.getPrincipal();
            return userRepository.findByUsername(ud.getUsername()).orElse(null);
        }
        return null;
    }
}