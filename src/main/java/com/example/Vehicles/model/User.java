package com.example.Vehicles.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users") // avoid conflict with SQL keyword "user"
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    private String login;
    private String password;
    private String role;
}
