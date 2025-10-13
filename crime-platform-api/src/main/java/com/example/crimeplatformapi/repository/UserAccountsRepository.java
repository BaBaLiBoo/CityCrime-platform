package com.example.crimeplatformapi.repository;

import com.example.crimeplatformapi.entity.UserAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountsRepository extends JpaRepository<UserAccounts, Integer> {
    Optional<UserAccounts> findByUsername(String username);
}