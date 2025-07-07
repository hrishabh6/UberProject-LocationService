package com.example.uberprojectlocationservice.service;

import com.example.uberprojectlocationservice.dto.DriverLocationDto;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class RedisLocationServiceImpl implements LocationService {

    private static final String DRIVER_GEO_OPS_KEY = "drivers";
    private static final double DEFAULT_RADIUS = 5.0;

    private StringRedisTemplate redisTemplate;

    public RedisLocationServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Boolean saveDriverLocation(String driverId, double longitude, double latitude) {
        try {
            GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();
            geoOperations.add(
                    DRIVER_GEO_OPS_KEY,
                    new RedisGeoCommands.GeoLocation<>(
                            driverId,
                            new Point(longitude, latitude)
                    )
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<DriverLocationDto> getNearbyDrivers(double latitude, double longitude) {
        try {
            GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();

            Distance radius = new Distance(DEFAULT_RADIUS, Metrics.KILOMETERS);
            Circle within = new Circle(new Point(longitude, latitude), radius);

            GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                    geoOperations.radius(
                            DRIVER_GEO_OPS_KEY,
                            within,
                            RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates()
                    );

            if (results == null) {
                return Collections.emptyList();
            }

            List<DriverLocationDto> drivers = new ArrayList<>();

            for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
                RedisGeoCommands.GeoLocation<String> location = result.getContent();
                Point point = location.getPoint();

                if (point != null) {
                    DriverLocationDto driverLocation = DriverLocationDto.builder()
                            .driverId(location.getName())
                            .longitude(point.getX())
                            .latitude(point.getY())
                            .build();
                    drivers.add(driverLocation);
                }
            }

            return drivers;

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }



}