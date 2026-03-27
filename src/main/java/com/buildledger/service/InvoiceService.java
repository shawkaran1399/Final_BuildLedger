package com.buildledger.service;

import com.buildledger.dto.request.InvoiceRequest;
import com.buildledger.dto.response.InvoiceResponse;
import com.buildledger.enums.InvoiceStatus;
import java.util.List;

public interface InvoiceService {
    InvoiceResponse submitInvoice(InvoiceRequest request);
    InvoiceResponse getInvoiceById(Long invoiceId);
    List<InvoiceResponse> getAllInvoices();
    List<InvoiceResponse> getInvoicesByContract(Long contractId);
    List<InvoiceResponse> getInvoicesByStatus(InvoiceStatus status);
    InvoiceResponse approveInvoice(Long invoiceId);
    InvoiceResponse rejectInvoice(Long invoiceId, String reason);
    void deleteInvoice(Long invoiceId);
}
