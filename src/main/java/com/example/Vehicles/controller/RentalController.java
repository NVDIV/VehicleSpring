package com.example.Vehicles.controller;

import com.example.Vehicles.dto.RentalRequest;
import com.example.Vehicles.model.Rental;
import com.example.Vehicles.model.User;
import com.example.Vehicles.repository.UserRepository;
import com.example.Vehicles.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;
    private final UserRepository userRepository;

    // Wypożyczenie pojazdu
    @PostMapping("/rent")
    public ResponseEntity<Rental> rentVehicle(
            @RequestBody RentalRequest rentalRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Pobieramy użytkownika z bazy na podstawie zalogowanego loginu
        String login = userDetails.getUsername();
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Użytkownik nie znaleziony: " + login));

        // Wypożyczamy pojazd dla zalogowanego użytkownika
        Rental rental = rentalService.rent(rentalRequest.getVehicleId(), user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }

    // Zwrot pojazdu
    @PostMapping("/return")
    public ResponseEntity<String> returnVehicle(
            @RequestBody RentalRequest rentalRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Nie potrzebujemy przesyłać userId w DTO – korzystamy z zalogowanego użytkownika
        String login = userDetails.getUsername();
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Użytkownik nie znaleziony: " + login));

        boolean success = rentalService.returnRental(rentalRequest.getVehicleId(), user.getId());
        if (success) {
            return ResponseEntity.ok("Vehicle returned successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Active rental not found.");
        }
    }
}
