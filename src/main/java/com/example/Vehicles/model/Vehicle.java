package com.example.Vehicles.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "vehicle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    private String id;

    private String category;
    private String brand;
    private String model;
    private int year;
    private String plate;
    private BigDecimal price;

    @Column(name = "active", nullable = false)
    private boolean isActive = true;  // for soft delete
}
