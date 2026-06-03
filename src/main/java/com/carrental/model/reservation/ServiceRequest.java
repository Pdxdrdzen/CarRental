package com.carrental.model.reservation;
import com.carrental.model.vehicle.Vehicle;

public class ServiceRequest {
    private Long id;
    private Vehicle vehicle;
    private String description;
    private boolean resolved;

    public ServiceRequest(Long id, Vehicle vehicle, String description) {
        this.id = id; this.vehicle = vehicle;
        this.description = description;
        vehicle.sendToService();
    }

    public void resolve() { resolved = true; vehicle.returnFromService(); }

    public Long getId() { return id; }
    public Vehicle getVehicle() { return vehicle; }
    public String getDescription() { return description; }
    public boolean isResolved() { return resolved; }
}