package com.buildledger.service.impl;

import com.buildledger.dto.request.PaymentRequest;
import com.buildledger.dto.response.PaymentResponse;
import com.buildledger.entity.Invoice;
import com.buildledger.entity.Payment;
import com.buildledger.enums.InvoiceStatus;
import com.buildledger.enums.PaymentStatus;
import com.buildledger.exception.BadRequestException;
import com.buildledger.exception.ResourceNotFoundException;
import com.buildledger.repository.InvoiceRepository;
import com.buildledger.repository.PaymentRepository;
import com.buildledger.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment for invoice {}", request.getInvoiceId());
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", request.getInvoiceId()));

        if (invoice.getStatus() != InvoiceStatus.APPROVED) {
            throw new BadRequestException("Payment can only be processed for APPROVED invoices. Current status: " + invoice.getStatus());
        }

        Payment payment = Payment.builder()
                .invoice(invoice)
                .amount(request.getAmount())
                .date(request.getDate())
                .method(request.getMethod())
                .status(PaymentStatus.PENDING)
                .transactionReference(request.getTransactionReference())
                .remarks(request.getRemarks())
                .build();

        Payment saved = paymentRepository.save(payment);

        // Mark invoice as PAID after payment is processed
        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);

        saved.setStatus(PaymentStatus.COMPLETED);
        return mapToResponse(paymentRepository.save(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long paymentId) {
        return mapToResponse(findById(paymentId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByInvoice(Long invoiceId) {
        return paymentRepository.findByInvoiceInvoiceId(invoiceId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public PaymentResponse updatePaymentStatus(Long paymentId, PaymentStatus status) {
        Payment payment = findById(paymentId);
        payment.setStatus(status);
        return mapToResponse(paymentRepository.save(payment));
    }

    private Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
    }

    private PaymentResponse mapToResponse(Payment p) {
        return PaymentResponse.builder()
                .paymentId(p.getPaymentId())
                .invoiceId(p.getInvoice().getInvoiceId())
                .amount(p.getAmount())
                .date(p.getDate())
                .method(p.getMethod())
                .status(p.getStatus())
                .transactionReference(p.getTransactionReference())
                .remarks(p.getRemarks())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
