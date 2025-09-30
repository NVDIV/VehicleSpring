package com.example.Vehicles.controller;

import com.example.Vehicles.model.Vehicle;
import com.example.Vehicles.service.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // --- CREATE --- (only admin)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Vehicle> addVehicle(@RequestBody Vehicle vehicle) {
        try {
            Vehicle savedVehicle = vehicleService.save(vehicle);
            log.info("Vehicle created with ID: {}", savedVehicle.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicle); // 201
        } catch (Exception e) {
            log.error("Error while saving vehicle", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500
        }
    }

    // --- SOFT DELETE --- (only admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVehicle(@PathVariable String id) {
        log.info("Soft deleting vehicle with ID: {}", id);
        vehicleService.deleteById(id);
        return ResponseEntity.noContent().build(); // 204
    }

    // --- READ ALL ---
    @GetMapping
    public List<Vehicle> getAllVehicles() {
        log.info("Fetching all vehicles");
        return vehicleService.findAll();
    }

    // --- READ ACTIVE ONLY ---
    @GetMapping("/active")
    public List<Vehicle> getActiveVehicles() {
        log.info("Fetching all active vehicles");
        return vehicleService.findAllActive();
    }

    // --- READ BY ID ---
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable String id) {
        log.info("Request received for vehicle with ID: {}", id);
        return vehicleService.findById(id)
                .map(ResponseEntity::ok) // 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build()); // 404
    }

    // --- AVAILABLE VEHICLES ---
    @GetMapping("/available")
    public List<Vehicle> getAvailableVehicles() {
        log.info("Fetching all available vehicles");
        return vehicleService.findAvailableVehicles();
    }

    // --- RENTED VEHICLES ---
    @GetMapping("/rented")
    public List<Vehicle> getRentedVehicles() {
        log.info("Fetching all rented vehicles");
        return vehicleService.findRentedVehicles();
    }
}
