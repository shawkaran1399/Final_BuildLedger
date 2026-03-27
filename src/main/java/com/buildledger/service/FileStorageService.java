package com.buildledger.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Abstraction for file storage.
 * Swap implementations for local disk, AWS S3, Azure Blob, GCP Storage, etc.
 */
public interface FileStorageService {

    /**
     * Stores the uploaded file under a vendor-specific subdirectory.
     * Returns the full path/URI string that can be persisted to the database.
     */
    String store(MultipartFile file, Long vendorId);

    /**
     * Loads a previously stored file as a Spring Resource for download/streaming.
     */
    Resource load(String fileUri);

    /**
     * Deletes a stored file from disk. No-op if the file does not exist.
     */
    void delete(String fileUri);
}
