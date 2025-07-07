package com.example.uberprojectlocationservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NearbyDriversReqestDto {
    double latitude;
    double longitude;
}
