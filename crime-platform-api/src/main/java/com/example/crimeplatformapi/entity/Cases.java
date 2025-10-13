package com.example.crimeplatformapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Cases")
public class Cases {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_id")
    private Integer caseId;

    @Column(name = "case_title", nullable = false, length = 100)
    private String caseTitle;

    @Column(name = "case_type", nullable = false, length = 50)
    private String caseType;

    @Column(name = "status", nullable = false)
    private String status; // ENUM 映射为 String

    @Column(name = "report_time", nullable = false)
    private LocalDateTime reportTime;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // --- 关系映射 ---
    // 多个案件(Many)可以对应一个地点(One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Locations location;

    // 一个案件可以有多个办案人员(Officer)，是多对多关系
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Case_Officers",
            joinColumns = @JoinColumn(name = "case_id"),
            inverseJoinColumns = @JoinColumn(name = "officer_id")
    )
    private Set<Officers> officers = new HashSet<>();
    
    // 一个案件可以涉及多个相关人员(Person)，并且中间表有额外字段`role_in_case`
    // 这种带额外字段的多对多关系，需要将中间表也映射为一个实体
    @OneToMany(mappedBy = "cases", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CasePersons> casePersons = new HashSet<>();
}