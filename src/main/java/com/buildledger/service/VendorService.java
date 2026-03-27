package com.buildledger.service;

import com.buildledger.dto.request.CreateVendorRequest;
import com.buildledger.dto.request.UpdateVendorRequest;
import com.buildledger.dto.response.VendorDocumentResponse;
import com.buildledger.dto.response.VendorResponse;
import com.buildledger.enums.DocumentType;
import com.buildledger.enums.VendorStatus;
import com.buildledger.enums.VerificationStatus;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VendorService {

    // ── Vendor CRUD ──────────────────────────────────────────────────────────
    VendorResponse createVendor(CreateVendorRequest request);
    VendorResponse getVendorById(Long vendorId);
    List<VendorResponse> getAllVendors();
    List<VendorResponse> getVendorsByStatus(VendorStatus status);
    VendorResponse updateVendor(Long vendorId, UpdateVendorRequest request);
    void deleteVendor(Long vendorId);

    // ── Document lifecycle ───────────────────────────────────────────────────

    /** VENDOR uploads a PDF — validates, stores, returns PENDING document record */
    VendorDocumentResponse uploadDocument(Long vendorId, MultipartFile file,
                                          DocumentType docType, String remarks,
                                          String uploaderUsername);

    /** All documents for a vendor */
    List<VendorDocumentResponse> getVendorDocuments(Long vendorId);

    /** All documents filtered by verification status */
    List<VendorDocumentResponse> getDocumentsByStatus(VerificationStatus status);

    /** Stream the PDF from disk */
    Resource downloadDocument(Long documentId);

    /**
     * PROJECT_MANAGER / ADMIN reviews a document (APPROVED or REJECTED).
     * After every review, triggers autoUpdateVendorStatus.
     */
    VendorDocumentResponse reviewDocument(Long documentId, VerificationStatus status,
                                          String reviewRemarks, String reviewerUsername);

    /**
     * Automatically updates the parent vendor's status based on its documents:
     *  - All docs APPROVED  → vendor becomes ACTIVE
     *  - Any doc REJECTED   → vendor becomes REJECTED
     *  - Otherwise          → vendor stays PENDING
     * Only acts when vendor is currently PENDING.
     */
    void autoUpdateVendorStatus(Long vendorId);
}
