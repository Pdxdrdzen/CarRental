package com.carrental.service;

import com.carrental.entity.ReservationEntity;
import com.carrental.entity.VehicleEntity;
import com.carrental.repository.ReservationRepository;
import com.carrental.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final VehicleRepository vehicleRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              VehicleRepository vehicleRepository) {
        this.reservationRepository = reservationRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<ReservationEntity> getAllReservations() {
        return reservationRepository.findAll();
    }

    public List<ReservationEntity> getReservationsByClient(Long clientId) {
        return reservationRepository.findByClientId(clientId);
    }

    public List<ReservationEntity> getReservationsByStatus(String status) {
        return reservationRepository.findByStatus(status);
    }

    public Optional<ReservationEntity> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public ReservationEntity createReservation(ReservationEntity reservation) {
        // Sprawdź czy pojazd jest dostępny
        VehicleEntity vehicle = vehicleRepository.findById(
                        reservation.getVehicle().getId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono pojazdu!"));

        if (!vehicle.getStatus().equals("AVAILABLE")) {
            throw new RuntimeException("Pojazd jest niedostępny!");
        }

        // Oblicz koszt
        long hours = java.time.Duration.between(
                reservation.getStartDate(),
                reservation.getEndDate()
        ).toHours();
        if (hours == 0) hours = 1;
        reservation.setTotalCost(BigDecimal.valueOf(hours).multiply(vehicle.getPricePerHour()));
        reservation.setStatus("PENDING");

        // Zmień status pojazdu na RENTED
        vehicle.setStatus("RENTED");
        vehicleRepository.save(vehicle);

        return reservationRepository.save(reservation);
    }

    public ReservationEntity confirmReservation(Long id) {
        ReservationEntity res = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono rezerwacji o ID: " + id));
        res.setStatus("CONFIRMED");
        return reservationRepository.save(res);
    }

    public ReservationEntity cancelReservation(Long id) {
        ReservationEntity res = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono rezerwacji o ID: " + id));
        res.setStatus("CANCELLED");

        // Zwróć pojazd do puli
        VehicleEntity vehicle = res.getVehicle();
        vehicle.setStatus("AVAILABLE");
        vehicleRepository.save(vehicle);

        return reservationRepository.save(res);
    }

    public ReservationEntity completeReservation(Long id) {
        ReservationEntity res = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono rezerwacji o ID: " + id));
        res.setStatus("COMPLETED");

        // Zwróć pojazd do puli
        VehicleEntity vehicle = res.getVehicle();
        vehicle.setStatus("AVAILABLE");
        vehicleRepository.save(vehicle);

        return reservationRepository.save(res);
    }
}