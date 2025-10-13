package com.example.crimeplatformapi.controller;

import com.example.crimeplatformapi.dto.*;
import com.example.crimeplatformapi.entity.Cases;
import com.example.crimeplatformapi.service.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;

@RestController
@RequestMapping("/api/cases")

public class CaseController {

    @Autowired
    private CaseService caseService;

    /**
     * 获取所有案件的简洁列表
     * GET /api/cases
     * @return List<CaseDTO>
     */
    @GetMapping
    public List<CaseDTO> getAllCases() {
        return caseService.findAllCases().stream()
                .map(this::convertToCaseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取单个案件的详细信息
     * GET /api/cases/{id}
     * @param id 案件ID
     * @return ResponseEntity<CaseDetailDTO>
     */
    @GetMapping("/{id}")
    public ResponseEntity<CaseDetailDTO> getCaseById(@PathVariable Integer id) {
        Cases caseEntity = caseService.findCaseById(id);
        return ResponseEntity.ok(convertToCaseDetailDTO(caseEntity));
    }

    /**
     * 创建一个新案件
     * POST /api/cases
     * @param request 包含案件信息的请求体
     * @return ResponseEntity<CaseDTO>
     */
    @PostMapping
    public ResponseEntity<CaseDTO> createCase(@RequestBody CreateCaseRequest request) {
        Cases createdCase = caseService.createCase(request);
        return new ResponseEntity<>(convertToCaseDTO(createdCase), HttpStatus.CREATED);
    }

    /**
     * 根据ID更新一个案件
     * PUT /api/cases/{id}
     * @param id 案件ID
     * @param request 包含更新信息的请求体
     * @return ResponseEntity<CaseDTO>
     */
    @PutMapping("/{id}")
    public ResponseEntity<CaseDTO> updateCase(@PathVariable Integer id, @RequestBody CreateCaseRequest request) {
        Cases updatedCase = caseService.updateCase(id, request);
        return ResponseEntity.ok(convertToCaseDTO(updatedCase));
    }

    /**
     * 根据ID删除一个案件
     * DELETE /api/cases/{id}
     * @param id 案件ID
     * @return ResponseEntity<Void>
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCase(@PathVariable Integer id) {
        caseService.deleteCase(id);
        return ResponseEntity.noContent().build(); // 返回 204 No Content
    }

    /**
     * 将一名办案警员关联到一个案件
     * POST /api/cases/{caseId}/officers/{officerId}
     * @param caseId 案件ID
     * @param officerId 警员ID
     * @return ResponseEntity<CaseDetailDTO> 返回更新后的案件详情
     */
    @PostMapping("/{caseId}/officers/{officerId}")
    public ResponseEntity<CaseDetailDTO> addOfficerToCase(@PathVariable Integer caseId, @PathVariable String officerId) {
        Cases updatedCase = caseService.addOfficerToCase(caseId, officerId);
        return ResponseEntity.ok(convertToCaseDetailDTO(updatedCase));
    }
    
    /**
     * 将一个涉案人员（嫌疑人/受害人/证人）关联到一个案件
     * POST /api/cases/{caseId}/persons
     * @param caseId 案件ID
     * @param request 包含 personId 和 roleInCase 的请求体
     * @return ResponseEntity<CaseDetailDTO> 返回更新后的案件详情
     */
    @PostMapping("/{caseId}/persons")
    public ResponseEntity<CaseDetailDTO> addPersonToCase(@PathVariable Integer caseId, @RequestBody AddPersonRequest request) {
        caseService.addPersonToCase(caseId, request.personId(), request.roleInCase());
        Cases updatedCase = caseService.findCaseById(caseId); // 重新获取案件以展示最新关联
        return ResponseEntity.ok(convertToCaseDetailDTO(updatedCase));
    }


    // ----------------------------------------------------------------
    // Private Helper Methods for DTO Conversion (私有辅助方法，用于转换DTO)
    // ----------------------------------------------------------------

    /**
     * 将 Cases 实体转换为 CaseDTO (用于列表)
     */
    private CaseDTO convertToCaseDTO(Cases entity) {
        String address = (entity.getLocation() != null) ? entity.getLocation().getAddress() : "无地点信息";
        return new CaseDTO(
                entity.getCaseId(),
                entity.getCaseTitle(),
                entity.getCaseType(),
                entity.getStatus(),
                entity.getReportTime(),
                address
        );
    }

    /**
     * 将 Cases 实体转换为 CaseDetailDTO (用于详情页)
     */
    private CaseDetailDTO convertToCaseDetailDTO(Cases entity) {
        // 转换地点信息
        LocationDTO locationDTO = null;
        if (entity.getLocation() != null) {
            locationDTO = new LocationDTO(
                    entity.getLocation().getLocationId(),
                    entity.getLocation().getAddress(),
                    entity.getLocation().getDistrict(),
                    entity.getLocation().getLongitude(),
                    entity.getLocation().getLatitude()
            );
        }

        // 转换办案警员信息
        Set<OfficerDTO> officerDTOs = entity.getOfficers().stream().map(officer -> new OfficerDTO(
                officer.getOfficerId(),
                officer.getDepartment(),
                officer.getPosition(),
                new PersonDTO( // 嵌套警员的基础信息
                        officer.getPerson().getPersonId(),
                        officer.getPerson().getName(),
                        officer.getPerson().getIdNumber(),
                        officer.getPerson().getGender(),
                        officer.getPerson().getBirthDate(),
                        officer.getPerson().getAddress(),
                        officer.getPerson().getContactInfo()
                )
        )).collect(Collectors.toSet());

        // 转换涉案人员信息
        Set<CasePersonDTO> casePersonDTOs = entity.getCasePersons().stream().map(casePerson -> new CasePersonDTO(
                casePerson.getPerson().getPersonId(),
                casePerson.getPerson().getName(),
                casePerson.getPerson().getIdNumber(),
                casePerson.getRoleInCase()
        )).collect(Collectors.toSet());


        return new CaseDetailDTO(
                entity.getCaseId(),
                entity.getCaseTitle(),
                entity.getCaseType(),
                entity.getStatus(),
                entity.getReportTime(),
                entity.getDescription(),
                locationDTO,
                officerDTOs,
                casePersonDTOs
        );
    }
}