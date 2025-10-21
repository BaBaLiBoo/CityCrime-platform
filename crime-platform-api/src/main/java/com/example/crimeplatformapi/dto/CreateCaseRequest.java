package com.example.crimeplatformapi.dto;

import java.time.LocalDateTime;

public record CreateCaseRequest(
        String caseTitle,
        String caseType,
        String status,
        LocalDateTime reportTime,
        LocalDateTime filingTime,
        LocalDateTime solveTime,
        LocalDateTime archiveTime,
        String description,
        String locationAddress
) {}