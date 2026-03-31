package com.carrental.reservation;

import java.time.LocalDateTime;

public class Payment {
    private Long id;
    private Reservation reservation;
    private double amount;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private LocalDateTime paymentDate;

    public Payment(Long id, Reservation reservation, PaymentMethod paymentMethod) {
        this.id = id;
        this.reservation = reservation;
        this.amount = reservation.getTotalCost();
        this.paymentStatus = PaymentStatus.PENDING;
        this.paymentMethod = paymentMethod;
        this.paymentDate = LocalDateTime.now();
    }

    public void complete() { this.paymentStatus = PaymentStatus.COMPLETED; }
    public void fail() { this.paymentStatus = PaymentStatus.FAILED; }
    public void refund() { this.paymentStatus = PaymentStatus.REFUNDED; }

    public Long getId() { return id; }
    public Reservation getReservation() { return reservation; }
    public double getAmount() { return amount; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
}