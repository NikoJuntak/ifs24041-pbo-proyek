package org.delcom.app.views;

import org.delcom.app.entities.Product;
import org.delcom.app.entities.User;
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

    // 1. TAMPILKAN LIST PRODUK
    @GetMapping
    public String listProducts(Model model) {
        User authUser = getAuthenticatedUser();
        model.addAttribute("auth", authUser);
        model.addAttribute("products", productService.getAllActiveProducts());
        return "pages/products/list"; // Kita akan buat file ini
    }

    // 2. FORM TAMBAH PRODUK
    @GetMapping("/create")
    public String createProductForm(Model model) {
        model.addAttribute("auth", getAuthenticatedUser());
        model.addAttribute("product", new Product());
        model.addAttribute("pageTitle", "Tambah Produk Baru");
        return "pages/products/form"; // Kita akan buat file ini
    }

    // 3. FORM EDIT PRODUK
    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("auth", getAuthenticatedUser());
        try {
            Product product = productService.getProductById(id);
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", "Edit Produk");
            return "pages/products/form";
        } catch (Exception e) {
            return "redirect:/products";
        }
    }

    // 4. SIMPAN DATA (CREATE / UPDATE)
    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product, 
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            if (product.getId() == null) {
                // Mode Create
                productService.createProduct(product, imageFile);
            } else {
                // Mode Update
                productService.updateProduct(product.getId(), product, imageFile);
            }
        } catch (Exception e) {
            System.out.println("Error saving product: " + e.getMessage());
        }
        return "redirect:/products";
    }

    // 5. HAPUS PRODUK (Soft Delete) - Admin Only
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
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