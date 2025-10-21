package com.example.crimeplatformapi.dto;

import java.time.LocalDate;

public record PersonDTO(
        String idNumber,
        String name,
        String gender,
        LocalDate birthDate,
        String address,
        String contactInfo
) {}