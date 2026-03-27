package com.buildledger.service;

import com.buildledger.dto.request.DeliveryRequest;
import com.buildledger.dto.response.DeliveryResponse;
import com.buildledger.enums.DeliveryStatus;
import java.util.List;

public interface DeliveryService {
    DeliveryResponse createDelivery(DeliveryRequest request);
    DeliveryResponse getDeliveryById(Long deliveryId);
    List<DeliveryResponse> getAllDeliveries();
    List<DeliveryResponse> getDeliveriesByContract(Long contractId);
    DeliveryResponse updateDeliveryStatus(Long deliveryId, DeliveryStatus status);
    DeliveryResponse updateDelivery(Long deliveryId, DeliveryRequest request);
    void deleteDelivery(Long deliveryId);
}
