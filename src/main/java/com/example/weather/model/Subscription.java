package com.example.weather.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscription",
       uniqueConstraints = @UniqueConstraint(columnNames = {"email", "city"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String city;
}
