package com.carrental.repository;

import com.carrental.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findByClientId(Long clientId);
    List<ReservationEntity> findByVehicleId(Long vehicleId);
    List<ReservationEntity> findByStatus(String status);
}