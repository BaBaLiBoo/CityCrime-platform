package com.example.crimeplatformapi.dto;

import java.time.LocalDateTime;
import java.util.Set;

// 用于展示单个案件的全部详细信息，包括关联的人员
public record CaseDetailDTO(
        Integer caseId,
        String caseTitle,
        String caseType,
        String status,
        LocalDateTime reportTime,
        LocalDateTime filingTime,
        LocalDateTime solveTime,
        LocalDateTime archiveTime,
        String description,
        LocationDTO location, // 嵌套地点的DTO
        Set<OfficerDTO> officers, // 办案警员列表
        Set<CasePersonDTO> involvedPersons // 涉案人员列表
) {}