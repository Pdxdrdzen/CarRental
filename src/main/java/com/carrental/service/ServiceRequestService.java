package com.carrental.service;

import com.carrental.entity.ServiceRequestEntity;
import com.carrental.entity.VehicleEntity;
import com.carrental.repository.ServiceRequestRepository;
import com.carrental.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final VehicleRepository vehicleRepository;

    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository,
                                 VehicleRepository vehicleRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<ServiceRequestEntity> getAllRequests() {
        return serviceRequestRepository.findAll();
    }

    public List<ServiceRequestEntity> getRequestsByStatus(String status) {
        return serviceRequestRepository.findByStatus(status);
    }

    public Optional<ServiceRequestEntity> getRequestById(Long id) {
        return serviceRequestRepository.findById(id);
    }

    public ServiceRequestEntity createRequest(Long vehicleId, String description) {
        VehicleEntity vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono pojazdu o ID: " + vehicleId));

        // Zmień status pojazdu na IN_SERVICE
        vehicle.setStatus("IN_SERVICE");
        vehicleRepository.save(vehicle);

        ServiceRequestEntity request = new ServiceRequestEntity();
        request.setVehicle(vehicle);
        request.setDescription(description);
        request.setStatus("OPEN");
        request.setReportedAt(LocalDateTime.now());

        return serviceRequestRepository.save(request);
    }

    public ServiceRequestEntity resolveRequest(Long id) {
        ServiceRequestEntity request = serviceRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono zgłoszenia o ID: " + id));

        request.setStatus("RESOLVED");

        // Zwróć pojazd do puli
        VehicleEntity vehicle = request.getVehicle();
        vehicle.setStatus("AVAILABLE");
        vehicleRepository.save(vehicle);

        return serviceRequestRepository.save(request);
    }
}