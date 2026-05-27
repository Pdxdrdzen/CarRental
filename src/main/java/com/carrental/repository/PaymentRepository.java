package com.carrental.repository;

import com.carrental.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByReservationId(Long reservationId);
    List<PaymentEntity> findByPaymentStatus(String paymentStatus);
}