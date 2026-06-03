package com.carrental.pattern;

import com.carrental.model.vehicle.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Wzorzec Strategy (Rentable / Serviceable)")
class StrategyPatternTest {

    @Test
    @DisplayName("Car i Bus są traktowane jako Rentable — polimorfizm")
    void vehiclesArePolymorphicThroughRentable() {
        Rentable car = new Car.Builder().brand("Ford").model("Focus")
                .pricePerHour(30.0).status(VehicleStatus.AVAILABLE).build();
        Rentable bus = new Bus.Builder().brand("MAN").model("Lion's City")
                .pricePerHour(90.0).status(VehicleStatus.AVAILABLE).build();

        LocalDateTime start = LocalDateTime.of(2024, 8, 1, 8, 0);
        LocalDateTime end   = LocalDateTime.of(2024, 8, 1, 10, 0); // 2h

        assertEquals(60.0,  car.calculateCost(start, end), 0.001);
        assertEquals(180.0, bus.calculateCost(start, end), 0.001);
    }

    @Test
    @DisplayName("Car i Bus są traktowane jako Serviceable — polimorfizm")
    void vehiclesArePolymorphicThroughServiceable() {
        Serviceable car = new Car.Builder().brand("Kia").model("Ceed")
                .status(VehicleStatus.AVAILABLE).build();
        Serviceable bus = new Bus.Builder().brand("Neoplan").model("Cityliner")
                .status(VehicleStatus.AVAILABLE).build();

        car.sendToService();
        bus.sendToService();

        assertEquals(VehicleStatus.IN_SERVICE, ((Vehicle) car).getStatus());
        assertEquals(VehicleStatus.IN_SERVICE, ((Vehicle) bus).getStatus());
    }

    @Test
    @DisplayName("isAvailable zwraca true tylko dla AVAILABLE — spójność interfejsu")
    void isAvailableConsistentAcrossImplementations() {
        Rentable availableCar = new Car.Builder().brand("Skoda").model("Octavia")
                .status(VehicleStatus.AVAILABLE).build();
        Rentable rentedBus = new Bus.Builder().brand("Setra").model("S 515")
                .status(VehicleStatus.RENTED).build();

        assertTrue(availableCar.isAvailable());
        assertFalse(rentedBus.isAvailable());
    }

    @Test
    @DisplayName("Koszt rośnie liniowo proporcjonalnie do czasu")
    void costScalesLinearlyWithTime() {
        Car car = new Car.Builder().brand("Renault").model("Clio")
                .pricePerHour(40.0).status(VehicleStatus.AVAILABLE).build();
        LocalDateTime base = LocalDateTime.of(2024, 9, 1, 8, 0);

        double cost2h = car.calculateCost(base, base.plusHours(2));
        double cost4h = car.calculateCost(base, base.plusHours(4));

        assertEquals(cost2h * 2, cost4h, 0.001);
    }

    @Test
    @DisplayName("sendToService i returnFromService działają poprawnie na Serviceable")
    void serviceableCycleWorks() {
        Vehicle car = new Car.Builder().brand("Opel").model("Astra")
                .status(VehicleStatus.AVAILABLE).build();

        assertTrue(car.isAvailable());
        car.sendToService();
        assertFalse(car.isAvailable());
        car.returnFromService();
        assertTrue(car.isAvailable());
    }
}