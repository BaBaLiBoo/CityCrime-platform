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
    @MapsId("personId") // 将复合主键的 personId 字段映射到这个关系
    @JoinColumn(name = "person_id")
    private Persons person;

    // --- 核心改动在这里 ---
    // 将这个属性标记为不可插入和不可更新
    // 它现在仅仅是复合主键中同名字段的一个“只读视图”
    @Column(name = "role_in_case", nullable = false, insertable = false, updatable = false)
    private String roleInCase;
}