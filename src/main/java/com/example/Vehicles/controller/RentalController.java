package com.example.Vehicles.controller;

import com.example.Vehicles.dto.RentalRequest;
import com.example.Vehicles.model.Rental;
import com.example.Vehicles.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @PostMapping("/rent")
    public ResponseEntity<Rental> rentVehicle(@RequestBody RentalRequest rentalRequest) {
        if (rentalRequest.vehicleId == null || rentalRequest.userId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Rental rental = rentalService.rent(rentalRequest.vehicleId, rentalRequest.userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(rental);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    fetch("http://localhost:8080/rentals/rent", {
//        method: "POST",
//                headers: {
//            "Content-Type": "application/json"
//        },
//        body: JSON.stringify({
//                vehicleId: "3",
//                userId: "f4b5b79b-df2c-4e0a-89fc-464908d2e6dc"
//    })
//    })
//            .then(response => response.json())
//            .then(data => console.log("Rented:", data))
//            .catch(error => console.error(error));


    @PostMapping("/return")
    public ResponseEntity<String> returnVehicle(@RequestBody RentalRequest rentalRequest) {
        boolean success = rentalService.returnRental(rentalRequest.vehicleId, rentalRequest.userId);
        if (success) {
            return ResponseEntity.ok("Vehicle returned successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Active rental not found.");
        }
    }

//    fetch("http://localhost:8080/rentals/return", {
//        method: "POST",
//                headers: {
//            "Content-Type": "application/json"
//        },
//        body: JSON.stringify({
//                vehicleId: "3",
//                userId: "f4b5b79b-df2c-4e0a-89fc-464908d2e6dc"
//    })
//    })
//            .then(response => response.text())
//            .then(data => console.log("Return response:", data))
//            .catch(error => console.error(error));
}
