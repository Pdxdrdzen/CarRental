package com.carrental.service;

import com.carrental.entity.PaymentEntity;
import com.carrental.entity.ReservationEntity;
import com.carrental.repository.PaymentRepository;
import com.carrental.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<PaymentEntity> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<PaymentEntity> getPaymentByReservation(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId);
    }

    public PaymentEntity createPayment(Long reservationId, String method) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono rezerwacji o ID: " + reservationId));

        PaymentEntity payment = new PaymentEntity();
        payment.setReservation(reservation);
        payment.setAmount(reservation.getTotalCost());
        payment.setPaymentMethod(method);
        payment.setPaymentStatus("COMPLETED");
        payment.setPaymentDate(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    public PaymentEntity refundPayment(Long id) {
        PaymentEntity payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono płatności o ID: " + id));
        payment.setPaymentStatus("REFUNDED");
        return paymentRepository.save(payment);
    }
}