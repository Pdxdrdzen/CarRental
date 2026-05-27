package com.carrental.service;

import com.carrental.entity.VehicleEntity;
import com.carrental.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<VehicleEntity> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<VehicleEntity> getAvailableVehicles() {
        return vehicleRepository.findByStatus("AVAILABLE");
    }

    public List<VehicleEntity> getVehiclesByType(String type) {
        return vehicleRepository.findByType(type);
    }

    public Optional<VehicleEntity> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public VehicleEntity createVehicle(VehicleEntity vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public VehicleEntity updateVehicle(Long id, VehicleEntity updated) {
        VehicleEntity existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono pojazdu o ID: " + id));

        existing.setBrand(updated.getBrand());
        existing.setModel(updated.getModel());
        existing.setDescription(updated.getDescription());
        existing.setPricePerHour(updated.getPricePerHour());
        existing.setStatus(updated.getStatus());
        existing.setType(updated.getType());
        existing.setNumberOfSeats(updated.getNumberOfSeats());
        existing.setNumberOfDoors(updated.getNumberOfDoors());

        return vehicleRepository.save(existing);
    }

    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new RuntimeException("Nie znaleziono pojazdu o ID: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    public VehicleEntity changeStatus(Long id, String newStatus) {
        VehicleEntity vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono pojazdu o ID: " + id));
        vehicle.setStatus(newStatus);
        return vehicleRepository.save(vehicle);
    }
}