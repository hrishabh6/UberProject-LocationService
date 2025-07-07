package com.example.uberprojectlocationservice.controllers;

import com.example.uberprojectlocationservice.dto.DriverLocationDto;
import com.example.uberprojectlocationservice.dto.NearbyDriversReqestDto;
import com.example.uberprojectlocationservice.dto.SaveDriverLocationRequestDto;
import com.example.uberprojectlocationservice.service.LocationService;
import org.hibernate.loader.ast.spi.Loadable;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/location")
public class LocationControllers {

    private LocationService locationService;



    public LocationControllers(LocationService locationService){
        this.locationService = locationService;
    }

    @PostMapping("/drivers")
    public ResponseEntity<Boolean> saveDriverLocation(@RequestBody SaveDriverLocationRequestDto saveDriverLocationRequestDto){

        try{
            Boolean result = locationService.saveDriverLocation(saveDriverLocationRequestDto.getDriverId(), saveDriverLocationRequestDto.getLongitude(), saveDriverLocationRequestDto.getLatitude());
            return new ResponseEntity<>(true, HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PostMapping("/drivers/bulk")
//    public ResponseEntity<Boolean> saveBulkDriverLocations(@RequestBody List<SaveDriverLocationRequestDto> driverList) {
//        try {
//            GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();
//
//            for (SaveDriverLocationRequestDto dto : driverList) {
//                geoOperations.add(
//                        DRIVER_GEO_OPS_KEY,
//                        new RedisGeoCommands.GeoLocation<>(
//                                dto.getDriverId(),
//                                new Point(dto.getLongitude(), dto.getLatitude()) // ✅ Note: longitude first!
//                        )
//                );
//            }
//
//            System.out.println("✅ Successfully saved " + driverList.size() + " driver locations.");
//            return ResponseEntity.status(HttpStatus.CREATED).body(true);
//
//        } catch (Exception e) {
//            System.err.println("❌ Error while saving bulk driver locations: " + e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
//        }
//    }



    @GetMapping("/nearby/drivers")
    public ResponseEntity<List<DriverLocationDto>> getNearbyDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude) {


        try {
            List<DriverLocationDto> drivers = locationService.getNearbyDrivers(latitude, longitude);
            return new ResponseEntity<>(drivers, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("❌ Error while fetching nearby drivers: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
