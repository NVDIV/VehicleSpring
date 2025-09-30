package com.example.Vehicles.service.impl;

import com.example.Vehicles.model.Rental;
import com.example.Vehicles.model.User;
import com.example.Vehicles.model.Vehicle;
import com.example.Vehicles.repository.RentalRepository;
import com.example.Vehicles.repository.UserRepository;
import com.example.Vehicles.repository.VehicleRepository;
import com.example.Vehicles.service.RentalService;
import com.example.Vehicles.service.VehicleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final VehicleService vehicleService;

    @Override
    public boolean isVehicleRented(String vehicleId) {
        return rentalRepository.existsByVehicleIdAndReturnDateIsNull(vehicleId);
    }

    @Override
    public Optional<Rental> findActiveRentalByVehicleId(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId);
    }

    @Override
    @Transactional
    public Rental rent(String vehicleId, String userId) {
        if (!vehicleService.isAvailable(vehicleId)) {
            throw new IllegalStateException("Vehicle " + vehicleId + " is not available for rent.");
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found: " + vehicleId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        Rental newRental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .vehicle(vehicle)
                .user(user)
                .rentDate(LocalDateTime.now().toString())
                .returnDate(null)
                .build();

        return rentalRepository.save(newRental);
    }

    @Override
    @Transactional
    public boolean returnRental(String vehicleId, String userId) {
        Optional<Rental> rentalOpt = rentalRepository.findByVehicleIdAndUserIdAndReturnDateIsNull(vehicleId, userId);
        if (rentalOpt.isEmpty()) {
            return false;
        }
        Rental rental = rentalOpt.get();
        rental.setReturnDate(LocalDateTime.now().toString());
        rentalRepository.save(rental);
        return true;
    }

    @Override
    public List<Rental> findAll() {
        return rentalRepository.findAll();
    }
}
