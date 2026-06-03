package com.carrental.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private ReservationEntity reservation;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus; // PENDING, COMPLETED, FAILED, REFUNDED

    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod; // CARD, CASH, TRANSFER

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    public PaymentEntity() {}

    // Gettery i settery

    public Long getVersion() { return version; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ReservationEntity getReservation() { return reservation; }
    public void setReservation(ReservationEntity reservation) { this.reservation = reservation; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
}