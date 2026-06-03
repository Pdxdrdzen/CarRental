package com.carrental.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_requests")
public class ServiceRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;


    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private VehicleEntity vehicle;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // OPEN, IN_PROGRESS, RESOLVED

    @Column(name = "reported_at")
    private LocalDateTime reportedAt;

    public ServiceRequestEntity() {}

    // Gettery i settery

    public Long getVersion() { return version; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public VehicleEntity getVehicle() { return vehicle; }
    public void setVehicle(VehicleEntity vehicle) { this.vehicle = vehicle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }
}