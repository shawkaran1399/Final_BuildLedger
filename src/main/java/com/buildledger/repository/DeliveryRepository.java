package com.buildledger.repository;

import com.buildledger.entity.Delivery;
import com.buildledger.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByContractContractId(Long contractId);
    List<Delivery> findByStatus(DeliveryStatus status);
    List<Delivery> findByContractContractIdAndStatus(Long contractId, DeliveryStatus status);
}
