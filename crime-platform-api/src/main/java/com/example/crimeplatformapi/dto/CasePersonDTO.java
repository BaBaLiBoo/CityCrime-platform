package com.example.crimeplatformapi.dto;

// 用于在案件详情中，展示涉案人员及其在案件中的角色
public record CasePersonDTO(
        String idNumber,
        String name,
        String roleInCase // 关键信息：在案件中的角色
) {}