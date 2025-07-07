package com.example.uberprojectlocationservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveDriverLocationRequestDto {
    String driverId;
    double latitude;
    double longitude;
}
