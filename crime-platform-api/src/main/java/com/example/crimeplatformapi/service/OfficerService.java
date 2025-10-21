package com.example.crimeplatformapi.service;

import com.example.crimeplatformapi.entity.Officers;
import com.example.crimeplatformapi.entity.UserAccounts;
import com.example.crimeplatformapi.repository.OfficersRepository;
import com.example.crimeplatformapi.repository.UserAccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OfficerService {

    @Autowired
    private OfficersRepository officersRepository;

    @Autowired
    private UserAccountsRepository userAccountsRepository;
    
    // 这是一个复杂的业务：注册一个新警员，并为他创建一个系统账户
    // 它需要同时操作两张表，因此必须使用 @Transactional 注解
    @Transactional
    public Officers registerNewOfficer(Officers officerData, UserAccounts accountData) {
        // 步骤 1: 保存警员信息（现在包含所有基本信息）
        Officers savedOfficer = officersRepository.save(officerData);

        // 步骤 2: 关联并保存系统账户信息
        // TODO: 在实际项目中，密码需要在这里进行加密 (e.g., using Spring Security's PasswordEncoder)
        // accountData.setPasswordHash(passwordEncoder.encode(accountData.getPasswordHash()));
        accountData.setOfficer(savedOfficer);
        userAccountsRepository.save(accountData);

        return savedOfficer;
    }
}