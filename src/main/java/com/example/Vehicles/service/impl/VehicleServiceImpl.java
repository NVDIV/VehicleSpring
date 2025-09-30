package com.example.Vehicles.service.impl;

import com.example.Vehicles.model.Vehicle;
import com.example.Vehicles.repository.RentalRepository;
import com.example.Vehicles.repository.VehicleRepository;
import com.example.Vehicles.service.VehicleService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              RentalRepository rentalRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAllActive() {
        return vehicleRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehicle> findById(String id) {
        return vehicleRepository.findByIdAndIsActiveTrue(id);
    }

    @Override
    @Transactional
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(UUID.randomUUID().toString());
            vehicle.setActive(true); // new vehicles are active by default
        }
        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAvailableVehicles() {
        Set<String> rentedVehicleIds = rentalRepository.findRentedVehicleIds();
        if (rentedVehicleIds.isEmpty()) {
            return vehicleRepository.findByIsActiveTrue(); // all active vehicles are available
        }
        return vehicleRepository.findByIsActiveTrueAndIdNotIn(rentedVehicleIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findRentedVehicles() {
        Set<String> rentedVehicleIds = rentalRepository.findRentedVehicleIds();
        return vehicleRepository.findAllById(rentedVehicleIds).stream()
                .filter(Vehicle::isActive)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAvailable(String vehicleId) {
        return findAvailableVehicles().stream()
                .anyMatch(v -> v.getId().equals(vehicleId));
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        vehicleRepository.findById(id).ifPresent(vehicle -> {
            vehicle.setActive(false); // soft delete
            vehicleRepository.save(vehicle);
        });
    }
}
