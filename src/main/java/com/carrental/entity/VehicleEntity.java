package com.carrental.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "vehicles")
public class VehicleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "brand", nullable = false, length = 100)
    private String brand;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "price_per_hour", precision = 10, scale = 2)
    private BigDecimal pricePerHour;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // AVAILABLE, RENTED, IN_SERVICE

    @Column(name = "type", nullable = false, length = 20)
    private String type; // CAR, BUS

    @Column(name = "number_of_seats")
    private Integer numberOfSeats; // tylko dla CAR

    @Column(name = "number_of_doors")
    private Integer numberOfDoors; // tylko dla BUS

    public VehicleEntity() {}

    // Gettery i settery

    public Long getVersion() { return version; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(BigDecimal pricePerHour) { this.pricePerHour = pricePerHour; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(Integer numberOfSeats) { this.numberOfSeats = numberOfSeats; }

    public Integer getNumberOfDoors() { return numberOfDoors; }
    public void setNumberOfDoors(Integer numberOfDoors) { this.numberOfDoors = numberOfDoors; }
}