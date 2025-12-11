package org.delcom.app.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    // Lokasi folder penyimpanan (akan dibuat otomatis di root project)
    private final Path rootLocation = Paths.get("uploads");

    public FileStorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Gagal membuat folder penyimpanan upload!");
        }
    }

    public String storeFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Gagal menyimpan file kosong.");
            }

            // Generate nama file unik agar tidak bentrok
            // Contoh: laptop.jpg -> 550e8400-e29b...jpg
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            
            Path destinationFile = this.rootLocation.resolve(filename)
                    .normalize().toAbsolutePath();

            // Simpan file
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return filename; // Kembalikan nama file untuk disimpan di DB
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file.", e);
        }
    }

    public void deleteFile(String filename) {
        try {
            if (filename != null) {
                Path file = rootLocation.resolve(filename);
                Files.deleteIfExists(file);
            }
        } catch (IOException e) {
            System.err.println("Gagal menghapus file lama: " + filename);
        }
    }
}