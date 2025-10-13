package com.example.crimeplatformapi.dto;

import java.time.LocalDateTime;

public record CreateCaseRequest(
        String caseTitle,
        String caseType,
        String status,
        LocalDateTime reportTime,
        String description,
        String locationAddress
) {}