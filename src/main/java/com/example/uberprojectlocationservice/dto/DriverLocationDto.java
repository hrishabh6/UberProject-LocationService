package com.example.uberprojectlocationservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverLocationDto {
    String driverId;
    double latitude;
    double longitude;
}
