package com.example.crimeplatformapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class CasePersonsId implements Serializable {

    @Column(name = "case_id")
    private Integer caseId;

    @Column(name = "person_id")
    private Integer personId;

    // --- 新增的字段 ---
    // 将 role_in_case 添加为复合主键的一部分，与数据库表结构保持一致
    @Column(name = "role_in_case")
    private String roleInCase;
}