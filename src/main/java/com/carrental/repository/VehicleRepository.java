package com.carrental.repository;

import com.carrental.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {
    List<VehicleEntity> findByStatus(String status);
    List<VehicleEntity> findByType(String type);
    List<VehicleEntity> findByStatusAndType(String status, String type);
}