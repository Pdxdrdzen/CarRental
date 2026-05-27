package com.carrental.controller;

import com.carrental.service.ServiceRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/service-requests")
@CrossOrigin(origins = "*")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    public ServiceRequestController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    // GET /api/service-requests
    @GetMapping
    public ResponseEntity<?> getAllRequests() {
        return ResponseEntity.ok(serviceRequestService.getAllRequests());
    }

    // GET /api/service-requests/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return serviceRequestService.getRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/service-requests/status/{status}
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(serviceRequestService.getRequestsByStatus(status));
    }

    // POST /api/service-requests?vehicleId=1&description=Usterka
    @PostMapping
    public ResponseEntity<?> createRequest(@RequestParam Long vehicleId,
                                           @RequestParam String description) {
        try {
            return ResponseEntity.ok(serviceRequestService.createRequest(vehicleId, description));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PATCH /api/service-requests/{id}/resolve
    @PatchMapping("/{id}/resolve")
    public ResponseEntity<?> resolve(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(serviceRequestService.resolveRequest(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}