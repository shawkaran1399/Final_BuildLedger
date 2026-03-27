package com.buildledger.service;

import com.buildledger.dto.request.ServiceRequest;
import com.buildledger.dto.response.ServiceResponse;
import com.buildledger.enums.ServiceStatus;
import java.util.List;

public interface ServiceTrackingService {
    ServiceResponse createService(ServiceRequest request);
    ServiceResponse getServiceById(Long serviceId);
    List<ServiceResponse> getAllServices();
    List<ServiceResponse> getServicesByContract(Long contractId);
    ServiceResponse updateServiceStatus(Long serviceId, ServiceStatus status);
    ServiceResponse updateService(Long serviceId, ServiceRequest request);
    void deleteService(Long serviceId);
}
