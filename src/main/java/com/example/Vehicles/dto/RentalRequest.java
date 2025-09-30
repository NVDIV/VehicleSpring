package com.example.Vehicles.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class RentalRequest {
    public String vehicleId;
    public String userId;
}

