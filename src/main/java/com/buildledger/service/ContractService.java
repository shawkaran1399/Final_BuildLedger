package com.buildledger.service;

import com.buildledger.dto.request.ContractRequest;
import com.buildledger.dto.request.ContractTermRequest;
import com.buildledger.dto.response.ContractResponse;
import com.buildledger.dto.response.ContractTermResponse;
import com.buildledger.enums.ContractStatus;
import java.util.List;

public interface ContractService {
    ContractResponse createContract(ContractRequest request);
    ContractResponse getContractById(Long contractId);
    List<ContractResponse> getAllContracts();
    List<ContractResponse> getContractsByVendor(Long vendorId);
    List<ContractResponse> getContractsByProject(Long projectId);
    List<ContractResponse> getContractsByStatus(ContractStatus status);
    ContractResponse updateContractStatus(Long contractId, ContractStatus status);
    ContractResponse updateContract(Long contractId, ContractRequest request);
    void deleteContract(Long contractId);
    ContractTermResponse addContractTerm(Long contractId, ContractTermRequest request);
    List<ContractTermResponse> getContractTerms(Long contractId);
    ContractTermResponse editContractTerm(Long termId, ContractTermRequest request);
    void deleteContractTerm(Long termId);
}
