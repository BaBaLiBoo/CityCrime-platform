package com.example.crimeplatformapi.controller;

import com.example.crimeplatformapi.entity.UserAccounts;
import com.example.crimeplatformapi.repository.UserAccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    /**
     * 测试接口：查看testuser123的用户类型
     * GET /api/test/user-type?username=testuser123
     */
    @GetMapping("/user-type")
    public ResponseEntity<?> getUserType(@RequestParam String username) {
        Optional<UserAccounts> userOpt = userAccountsRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "用户不存在");
            return ResponseEntity.status(404).body(response);
        }
        
        UserAccounts user = userOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUserId());
        response.put("username", user.getUsername());
        response.put("userType", user.getUserType());
        response.put("userTypeLength", user.getUserType() != null ? user.getUserType().length() : 0);
        response.put("userTypeBytes", user.getUserType() != null ? user.getUserType().getBytes().length : 0);
        response.put("isAdmin", "管理员".equals(user.getUserType()));
        response.put("equalsCheck", user.getUserType() != null && user.getUserType().equals("管理员"));
        
        return ResponseEntity.ok(response);
    }
}

