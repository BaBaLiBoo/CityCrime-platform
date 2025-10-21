package com.example.crimeplatformapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Case_Persons")
public class CasePersons {

    @EmbeddedId
    private CasePersonsId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("caseId") // 将复合主键的 caseId 字段映射到这个关系
    @JoinColumn(name = "case_id")
    private Cases cases;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idNumber") // 将复合主键的 idNumber 字段映射到这个关系
    @JoinColumn(name = "id_number")
    private Persons person;

    // roleInCase 字段已经在 CasePersonsId 中定义，这里不需要重复定义
}