package com.example.crimeplatformapi.dto;

// 用于“添加涉案人员到案件”接口的请求体
public record AddPersonRequest(
        Integer personId,
        String roleInCase // 例如："嫌疑人", "受害人", "证人"
) {}