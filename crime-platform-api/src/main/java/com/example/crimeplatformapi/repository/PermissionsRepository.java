package com.example.crimeplatformapi.repository;

import com.example.crimeplatformapi.entity.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionsRepository extends JpaRepository<Permissions, Integer> {
}
