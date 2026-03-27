package com.buildledger.service.impl;

import com.buildledger.dto.request.DeliveryRequest;
import com.buildledger.dto.response.DeliveryResponse;
import com.buildledger.entity.Contract;
import com.buildledger.entity.Delivery;
import com.buildledger.enums.DeliveryStatus;
import com.buildledger.exception.ResourceNotFoundException;
import com.buildledger.repository.ContractRepository;
import com.buildledger.repository.DeliveryRepository;
import com.buildledger.service.DeliveryService;
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
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final ContractRepository contractRepository;

    @Override
    public DeliveryResponse createDelivery(DeliveryRequest request) {
        log.info("Creating delivery for contract {}", request.getContractId());
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException("Contract", "id", request.getContractId()));

        Delivery delivery = Delivery.builder()
                .contract(contract)
                .date(request.getDate())
                .item(request.getItem())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .remarks(request.getRemarks())
                .build();

        return mapToResponse(deliveryRepository.save(delivery));
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryById(Long deliveryId) {
        return mapToResponse(findById(deliveryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponse> getAllDeliveries() {
        return deliveryRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesByContract(Long contractId) {
        return deliveryRepository.findByContractContractId(contractId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public DeliveryResponse updateDeliveryStatus(Long deliveryId, DeliveryStatus status) {
        Delivery delivery = findById(deliveryId);
        delivery.setStatus(status);
        return mapToResponse(deliveryRepository.save(delivery));
    }

    @Override
    public DeliveryResponse updateDelivery(Long deliveryId, DeliveryRequest request) {
        Delivery delivery = findById(deliveryId);
        if (request.getDate() != null) delivery.setDate(request.getDate());
        if (request.getItem() != null) delivery.setItem(request.getItem());
        if (request.getQuantity() != null) delivery.setQuantity(request.getQuantity());
        if (request.getUnit() != null) delivery.setUnit(request.getUnit());
        if (request.getRemarks() != null) delivery.setRemarks(request.getRemarks());
        return mapToResponse(deliveryRepository.save(delivery));
    }

    @Override
    public void deleteDelivery(Long deliveryId) {
        deliveryRepository.delete(findById(deliveryId));
    }

    private Delivery findById(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery", "id", id));
    }

    private DeliveryResponse mapToResponse(Delivery d) {
        return DeliveryResponse.builder()
                .deliveryId(d.getDeliveryId())
                .contractId(d.getContract().getContractId())
                .date(d.getDate())
                .item(d.getItem())
                .quantity(d.getQuantity())
                .unit(d.getUnit())
                .remarks(d.getRemarks())
                .status(d.getStatus())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
