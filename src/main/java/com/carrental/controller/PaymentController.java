package com.carrental.controller;

import com.carrental.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // GET /api/payments
    @GetMapping
    public ResponseEntity<?> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // GET /api/payments/reservation/{reservationId}
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<?> getByReservation(@PathVariable Long reservationId) {
        return paymentService.getPaymentByReservation(reservationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/payments/{reservationId}?method=CARD
    @PostMapping("/{reservationId}")
    public ResponseEntity<?> createPayment(@PathVariable Long reservationId,
                                           @RequestParam String method) {
        try {
            return ResponseEntity.ok(paymentService.createPayment(reservationId, method));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PATCH /api/payments/{id}/refund
    @PatchMapping("/{id}/refund")
    public ResponseEntity<?> refund(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(paymentService.refundPayment(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}