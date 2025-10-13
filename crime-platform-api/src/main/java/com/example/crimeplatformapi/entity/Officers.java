package com.example.crimeplatformapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Officers")
public class Officers {

    @Id
    @Column(name = "officer_id", length = 20)
    private String officerId; // 主键是 VARCHAR，不需要 @GeneratedValue

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "position", length = 50)
    private String position;

    // --- 关系映射 ---
    // 一个 Officer 对应一个 Person，是一对一关系
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", referencedColumnName = "person_id", nullable = false, unique = true)
    private Persons person;
}