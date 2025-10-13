package com.example.crimeplatformapi.dto;

import java.time.LocalDate;

public record PersonDTO(
        Integer personId,
        String name,
        String idNumber,
        String gender,
        LocalDate birthDate,
        String address,
        String contactInfo
) {}