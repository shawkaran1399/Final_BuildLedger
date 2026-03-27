package com.buildledger.entity;

import com.buildledger.enums.DocumentType;
import com.buildledger.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_documents")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false)
    private DocumentType docType;

    /** Full path on disk where the PDF is stored — used for both serving and display */
    @Column(name = "file_uri", nullable = false, length = 1000)
    private String fileUri;

    @Column(name = "uploaded_date")
    private LocalDate uploadedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    /** Username of the reviewer (PM/ADMIN) who approved or rejected */
    @Column(name = "reviewed_by", length = 100)
    private String reviewedBy;

    /** Timestamp of the review decision */
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    /** Reason given by reviewer */
    @Column(name = "review_remarks", columnDefinition = "TEXT")
    private String reviewRemarks;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
