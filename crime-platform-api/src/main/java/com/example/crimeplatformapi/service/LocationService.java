package com.example.crimeplatformapi.service;

import com.example.crimeplatformapi.entity.Locations;
import com.example.crimeplatformapi.exception.ResourceNotFoundException;
import com.example.crimeplatformapi.repository.LocationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    @Autowired
    private LocationsRepository locationsRepository;

    public List<Locations> findAll() {
        return locationsRepository.findAll();
    }

    public Locations findById(Integer id) {
        return locationsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到ID为 " + id + " 的地点"));
    }

    public Locations save(Locations location) {
        return locationsRepository.save(location);
    }

    public void delete(Integer id) {
        if (!locationsRepository.existsById(id)) {
            throw new ResourceNotFoundException("删除地点失败：未找到ID为 " + id + " 的地点");
        }
        locationsRepository.deleteById(id);
    }
}