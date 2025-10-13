package com.example.crimeplatformapi.dto;

import java.math.BigDecimal;

public record LocationDTO(
        Integer locationId,
        String address,
        String district,
        BigDecimal longitude,
        BigDecimal latitude
) {}