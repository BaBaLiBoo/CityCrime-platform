package com.example.crimeplatformapi.repository;

import com.example.crimeplatformapi.entity.Locations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationsRepository extends JpaRepository<Locations, Integer> {
    Optional<Locations> findByAddress(String address);
}