package com.example.crimeplatformapi.controller;

import com.example.crimeplatformapi.dto.LocationDTO;
import com.example.crimeplatformapi.entity.Locations;
import com.example.crimeplatformapi.service.LocationService; // 您需要创建这个Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService; // 假设您已创建 LocationService

    // GET /api/locations - 获取所有地点
    @GetMapping
    public List<LocationDTO> getAllLocations() {
        return locationService.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // POST /api/locations - 添加新地点
    @PostMapping
    public LocationDTO createLocation(@RequestBody Locations location) {
        return convertToDto(locationService.save(location));
    }

    private LocationDTO convertToDto(Locations entity) {
        return new LocationDTO(
            entity.getLocationId(),
            entity.getAddress(),
            entity.getDistrict(),
            entity.getLongitude(),
            entity.getLatitude()
        );
    }
}