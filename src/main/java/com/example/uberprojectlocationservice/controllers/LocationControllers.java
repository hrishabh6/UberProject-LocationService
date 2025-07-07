package com.example.uberprojectlocationservice.controllers;

import com.example.uberprojectlocationservice.dto.NearbyDriversReqestDto;
import com.example.uberprojectlocationservice.dto.SaveDriverLocationRequestDto;
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

    private StringRedisTemplate redisTemplate;

    private static final String DRIVER_GEO_OPS_KEY = "drivers";
    private static final double DEFAULT_RADIUS = 5.0;

    public LocationControllers(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/drivers")
    public ResponseEntity<Boolean> saveDriverLocation(@RequestBody SaveDriverLocationRequestDto saveDriverLocationRequestDto){

        try{
            GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();
            geoOperations.add(
                    DRIVER_GEO_OPS_KEY,
                    new RedisGeoCommands.GeoLocation<>(
                            saveDriverLocationRequestDto.getDriverId(),
                            new Point(
                                    saveDriverLocationRequestDto.getLongitude(),
                                    saveDriverLocationRequestDto.getLatitude()
                            )
                    )
            );
            return new ResponseEntity<>(true, HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/drivers/bulk")
    public ResponseEntity<Boolean> saveBulkDriverLocations(@RequestBody List<SaveDriverLocationRequestDto> driverList) {
        try {
            GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();

            for (SaveDriverLocationRequestDto dto : driverList) {
                geoOperations.add(
                        DRIVER_GEO_OPS_KEY,
                        new RedisGeoCommands.GeoLocation<>(
                                dto.getDriverId(),
                                new Point(dto.getLongitude(), dto.getLatitude()) // ✅ Note: longitude first!
                        )
                );
            }

            System.out.println("✅ Successfully saved " + driverList.size() + " driver locations.");
            return ResponseEntity.status(HttpStatus.CREATED).body(true);

        } catch (Exception e) {
            System.err.println("❌ Error while saving bulk driver locations: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }



    @GetMapping("/nearby/drivers")
    public ResponseEntity<List<String>> getNearbyDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude) {

        System.out.println("🔍 Received request for nearby drivers");
        System.out.println("📍 Query Coordinates -> Latitude: " + latitude + ", Longitude: " + longitude);

        try {
            GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();

            System.out.println("🔧 Preparing search radius: " + DEFAULT_RADIUS + " km");

            Distance radius = new Distance(DEFAULT_RADIUS, Metrics.KILOMETERS);
            Circle within = new Circle(new Point(longitude , latitude), radius);

            GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                    geoOperations.radius(DRIVER_GEO_OPS_KEY, within);

            if (results == null) {
                System.out.println("⚠️ Redis returned null results.");
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<String> drivers = new ArrayList<>();

            for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
                String driverId = result.getContent().getName();
                System.out.println("✅ Found driver within radius: " + driverId);
                drivers.add(driverId);
            }

            System.out.println("🚗 Total drivers found: " + drivers.size());
            return ResponseEntity.ok(drivers);

        } catch (Exception e) {
            System.err.println("❌ Error while fetching nearby drivers: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
