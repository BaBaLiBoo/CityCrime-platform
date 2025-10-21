package com.example.crimeplatformapi.controller;

import com.example.crimeplatformapi.dto.OfficerDTO;
import com.example.crimeplatformapi.entity.Officers;
import com.example.crimeplatformapi.repository.OfficersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

@RestController
@RequestMapping("/api/officers")
public class OfficerController {

    @Autowired
    private OfficersRepository officersRepository;

    // GET /api/officers - 获取所有警员列表
    @GetMapping
    public List<OfficerDTO> getAllOfficers() {
        return officersRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // POST /api/officers - 创建一个新的警员记录
    @PostMapping
    public ResponseEntity<?> createOfficer(@RequestBody CreateOfficerRequest request) {
        try {
            // 检查警员编号是否已存在
            if (officersRepository.findById(request.officerId()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "警员编号已存在"));
            }
            
            // 创建警员记录（完全独立，不关联 Persons 表）
            Officers officer = new Officers();
            officer.setOfficerId(request.officerId());
            officer.setDepartment(request.department());
            officer.setPosition(request.position());
            officer.setName(request.name());
            officer.setIdNumber(request.idNumber());
            officer.setGender(request.gender());
            officer.setBirthDate(request.birthDate());
            officer.setAddress(request.address());
            officer.setContactInfo(request.contactInfo());
            
            Officers savedOfficer = officersRepository.save(officer);
            
            return new ResponseEntity<>(convertToDto(savedOfficer), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "创建警员失败: " + e.getMessage()));
        }
    }

    // GET /api/officers/{id} - 查询警员
    @GetMapping("/{id}")
    public ResponseEntity<OfficerDTO> getOfficer(@PathVariable String id) {
        Optional<Officers> officerOpt = officersRepository.findById(id);
        if (officerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDto(officerOpt.get()));
    }

    // PUT /api/officers/{id} - 更新警员
    @PutMapping("/{id}")
    public ResponseEntity<OfficerDTO> updateOfficer(@PathVariable String id, @RequestBody CreateOfficerRequest request) {
        Optional<Officers> officerOpt = officersRepository.findById(id);
        if (officerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Officers officer = officerOpt.get();
        
        // 更新警员信息（包括基本信息）
        officer.setDepartment(request.department());
        officer.setPosition(request.position());
        officer.setName(request.name());
        officer.setIdNumber(request.idNumber());
        officer.setGender(request.gender());
        officer.setBirthDate(request.birthDate());
        officer.setAddress(request.address());
        officer.setContactInfo(request.contactInfo());
        
        Officers savedOfficer = officersRepository.save(officer);
        
        return ResponseEntity.ok(convertToDto(savedOfficer));
    }

    // DELETE /api/officers/{id} - 删除警员
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOfficer(@PathVariable String id) {
        Optional<Officers> officerOpt = officersRepository.findById(id);
        if (officerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Officers officer = officerOpt.get();
        // 删除警员记录（不再需要删除关联的人员记录）
        officersRepository.delete(officer);
        
        return ResponseEntity.noContent().build();
    }

    private OfficerDTO convertToDto(Officers entity) {
        return new OfficerDTO(
            entity.getOfficerId(),
            entity.getDepartment(),
            entity.getPosition(),
            new com.example.crimeplatformapi.dto.PersonDTO(
                entity.getIdNumber(),
                entity.getName(),
                entity.getGender(),
                entity.getBirthDate(),
                entity.getAddress(),
                entity.getContactInfo()
            )
        );
    }

    // 创建警员的请求体
    public record CreateOfficerRequest(
        String officerId,
        String department,
        String position,
        String name,
        String idNumber,
        String gender,
        java.time.LocalDate birthDate,
        String address,
        String contactInfo
    ) {}
}

