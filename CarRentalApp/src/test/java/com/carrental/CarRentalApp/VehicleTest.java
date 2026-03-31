package com.carrental.CarRentalApp;

import com.carrental.model.vehicle.*;
import com.carrental.reservation.*;
import com.carrental.reservation.ReservationStatus;
import com.carrental.model.user.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class VehicleTest {

    private Car car;

    @BeforeEach
    void setUp() {
        car = new Car.Builder()
                .id(1L).brand("Toyota").model("Corolla")
                .description("Sedan").pricePerHour(50.0)
                .status(VehicleStatus.AVAILABLE).numberOfSeats(5)
                .build();
    }

    @Test
    @DisplayName("Obliczanie kosztu wypożyczenia — 3 godziny")
    void testCalculateCost() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(3);
        assertEquals(150.0, car.calculateCost(start, end), 0.001);
    }

    @Test
    @DisplayName("Nieprawidłowe daty rzucają wyjątek")
    void testCalculateCostInvalidDates() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.minusHours(1);
        assertThrows(IllegalArgumentException.class, () -> car.calculateCost(start, end));
    }

    @Test
    @DisplayName("Zmiana statusu — wysłanie do serwisu")
    void testSendToService() {
        car.sendToService();
        assertEquals(VehicleStatus.IN_SERVICE, car.getStatus());
        assertFalse(car.isAvailable());
    }

    @Test
    @DisplayName("Zmiana statusu — powrót z serwisu")
    void testReturnFromService() {
        car.sendToService();
        car.returnFromService();
        assertEquals(VehicleStatus.AVAILABLE, car.getStatus());
        assertTrue(car.isAvailable());
    }

    @Test
    @DisplayName("Walidacja — ujemna cena")
    void testNegativePriceValidation() {
        assertThrows(IllegalArgumentException.class, () -> car.setPricePerHour(-10.0));
    }

    @Test
    @DisplayName("Builder — brak marki rzuca wyjątek")
    void testBuilderValidation() {
        assertThrows(IllegalStateException.class, () ->
                new Car.Builder().model("X").pricePerHour(50).build());
    }

    @Test
    @DisplayName("ServiceRequest — automatyczna zmiana statusu pojazdu")
    void testServiceRequestChangesVehicleStatus() {
        ServiceRequest req = new ServiceRequest(1L, car, "Awaria silnika");
        assertEquals(VehicleStatus.IN_SERVICE, car.getStatus());
        req.resolve();
        assertEquals(VehicleStatus.AVAILABLE, car.getStatus());
    }

    @Test
    @DisplayName("Reservation Builder — poprawna rezerwacja")
    void testReservationBuilder() {
        Client client = new Client(1L, "Jan", "Kowalski", "jan@test.pl",
                "pass", "123456789", "DL123");
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(5);

        Reservation res = new Reservation.Builder()
                .id(1L).client(client).vehicle(car)
                .startDate(start).endDate(end).build();

        assertEquals(250.0, res.getTotalCost(), 0.001);
        assertEquals(ReservationStatus.PENDING, res.getStatus());
    }

    @Test
    @DisplayName("Reservation Builder — niedostępny pojazd rzuca wyjątek")
    void testReservationUnavailableVehicle() {
        car.sendToService();
        Client client = new Client(1L, "Jan", "Kowalski", "jan@test.pl",
                "pass", "123456789", "DL123");
        assertThrows(IllegalStateException.class, () ->
                new Reservation.Builder()
                        .id(1L).client(client).vehicle(car)
                        .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusHours(2))
                        .build());
    }
}