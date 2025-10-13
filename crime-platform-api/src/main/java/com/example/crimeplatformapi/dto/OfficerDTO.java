package com.example.crimeplatformapi.dto;

// 展示警员信息时，通常也需要其基础人员信息
public record OfficerDTO(
        String officerId,
        String department,
        String position,
        PersonDTO personDetails // 嵌套人员信息的DTO
) {}