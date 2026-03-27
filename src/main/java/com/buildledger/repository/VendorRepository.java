package com.buildledger.repository;

import com.buildledger.entity.Vendor;
import com.buildledger.enums.VendorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Vendor> findByStatus(VendorStatus status);
    List<Vendor> findByCategory(String category);
}
