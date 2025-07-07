package com.example.uberprojectlocationservice.service;

import com.example.uberprojectlocationservice.dto.DriverLocationDto;

import java.util.List;

public interface LocationService {

    Boolean saveDriverLocation(String driverId, double longitude, double latitude);

    List<DriverLocationDto> getNearbyDrivers(double latitude, double longitude);

}
