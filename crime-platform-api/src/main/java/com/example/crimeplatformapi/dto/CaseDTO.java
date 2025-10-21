package com.example.crimeplatformapi.dto;

import java.time.LocalDateTime;

// 用于在列表中简洁地展示案件信息
public record CaseDTO(
        Integer caseId,
        String caseTitle,
        String caseType,
        String status,
        LocalDateTime reportTime,
        LocalDateTime filingTime,
        LocalDateTime solveTime,
        LocalDateTime archiveTime,
        String locationAddress // 关联地点的地址
) {}