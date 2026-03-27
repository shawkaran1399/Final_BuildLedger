package com.buildledger.service.impl;

import com.buildledger.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class LocalFileStorageService implements FileStorageService {

    @Value("${app.file-storage.base-path:uploads}")
    private String basePath;

    /**
     * Resolves basePath relative to the project working directory (System.getProperty("user.dir")).
     * When running from IntelliJ, user.dir = project root, so files land in:
     *   <project-root>/target/uploads/vendor_{id}/
     * which is visible in the IntelliJ Project panel under target/.
     */
    private Path resolveBaseDir() {
        Path base = Paths.get(basePath);
        return base.isAbsolute() ? base
                : Paths.get(System.getProperty("user.dir")).resolve(base);
    }

    @Override
    public String store(MultipartFile file, Long vendorId) {
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path vendorDir = resolveBaseDir().resolve("vendor_" + vendorId);
            Files.createDirectories(vendorDir);
            Path targetPath = vendorDir.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Stored file: {}", targetPath.toAbsolutePath());
            return targetPath.toAbsolutePath().toString();
        } catch (IOException e) {
            log.error("Failed to store file for vendorId {}: {}", vendorId, e.getMessage());
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    @Override
    public Resource load(String fileUri) {
        try {
            Path filePath = Paths.get(fileUri);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new RuntimeException("File not found or not readable: " + fileUri);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load file: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String fileUri) {
        try {
            Path path = Paths.get(fileUri);
            Files.deleteIfExists(path);
            log.info("Deleted file: {}", fileUri);
        } catch (IOException e) {
            log.warn("Could not delete file {}: {}", fileUri, e.getMessage());
        }
    }
}
