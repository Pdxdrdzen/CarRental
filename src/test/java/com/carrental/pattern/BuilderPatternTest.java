package com.carrental.pattern;

import com.carrental.model.vehicle.Car;
import com.carrental.model.vehicle.Bus;
import com.carrental.model.vehicle.VehicleStatus;
import com.carrental.model.reservation.Reservation;
import com.carrental.model.reservation.ReservationStatus;
import com.carrental.model.user.Client;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Wzorzec Builder")
class BuilderPatternTest {

    @Test
    @DisplayName("Car.Builder tworzy obiekt z poprawnymi danymi")
    void carBuilderCreatesObjectWithCorrectData() {
        Car car = new Car.Builder()
                .id(1L).brand("Toyota").model("Corolla")
                .description("Sedan").pricePerHour(25.0)
                .numberOfSeats(5).status(VehicleStatus.AVAILABLE)
                .build();
        assertAll(
                () -> assertEquals(1L, car.getId()),
                () -> assertEquals("Toyota", car.getBrand()),
                () -> assertEquals("Corolla", car.getModel()),
                () -> assertEquals(25.0, car.getPricePerHour()),
                () -> assertEquals(5, car.getNumberOfSeats()),
                () -> assertEquals(VehicleStatus.AVAILABLE, car.getStatus())
        );
    }

    @Test
    @DisplayName("Car.Builder rzuca wyjątek gdy brak marki")
    void carBuilderThrowsWhenBrandMissing() {
        assertThrows(IllegalStateException.class, () ->
                new Car.Builder().model("Corolla").build()
        );
    }

    @Test
    @DisplayName("Car.Builder rzuca wyjątek gdy brak modelu")
    void carBuilderThrowsWhenModelMissing() {
        assertThrows(IllegalStateException.class, () ->
                new Car.Builder().brand("Toyota").build()
        );
    }

    @Test
    @DisplayName("Car.Builder domyślnie ustawia status AVAILABLE")
    void carBuilderDefaultStatusIsAvailable() {
        Car car = new Car.Builder().brand("BMW").model("X5").build();
        assertEquals(VehicleStatus.AVAILABLE, car.getStatus());
    }

    @Test
    @DisplayName("Bus.Builder tworzy obiekt z poprawnymi danymi")
    void busBuilderCreatesObjectWithCorrectData() {
        Bus bus = new Bus.Builder()
                .id(10L).brand("Mercedes").model("Sprinter")
                .pricePerHour(80.0).numberOfDoors(3)
                .status(VehicleStatus.AVAILABLE).build();
        assertAll(
                () -> assertEquals(10L, bus.getId()),
                () -> assertEquals("Mercedes", bus.getBrand()),
                () -> assertEquals(80.0, bus.getPricePerHour()),
                () -> assertEquals(3, bus.getNumberOfDoors())
        );
    }

    @Test
    @DisplayName("Bus.Builder rzuca wyjątek gdy brak marki")
    void busBuilderThrowsWhenBrandMissing() {
        assertThrows(IllegalStateException.class, () ->
                new Bus.Builder().model("Sprinter").build()
        );
    }

    @Test
    @DisplayName("Reservation.Builder tworzy rezerwację z obliczonym kosztem")
    void reservationBuilderCalculatesCost() {
        Client client = new Client(1L, "Jan", "Kowalski",
                "jan@example.com", "pass", "123456789", "DL001");
        Car car = new Car.Builder().brand("Audi").model("A4")
                .pricePerHour(50.0).status(VehicleStatus.AVAILABLE).build();

        LocalDateTime start = LocalDateTime.of(2024, 6, 1, 10, 0);
        LocalDateTime end   = LocalDateTime.of(2024, 6, 1, 14, 0); // 4h

        Reservation res = new Reservation.Builder()
                .client(client).vehicle(car)
                .startDate(start).endDate(end).build();

        assertAll(
                () -> assertEquals(200.0, res.getTotalCost(), 0.001),
                () -> assertEquals(ReservationStatus.PENDING, res.getStatus()),
                () -> assertEquals(client, res.getClient()),
                () -> assertEquals(car, res.getVehicle())
        );
    }

    @Test
    @DisplayName("Reservation.Builder rzuca wyjątek gdy brak klienta")
    void reservationBuilderThrowsWhenClientMissing() {
        Car car = new Car.Builder().brand("Ford").model("Focus")
                .status(VehicleStatus.AVAILABLE).build();
        assertThrows(IllegalStateException.class, () ->
                new Reservation.Builder().vehicle(car)
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now().plusHours(2)).build()
        );
    }

    @Test
    @DisplayName("Reservation.Builder rzuca wyjątek gdy pojazd niedostępny")
    void reservationBuilderThrowsWhenVehicleUnavailable() {
        Client client = new Client(2L, "Anna", "Nowak",
                "anna@example.com", "pass", "987654321", "DL002");
        Car car = new Car.Builder().brand("Ford").model("Focus")
                .status(VehicleStatus.RENTED).build();
        assertThrows(IllegalStateException.class, () ->
                new Reservation.Builder().client(client).vehicle(car)
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now().plusHours(2)).build()
        );
    }

    @Test
    @DisplayName("Reservation.Builder rzuca wyjątek gdy data końcowa <= startowa")
    void reservationBuilderThrowsWhenEndDateNotAfterStart() {
        Client client = new Client(3L, "Piotr", "Wiśniewski",
                "piotr@example.com", "pass", "111222333", "DL003");
        Car car = new Car.Builder().brand("VW").model("Golf")
                .status(VehicleStatus.AVAILABLE).build();
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () ->
                new Reservation.Builder().client(client).vehicle(car)
                        .startDate(now).endDate(now.minusHours(1)).build()
        );
    }
}