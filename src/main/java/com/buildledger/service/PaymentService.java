package com.buildledger.service;

import com.buildledger.dto.request.PaymentRequest;
import com.buildledger.dto.response.PaymentResponse;
import com.buildledger.enums.PaymentStatus;
import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
    PaymentResponse getPaymentById(Long paymentId);
    List<PaymentResponse> getAllPayments();
    List<PaymentResponse> getPaymentsByInvoice(Long invoiceId);
    PaymentResponse updatePaymentStatus(Long paymentId, PaymentStatus status);
}
