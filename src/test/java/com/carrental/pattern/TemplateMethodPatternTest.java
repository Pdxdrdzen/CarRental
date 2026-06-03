package com.carrental.pattern;

import com.carrental.model.vehicle.Car;
import com.carrental.model.vehicle.Bus;
import com.carrental.model.vehicle.VehicleStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Wzorzec Template Method (Vehicle)")
class TemplateMethodPatternTest {

    private Car car;
    private Bus bus;

    @BeforeEach
    void setUp() {
        car = new Car.Builder().brand("Toyota").model("Yaris")
                .pricePerHour(20.0).status(VehicleStatus.AVAILABLE).build();
        bus = new Bus.Builder().brand("Volvo").model("B9")
                .pricePerHour(100.0).status(VehicleStatus.AVAILABLE).build();
    }

    @Test
    @DisplayName("calculateCost poprawnie oblicza koszt dla Car (4h × 20 zł = 80 zł)")
    void calculateCostForCar() {
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 8, 0);
        LocalDateTime end   = LocalDateTime.of(2024, 7, 1, 12, 0);
        assertEquals(80.0, car.calculateCost(start, end), 0.001);
    }

    @Test
    @DisplayName("calculateCost poprawnie oblicza koszt dla Bus (3h × 100 zł = 300 zł)")
    void calculateCostForBus() {
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 9, 0);
        LocalDateTime end   = LocalDateTime.of(2024, 7, 1, 12, 0);
        assertEquals(300.0, bus.calculateCost(start, end), 0.001);
    }

    @Test
    @DisplayName("calculateCost liczy minimum 1 godzinę dla ułamkowych czasów")
    void calculateCostMinimumOneHour() {
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 10, 0);
        LocalDateTime end   = LocalDateTime.of(2024, 7, 1, 10, 30); // 30 min → 1h min
        assertEquals(20.0, car.calculateCost(start, end), 0.001);
    }

    @Test
    @DisplayName("calculateCost rzuca wyjątek gdy data końcowa <= startowa")
    void calculateCostThrowsForInvalidDates() {
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 12, 0);
        LocalDateTime end   = LocalDateTime.of(2024, 7, 1, 10, 0);
        assertThrows(IllegalArgumentException.class,
                () -> car.calculateCost(start, end));
    }

    @Test
    @DisplayName("calculateCost rzuca wyjątek gdy daty null")
    void calculateCostThrowsForNullDates() {
        assertThrows(IllegalArgumentException.class,
                () -> car.calculateCost(null, null));
    }

    @Test
    @DisplayName("isAvailable zwraca true dla pojazdu AVAILABLE")
    void isAvailableReturnsTrueWhenAvailable() {
        assertTrue(car.isAvailable());
    }

    @Test
    @DisplayName("isAvailable zwraca false dla pojazdu RENTED")
    void isAvailableReturnsFalseWhenRented() {
        Car rented = new Car.Builder().brand("BMW").model("3")
                .status(VehicleStatus.RENTED).build();
        assertFalse(rented.isAvailable());
    }

    @Test
    @DisplayName("isAvailable zwraca false dla pojazdu IN_SERVICE")
    void isAvailableReturnsFalseWhenInService() {
        Car inService = new Car.Builder().brand("Audi").model("A3")
                .status(VehicleStatus.IN_SERVICE).build();
        assertFalse(inService.isAvailable());
    }

    @Test
    @DisplayName("sendToService zmienia status na IN_SERVICE")
    void sendToServiceChangesStatus() {
        car.sendToService();
        assertEquals(VehicleStatus.IN_SERVICE, car.getStatus());
    }

    @Test
    @DisplayName("returnFromService zmienia status na AVAILABLE")
    void returnFromServiceChangesStatus() {
        car.sendToService();
        car.returnFromService();
        assertEquals(VehicleStatus.AVAILABLE, car.getStatus());
    }

    @Test
    @DisplayName("setPricePerHour aktualizuje cenę")
    void setPricePerHourUpdatesValue() {
        car.setPricePerHour(35.0);
        assertEquals(35.0, car.getPricePerHour(), 0.001);
    }

    @Test
    @DisplayName("setPricePerHour rzuca wyjątek dla ujemnej ceny")
    void setPricePerHourThrowsForNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> car.setPricePerHour(-10.0));
    }
}