package com.example.crimeplatformapi.controller;

import com.example.crimeplatformapi.dto.ReportRequest;
import com.example.crimeplatformapi.dto.ReportUpdateRequest;
import com.example.crimeplatformapi.entity.*;
import com.example.crimeplatformapi.repository.*;
import com.example.crimeplatformapi.util.Java17Compat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private static final Set<String> LOCKED_STATUSES = Set.of("立案侦查", "已告破", "已归档");

    @Autowired
    private CasesRepository casesRepository;

    @Autowired
    private LocationsRepository locationsRepository;

    @Autowired
    private UserAccountsRepository userAccountsRepository;

    @Autowired
    private PersonsRepository personsRepository;

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    /**
     * 用户举报违法事件
     * POST /api/reports
     * 支持文件上传
     */
    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestPart("report") ReportRequest report,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {
        
        // 验证token并获取用户ID
        Integer userId = extractUserIdFromToken(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body(Java17Compat.mapOf("message", "未认证"));
        }

        Optional<UserAccounts> userOpt = userAccountsRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(401).body(Java17Compat.mapOf("message", "用户不存在"));
        }

        UserAccounts user = userOpt.get();
        
        // 验证用户类型（只有普通用户才能举报）
        if (!"普通用户".equals(user.getUserType())) {
            return ResponseEntity.status(403).body(Java17Compat.mapOf("message", "只有普通用户可以进行举报"));
        }

        // 验证实名认证
        if (user.getIdNumber() == null || user.getIdNumber().trim().isEmpty()) {
            return ResponseEntity.status(403).body(Java17Compat.mapOf("message", "请先完成实名认证"));
        }

        try {
            // 1. 处理文件上传（仅保存文件名，数据库中使用 JSON 数组存储）
            List<String> fileNames = new ArrayList<>();
            if (files != null && files.length > 0) {
                // 确保上传目录存在
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String originalFilename = file.getOriginalFilename();
                        String extension = originalFilename != null && originalFilename.contains(".") 
                            ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                            : "";
                        String filename = UUID.randomUUID().toString() + extension;
                        Path filePath = uploadPath.resolve(filename);
                        Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        fileNames.add(filename);
                    }
                }
            }

            // 2. 创建地点
            Locations location = new Locations();
            location.setAddress(report.locationAddress());
            location.setDistrict(report.district());
            location.setLongitude(report.longitude());
            location.setLatitude(report.latitude());
            Locations savedLocation = locationsRepository.save(location);

            // 2.5. 将举报人信息添加到Persons表（如果不存在）
            String idNumber = user.getIdNumber();
            if (idNumber != null && !idNumber.trim().isEmpty()) {
                Optional<Persons> existingPersonOpt = personsRepository.findById(idNumber);
                if (existingPersonOpt.isEmpty()) {
                    Persons person = new Persons();
                    person.setIdNumber(idNumber);
                    person.setName(user.getRealName() != null ? user.getRealName() : "未知");
                    person.setGender("未知");
                    person.setContactInfo(user.getUsername());
                    personsRepository.save(person);
                }
            }

            // 3. 创建案件（来源为"在线举报"）
            Cases newCase = new Cases();
            newCase.setCaseTitle("用户举报：" + report.caseType());
            newCase.setCaseType(report.caseType());
            newCase.setStatus("已接报");
            newCase.setReportTime(LocalDateTime.now());
            newCase.setDescription(report.description());
            newCase.setSource("在线举报");
            newCase.setReporterId(userId);
            newCase.setLocation(savedLocation);
            
            // 存储文件名列表（JSON格式）
            if (!fileNames.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(fileNames);
                newCase.setMediaFiles(json);
            }

            Cases savedCase = casesRepository.save(newCase);

            Map<String, Object> response = new HashMap<>();
            response.put("caseId", savedCase.getCaseId());
            response.put("message", "举报成功，案件已提交");
            response.put("fileCount", fileNames.size());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Java17Compat.mapOf("message", "文件上传失败: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Java17Compat.mapOf("message", "创建举报失败: " + e.getMessage()));
        }
    }

    /**
     * 获取当前用户的举报记录
     * GET /api/reports/my-reports
     */
    @GetMapping("/my-reports")
    public ResponseEntity<?> getMyReports(
            @RequestHeader(value = "Authorization", required = false) String auth) {
        
        Integer userId = extractUserIdFromToken(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body(Java17Compat.mapOf("message", "未认证"));
        }

        List<Cases> myReports = casesRepository.findByReporterId(userId);
        
        List<Map<String, Object>> reportList = new ArrayList<>();
        for (Cases c : myReports) {
            Map<String, Object> report = new HashMap<>();
            report.put("caseId", c.getCaseId());
            report.put("caseTitle", c.getCaseTitle());
            report.put("caseType", c.getCaseType());
            report.put("status", c.getStatus());
            report.put("reportTime", c.getReportTime());
            report.put("description", c.getDescription());
            if (c.getLocation() != null) {
                report.put("locationAddress", c.getLocation().getAddress());
                report.put("district", c.getLocation().getDistrict());
            }
            reportList.add(report);
        }

        return ResponseEntity.ok(reportList);
    }

    /**
     * 获取用户举报的案件详情（隐藏警员和涉案人员信息）
     * GET /api/reports/{caseId}
     */
    @GetMapping("/{caseId}")
    public ResponseEntity<?> getMyReportDetail(
            @PathVariable Integer caseId,
            @RequestHeader(value = "Authorization", required = false) String auth) {
        
        if (caseId == null) {
            return ResponseEntity.badRequest().body(Java17Compat.mapOf("message", "案件ID不能为空"));
        }

        Integer userId = extractUserIdFromToken(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body(Java17Compat.mapOf("message", "未认证"));
        }

        Optional<Cases> caseOpt = casesRepository.findById(caseId);
        if (!caseOpt.isPresent()) {
            return ResponseEntity.status(404).body(Java17Compat.mapOf("message", "案件不存在"));
        }

        Cases c = caseOpt.get();
        
        // 验证该案件是否属于当前用户
        Integer reporterId = c.getReporterId();
        if (!Objects.equals(reporterId, userId)) {
            return ResponseEntity.status(403).body(Java17Compat.mapOf("message", "无权访问该案件"));
        }

        // 构建案件详情（不包含警员和涉案人员信息）
        Map<String, Object> caseDetail = new HashMap<>();
        caseDetail.put("caseId", c.getCaseId());
        caseDetail.put("caseTitle", c.getCaseTitle());
        caseDetail.put("caseType", c.getCaseType());
        caseDetail.put("status", c.getStatus());
        caseDetail.put("reportTime", c.getReportTime());
        caseDetail.put("filingTime", c.getFilingTime());
        caseDetail.put("solveTime", c.getSolveTime());
        caseDetail.put("archiveTime", c.getArchiveTime());
        caseDetail.put("description", c.getDescription());
        caseDetail.put("source", c.getSource());
        caseDetail.put("mediaFiles", c.getMediaFiles());
        
        if (c.getLocation() != null) {
            Map<String, Object> location = new HashMap<>();
            location.put("address", c.getLocation().getAddress());
            location.put("district", c.getLocation().getDistrict());
            location.put("longitude", c.getLocation().getLongitude());
            location.put("latitude", c.getLocation().getLatitude());
            caseDetail.put("location", location);
        }

        return ResponseEntity.ok(caseDetail);
    }

    /**
     * 更新用户举报的案件（仅限案件尚未进入立案侦查状态）
     * PUT /api/reports/{caseId}
     */
    @PutMapping("/{caseId}")
    public ResponseEntity<?> updateMyReport(
            @PathVariable Integer caseId,
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestBody ReportUpdateRequest request) {

        if (caseId == null) {
            return ResponseEntity.badRequest().body(Java17Compat.mapOf("message", "案件ID不能为空"));
        }

        Integer userId = extractUserIdFromToken(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body(Java17Compat.mapOf("message", "未认证"));
        }

        Optional<Cases> caseOpt = casesRepository.findById(caseId);
        if (caseOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Java17Compat.mapOf("message", "案件不存在"));
        }

        Cases existingCase = caseOpt.get();

        if (!Objects.equals(existingCase.getReporterId(), userId)) {
            return ResponseEntity.status(403).body(Java17Compat.mapOf("message", "无权修改该案件"));
        }

        if (existingCase.getStatus() != null && LOCKED_STATUSES.contains(existingCase.getStatus())) {
            return ResponseEntity.status(400).body(Java17Compat.mapOf("message", "案件已进入" + existingCase.getStatus() + "阶段，无法修改"));
        }

        // 基础校验
        if (request.caseType() == null || request.caseType().trim().isEmpty()
                || request.description() == null || request.description().trim().isEmpty()
                || request.locationAddress() == null || request.locationAddress().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Java17Compat.mapOf("message", "请填写完整的案件信息"));
        }

        // 更新案件信息（不允许用户直接修改状态）
        existingCase.setCaseType(request.caseType());
        existingCase.setCaseTitle("用户举报：" + request.caseType());
        existingCase.setDescription(request.description());

        // 更新地点信息
        Locations location = existingCase.getLocation();
        if (location == null) {
            location = new Locations();
        }
        location.setAddress(request.locationAddress());
        location.setDistrict(request.district());
        location.setLongitude(request.longitude());
        location.setLatitude(request.latitude());
        Locations savedLocation = locationsRepository.save(location);
        existingCase.setLocation(savedLocation);

        Cases savedCase = casesRepository.save(existingCase);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "案件信息已更新");
        response.put("caseId", savedCase.getCaseId());
        response.put("status", savedCase.getStatus());
        return ResponseEntity.ok(response);
    }

    /**
     * 从token中提取用户ID
     */
    private Integer extractUserIdFromToken(String auth) {
        if (auth == null || !auth.startsWith("Bearer dummy-token-")) {
            return null;
        }
        try {
            String idStr = auth.substring("Bearer dummy-token-".length());
            return Integer.parseInt(idStr);
        } catch (Exception e) {
            return null;
        }
    }
}

