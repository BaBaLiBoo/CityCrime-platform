package com.example.crimeplatformapi.dto;

import java.math.BigDecimal;

/**
 * 用于普通用户修改自己举报案件的请求体
 */
public record ReportUpdateRequest(
        String caseType,
        String description,
        String locationAddress,
        String district,
        BigDecimal longitude,
        BigDecimal latitude
) {}

