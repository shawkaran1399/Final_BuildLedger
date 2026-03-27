package com.buildledger.service.impl;

import com.buildledger.dto.request.CreateVendorRequest;
import com.buildledger.dto.request.UpdateVendorRequest;
import com.buildledger.dto.response.VendorDocumentResponse;
import com.buildledger.dto.response.VendorResponse;
import com.buildledger.entity.Vendor;
import com.buildledger.entity.VendorDocument;
import com.buildledger.enums.DocumentType;
import com.buildledger.enums.VendorStatus;
import com.buildledger.enums.VerificationStatus;
import com.buildledger.exception.BadRequestException;
import com.buildledger.exception.DuplicateResourceException;
import com.buildledger.exception.ResourceNotFoundException;
import com.buildledger.repository.VendorDocumentRepository;
import com.buildledger.repository.VendorRepository;
import com.buildledger.service.FileStorageService;
import com.buildledger.service.VendorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;
    private final VendorDocumentRepository vendorDocumentRepository;
    private final FileStorageService fileStorageService;

    @Value("${app.document.max-size-mb:10}")
    private long maxFileSizeMb;

    // ── Vendor CRUD ──────────────────────────────────────────────────────────

    @Override
    public VendorResponse createVendor(CreateVendorRequest request) {
        log.info("Creating vendor: {}", request.getName());
        if (request.getEmail() != null && vendorRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Vendor email already exists: " + request.getEmail());
        }
        Vendor vendor = Vendor.builder()
                .name(request.getName())
                .contactInfo(request.getContactInfo())
                .email(request.getEmail())
                .phone(request.getPhone())
                .category(request.getCategory())
                .address(request.getAddress())
                .build();
        return mapToResponse(vendorRepository.save(vendor));
    }

    @Override
    @Transactional(readOnly = true)
    public VendorResponse getVendorById(Long vendorId) {
        return mapToResponse(findVendorById(vendorId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorResponse> getAllVendors() {
        return vendorRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorResponse> getVendorsByStatus(VendorStatus status) {
        return vendorRepository.findByStatus(status).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public VendorResponse updateVendor(Long vendorId, UpdateVendorRequest request) {
        Vendor vendor = findVendorById(vendorId);
        if (request.getName() != null) vendor.setName(request.getName());
        if (request.getContactInfo() != null) vendor.setContactInfo(request.getContactInfo());
        if (request.getEmail() != null) {
            if (!request.getEmail().equals(vendor.getEmail()) && vendorRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Vendor email already exists: " + request.getEmail());
            }
            vendor.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) vendor.setPhone(request.getPhone());
        if (request.getCategory() != null) vendor.setCategory(request.getCategory());
        if (request.getAddress() != null) vendor.setAddress(request.getAddress());
        if (request.getStatus() != null) vendor.setStatus(request.getStatus());
        return mapToResponse(vendorRepository.save(vendor));
    }

    @Override
    public void deleteVendor(Long vendorId) {
        Vendor vendor = findVendorById(vendorId);
        // Clean up stored PDF files from disk before deleting vendor
        List<VendorDocument> docs = vendorDocumentRepository.findByVendorVendorId(vendorId);
        docs.forEach(doc -> fileStorageService.delete(doc.getFileUri()));
        vendorRepository.delete(vendor);
    }

    // ── Document lifecycle ───────────────────────────────────────────────────

    @Override
    public VendorDocumentResponse uploadDocument(Long vendorId, MultipartFile file,
                                                  DocumentType docType, String remarks,
                                                  String uploaderUsername) {
        log.info("Document upload: vendorId={}, docType={}, uploader={}", vendorId, docType, uploaderUsername);

        Vendor vendor = findVendorById(vendorId);

        // ── Validate ─────────────────────────────────────────────────────────
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("No file provided. Please attach a PDF file.");
        }

        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.pdf");

        if (!originalName.toLowerCase().endsWith(".pdf")) {
            throw new BadRequestException("Only PDF files are accepted. Got: " + originalName);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
            throw new BadRequestException(
                    "Invalid content type '" + contentType + "'. Only application/pdf is allowed.");
        }

        long maxBytes = maxFileSizeMb * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new BadRequestException("File size " + (file.getSize() / (1024 * 1024)) +
                    " MB exceeds the allowed limit of " + maxFileSizeMb + " MB.");
        }

        // ── Store via FileStorageService ──────────────────────────────────────
        String fileUri = fileStorageService.store(file, vendorId);

        VendorDocument document = VendorDocument.builder()
                .vendor(vendor)
                .docType(docType)
                .fileUri(fileUri)
                .uploadedDate(LocalDate.now())
                .verificationStatus(VerificationStatus.PENDING)
                .remarks(remarks)
                .build();

        VendorDocument saved = vendorDocumentRepository.save(document);
        log.info("Document saved: id={}, path={}", saved.getDocumentId(), fileUri);
        return mapDocumentToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorDocumentResponse> getVendorDocuments(Long vendorId) {
        findVendorById(vendorId);
        return vendorDocumentRepository.findByVendorVendorId(vendorId)
                .stream().map(this::mapDocumentToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorDocumentResponse> getDocumentsByStatus(VerificationStatus status) {
        return vendorDocumentRepository.findByVerificationStatus(status)
                .stream().map(this::mapDocumentToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadDocument(Long documentId) {
        VendorDocument document = findDocumentById(documentId);
        return fileStorageService.load(document.getFileUri());
    }

    @Override
    public VendorDocumentResponse reviewDocument(Long documentId, VerificationStatus status,
                                                  String reviewRemarks, String reviewerUsername) {
        log.info("Document review: id={}, status={}, reviewer={}", documentId, status, reviewerUsername);

        if (status == VerificationStatus.PENDING) {
            throw new BadRequestException("Review must result in APPROVED or REJECTED.");
        }

        VendorDocument document = findDocumentById(documentId);
        VerificationStatus current = document.getVerificationStatus();

        // Match reference logic: APPROVED→REJECTED and REJECTED→APPROVED both blocked
        if (current == VerificationStatus.APPROVED && status == VerificationStatus.REJECTED) {
            throw new BadRequestException(
                    "Document is already APPROVED and cannot be rejected. Once approved, status cannot be changed.");
        }
        if (current == VerificationStatus.REJECTED && status == VerificationStatus.APPROVED) {
            throw new BadRequestException(
                    "Document is already REJECTED and cannot be approved. Once rejected, status cannot be changed.");
        }
        if (current == status) {
            throw new BadRequestException("Document is already in " + status + " status.");
        }

        document.setVerificationStatus(status);
        document.setReviewedBy(reviewerUsername);
        document.setReviewedAt(LocalDateTime.now());
        document.setReviewRemarks(reviewRemarks);
        vendorDocumentRepository.save(document);

        // Auto-update the vendor's status based on all documents
        autoUpdateVendorStatus(document.getVendor().getVendorId());

        return mapDocumentToResponse(document);
    }

    @Override
    public void autoUpdateVendorStatus(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
        if (vendor == null) return;

        // Only auto-update while vendor is PENDING — manual accept/reject is final
        if (vendor.getStatus() != VendorStatus.PENDING) return;

        List<VendorDocument> documents = vendorDocumentRepository.findByVendorVendorId(vendorId);
        if (documents.isEmpty()) return;

        boolean allApproved = documents.stream()
                .allMatch(d -> d.getVerificationStatus() == VerificationStatus.APPROVED);
        boolean anyRejected = documents.stream()
                .anyMatch(d -> d.getVerificationStatus() == VerificationStatus.REJECTED);

        if (allApproved) {
            vendor.setStatus(VendorStatus.ACTIVE);   // All docs approved → vendor goes ACTIVE
            vendorRepository.save(vendor);
            log.info("Vendor {} auto-promoted to ACTIVE (all documents APPROVED)", vendorId);
        } else if (anyRejected) {
            vendor.setStatus(VendorStatus.SUSPENDED); // Any doc rejected → vendor goes SUSPENDED
            vendorRepository.save(vendor);
            log.info("Vendor {} auto-suspended (document REJECTED)", vendorId);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Vendor findVendorById(Long vendorId) {
        return vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", vendorId));
    }

    private VendorDocument findDocumentById(Long documentId) {
        return vendorDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("VendorDocument", "id", documentId));
    }

    private VendorResponse mapToResponse(Vendor vendor) {
        return VendorResponse.builder()
                .vendorId(vendor.getVendorId())
                .name(vendor.getName())
                .contactInfo(vendor.getContactInfo())
                .email(vendor.getEmail())
                .phone(vendor.getPhone())
                .category(vendor.getCategory())
                .address(vendor.getAddress())
                .status(vendor.getStatus())
                .createdAt(vendor.getCreatedAt())
                .updatedAt(vendor.getUpdatedAt())
                .build();
    }

    private VendorDocumentResponse mapDocumentToResponse(VendorDocument doc) {
        // Extract just the filename from the full path for display
        String fileUri = doc.getFileUri();
        String displayName = fileUri != null
                ? fileUri.replaceAll(".*[\\\\/]", "")
                : "unknown.pdf";

        return VendorDocumentResponse.builder()
                .documentId(doc.getDocumentId())
                .vendorId(doc.getVendor().getVendorId())
                .vendorName(doc.getVendor().getName())
                .docType(doc.getDocType())
                .fileUri(displayName)
                .uploadedDate(doc.getUploadedDate())
                .verificationStatus(doc.getVerificationStatus())
                .remarks(doc.getRemarks())
                .reviewedBy(doc.getReviewedBy())
                .reviewedAt(doc.getReviewedAt())
                .reviewRemarks(doc.getReviewRemarks())
                .createdAt(doc.getCreatedAt())
                .build();
    }
}
