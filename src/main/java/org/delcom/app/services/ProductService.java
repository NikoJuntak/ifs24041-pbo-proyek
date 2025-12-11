package org.delcom.app.services;

import org.delcom.app.entities.Product;
import org.delcom.app.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile; 

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService; 

    public ProductService(ProductRepository productRepository, FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
    }

    // Tampilkan hanya yang TIDAK dihapus
    public List<Product> getAllActiveProducts() {
        return productRepository.findByIsDeletedFalse();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .filter(p -> !p.isDeleted()) // Pastikan tidak terhapus
                .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan atau sudah dihapus"));
    }

    @Transactional
    public Product createProduct(Product product, MultipartFile imageFile) {
        // Jika ada file gambar yang diupload
        if (imageFile != null && !imageFile.isEmpty()) {
            String filename = fileStorageService.storeFile(imageFile);
            product.setImage(filename);
        }
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product updatedData, MultipartFile imageFile) {
        Product existingProduct = getProductById(id);

        // Update data text
        existingProduct.setName(updatedData.getName());
        existingProduct.setCategory(updatedData.getCategory());
        existingProduct.setPrice(updatedData.getPrice());
        existingProduct.setStock(updatedData.getStock());
        existingProduct.setDescription(updatedData.getDescription());

        // LOGIKA GANTI GAMBAR
        // Jika user mengupload gambar baru
        if (imageFile != null && !imageFile.isEmpty()) {
            // 1. Hapus gambar lama dari folder (jika ada)
            if (existingProduct.getImage() != null) {
                fileStorageService.deleteFile(existingProduct.getImage());
            }

            // 2. Simpan gambar baru
            String newFilename = fileStorageService.storeFile(imageFile);
            existingProduct.setImage(newFilename);
        }

        return productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        // Soft Delete: Set flag true, jangan hapus fisik datanya
        product.setDeleted(true);
        productRepository.save(product);
    }
}