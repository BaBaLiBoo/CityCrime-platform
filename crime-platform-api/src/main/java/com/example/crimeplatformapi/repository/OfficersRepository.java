package com.example.crimeplatformapi.repository;

import com.example.crimeplatformapi.entity.Officers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficersRepository extends JpaRepository<Officers, String> {
}