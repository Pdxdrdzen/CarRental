package com.carrental.pattern;

import com.carrental.model.vehicle.Car;
import com.carrental.model.vehicle.Bus;
import com.carrental.model.vehicle.VehicleStatus;
import com.carrental.model.reservation.Reservation;
import com.carrental.model.reservation.ReservationStatus;
import com.carrental.model.user.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Wzorzec Facade (Reservation jako fasada domenowa)")
class FacadePatternTest {

    private Client client;
    private Car availableCar;

    @BeforeEach
    void setUp() {
        client = new Client(1L, "Adam", "Mickiewicz",
                "adam@example.com", "haslo", "500600700", "PL12345");
        availableCar = new Car.Builder()
                .id(100L).brand("Mazda").model("CX-5")
                .pricePerHour(60.0).status(VehicleStatus.AVAILABLE).build();
    }

    @Test
    @DisplayName("Fasada: Builder ukrywa obliczanie kosztu")
    void facadeHidesCostCalculation() {
        LocalDateTime start = LocalDateTime.of(2024, 10, 1, 8, 0);
        LocalDateTime end   = LocalDateTime.of(2024, 10, 1, 13, 0); // 5h

        Reservation res = new Reservation.Builder()
                .client(client).vehicle(availableCar)
                .startDate(start).endDate(end).build();

        assertEquals(300.0, res.getTotalCost(), 0.001);
    }

    @Test
    @DisplayName("Fasada: Builder automatycznie ustawia status PENDING")
    void facadeSetsPendingStatus() {
        Reservation res = new Reservation.Builder()
                .client(client).vehicle(availableCar)
                .startDate(LocalDateTime.now().plusHours(1))
                .endDate(LocalDateTime.now().plusHours(4)).build();

        assertEquals(ReservationStatus.PENDING, res.getStatus());
    }

    @Test
    @DisplayName("Fasada: walidacja niedostępnego pojazdu ukryta wewnątrz Buildera")
    void facadeValidatesVehicleAvailability() {
        Car rentedCar = new Car.Builder().brand("Honda").model("Civic")
                .status(VehicleStatus.RENTED).build();

        assertThrows(IllegalStateException.class, () ->
                new Reservation.Builder().client(client).vehicle(rentedCar)
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now().plusHours(2)).build()
        );
    }

    @Test
    @DisplayName("Fasada: Builder akceptuje różne typy pojazdów (Car i Bus)")
    void facadeWorksWithDifferentVehicleTypes() {
        Bus bus = new Bus.Builder().brand("Scania").model("Citywide")
                .pricePerHour(120.0).status(VehicleStatus.AVAILABLE).build();

        LocalDateTime start = LocalDateTime.of(2024, 11, 1, 6, 0);
        LocalDateTime end   = LocalDateTime.of(2024, 11, 1, 10, 0); // 4h

        Reservation res = new Reservation.Builder()
                .client(client).vehicle(bus)
                .startDate(start).endDate(end).build();

        assertEquals(480.0, res.getTotalCost(), 0.001);
    }

    @Test
    @DisplayName("Fasada: poprawne przypisanie klienta i pojazdu do rezerwacji")
    void facadeCorrectlyAssignsClientAndVehicle() {
        Reservation res = new Reservation.Builder()
                .client(client).vehicle(availableCar)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(1).plusHours(3)).build();

        assertAll(
                () -> assertEquals(client, res.getClient()),
                () -> assertEquals(availableCar, res.getVehicle()),
                () -> assertNotNull(res.getStartDate()),
                () -> assertNotNull(res.getEndDate())
        );
    }
}