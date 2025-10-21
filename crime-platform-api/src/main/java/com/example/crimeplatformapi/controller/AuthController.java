package com.example.crimeplatformapi.controller;

import com.example.crimeplatformapi.entity.*;
import com.example.crimeplatformapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    public record LoginRequest(String username, String password) {}
    public record RegisterRequest(String username, String password) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Optional<UserAccounts> userOpt = userAccountsRepository.findByUsername(req.username());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "用户名或密码错误"));
        }
        UserAccounts ua = userOpt.get();
        // 简化：直接对比明文（生产中请使用 BCrypt 等哈希）
        if (!ua.getPasswordHash().equals(req.password())) {
            return ResponseEntity.status(401).body(Map.of("message", "用户名或密码错误"));
        }
        // 返回一个简易token（生产中应签发JWT）
        String token = "dummy-token-" + ua.getUserId();
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String auth) {
        if (auth == null || !auth.startsWith("Bearer dummy-token-")) {
            return ResponseEntity.status(401).body(Map.of("message", "未认证"));
        }
        String idStr = auth.substring("Bearer dummy-token-".length());
        Integer userId;
        try { userId = Integer.parseInt(idStr); } catch (Exception e) { return ResponseEntity.status(401).body(Map.of("message", "无效凭证")); }
        Optional<UserAccounts> user = userAccountsRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "用户不存在"));
        }
        UserAccounts ua = user.get();
        String officerId = (ua.getOfficer() != null) ? ua.getOfficer().getOfficerId() : null;
        return ResponseEntity.ok(Map.of(
                "userId", ua.getUserId(),
                "username", ua.getUsername(),
                "officerId", officerId
        ));
    }

    @PostMapping("/register")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        // 基础校验
        if (req.username() == null || req.username().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "用户名不能为空"));
        }
        if (req.password() == null || req.password().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "密码不能为空"));
        }
        if (userAccountsRepository.findByUsername(req.username()).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("message", "用户名已存在"));
        }
        try {
            // 创建 UserAccount（无需角色/警员信息）
            UserAccounts account = new UserAccounts();
            account.setUsername(req.username());
            account.setPasswordHash(req.password());
            account.setCreatedAt(LocalDateTime.now());

            UserAccounts saved = userAccountsRepository.save(account);

            return ResponseEntity.ok(Map.of(
                    "userId", saved.getUserId(),
                    "username", saved.getUsername()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "创建用户失败：" + e.getMessage()));
        }
    }
}


