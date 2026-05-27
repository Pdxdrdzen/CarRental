package com.carrental.repository;

import com.carrental.entity.ServiceRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequestEntity, Long> {
    List<ServiceRequestEntity> findByVehicleId(Long vehicleId);
    List<ServiceRequestEntity> findByStatus(String status);
}