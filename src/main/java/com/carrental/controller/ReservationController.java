package com.carrental.controller;

import com.carrental.entity.ReservationEntity;
import com.carrental.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // GET /api/reservations
    @GetMapping
    public List<ReservationEntity> getAllReservations() {
        return reservationService.getAllReservations();
    }

    // GET /api/reservations/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ReservationEntity> getById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/reservations/client/{clientId}
    @GetMapping("/client/{clientId}")
    public List<ReservationEntity> getByClient(@PathVariable Long clientId) {
        return reservationService.getReservationsByClient(clientId);
    }

    // GET /api/reservations/status/{status}
    @GetMapping("/status/{status}")
    public List<ReservationEntity> getByStatus(@PathVariable String status) {
        return reservationService.getReservationsByStatus(status);
    }

    // POST /api/reservations
    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationEntity reservation) {
        try {
            return ResponseEntity.ok(reservationService.createReservation(reservation));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PATCH /api/reservations/{id}/confirm
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<?> confirm(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reservationService.confirmReservation(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PATCH /api/reservations/{id}/cancel
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reservationService.cancelReservation(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PATCH /api/reservations/{id}/complete
    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reservationService.completeReservation(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}