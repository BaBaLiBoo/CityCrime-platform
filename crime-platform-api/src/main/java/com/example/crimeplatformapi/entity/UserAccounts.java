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
@Table(name = "UserAccounts")
public class UserAccounts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // --- 关系映射 ---
    // 一个 UserAccount 对应一个 Officer，是一对一关系
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id", referencedColumnName = "officer_id", nullable = false, unique = true)
    private Officers officer;

    // 一个 UserAccount 可以有多个 Role，是多对多关系
    @ManyToMany(fetch = FetchType.EAGER) // EAGER 表示加载用户时，立刻加载其角色
    @JoinTable(
            name = "User_Roles", // 指定中间表
            joinColumns = @JoinColumn(name = "user_id"), // 指定本实体在中间表的外键
            inverseJoinColumns = @JoinColumn(name = "role_id") // 指定对方实体在中间表的外键
    )
    private Set<Roles> roles = new HashSet<>();
}