package com.buildledger.repository;

import com.buildledger.entity.Service;
import com.buildledger.enums.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByContractContractId(Long contractId);
    List<Service> findByStatus(ServiceStatus status);
    List<Service> findByContractContractIdAndStatus(Long contractId, ServiceStatus status);
}
