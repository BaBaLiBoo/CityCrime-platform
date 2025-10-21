package com.example.crimeplatformapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
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
    // 用户与 Officer 关系可选（平台用户不一定是警员）
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id", referencedColumnName = "officer_id", nullable = true, unique = true)
    private Officers officer;

    // 角色功能已移除；如需恢复，请在此处恢复多对多映射
}