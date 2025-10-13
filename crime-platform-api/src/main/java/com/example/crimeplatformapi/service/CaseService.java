package com.example.crimeplatformapi.service;

import com.example.crimeplatformapi.dto.CreateCaseRequest;
import com.example.crimeplatformapi.entity.*;
import com.example.crimeplatformapi.exception.ResourceNotFoundException;
import com.example.crimeplatformapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CaseService {

    @Autowired
    private CasesRepository casesRepository;

    @Autowired
    private LocationsRepository locationsRepository;

    @Autowired
    private OfficersRepository officersRepository;

    @Autowired
    private PersonsRepository personsRepository;

    @Autowired
    private CasePersonsRepository casePersonsRepository;

    public List<Cases> findAllCases() {
        return casesRepository.findAll();
    }

    public Cases findCaseById(Integer caseId) {
        return casesRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到ID为 " + caseId + " 的案件"));
    }

    @Transactional
    public Cases createCase(CreateCaseRequest request) {
        // --- 核心修改：直接创建新地点 ---
        // 1. 创建一个新的 Locations 对象
        Locations newLocation = new Locations();
        // 2. 将用户输入的地址设置进去
        newLocation.setAddress(request.locationAddress());
        // 3. 将这个新地点保存到数据库，获取持久化后的对象（包含ID）
        Locations savedLocation = locationsRepository.save(newLocation);

        // 4. 创建案件并关联这个新创建的地点
        Cases newCase = new Cases();
        newCase.setCaseTitle(request.caseTitle());
        newCase.setCaseType(request.caseType());
        newCase.setStatus(request.status());
        newCase.setReportTime(request.reportTime());
        newCase.setDescription(request.description());
        newCase.setLocation(savedLocation); // 关联新地点

        return casesRepository.save(newCase);
    }

    @Transactional
    public Cases updateCase(Integer caseId, CreateCaseRequest request) {
        Cases existingCase = findCaseById(caseId);

        // --- 核心修改：同样直接创建新地点 ---
        Locations newLocation = new Locations();
        newLocation.setAddress(request.locationAddress());
        Locations savedLocation = locationsRepository.save(newLocation);

        existingCase.setCaseTitle(request.caseTitle());
        existingCase.setCaseType(request.caseType());
        existingCase.setStatus(request.status());
        existingCase.setReportTime(request.reportTime());
        existingCase.setDescription(request.description());
        existingCase.setLocation(savedLocation);

        return casesRepository.save(existingCase);
    }

    @Transactional
    public void deleteCase(Integer caseId) {
        if (!casesRepository.existsById(caseId)) {
            throw new ResourceNotFoundException("删除案件失败：未找到ID为 " + caseId + " 的案件");
        }
        casesRepository.deleteById(caseId);
    }

    @Transactional
    public Cases addOfficerToCase(Integer caseId, String officerId) {
        Cases currentCase = findCaseById(caseId);
        Officers officer = officersRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("关联警员失败：未找到ID为 " + officerId + " 的警员"));
        currentCase.getOfficers().add(officer);
        return casesRepository.save(currentCase);
    }

    @Transactional
    public CasePersons addPersonToCase(Integer caseId, Integer personId, String roleInCase) {
        Cases currentCase = findCaseById(caseId);
        Persons person = personsRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("关联涉案人员失败：未找到ID为 " + personId + " 的人员"));

        CasePersonsId casePersonsId = new CasePersonsId();
        casePersonsId.setCaseId(caseId);
        casePersonsId.setPersonId(personId);
        casePersonsId.setRoleInCase(roleInCase);

        CasePersons casePerson = new CasePersons();
        casePerson.setId(casePersonsId);
        casePerson.setCases(currentCase);
        casePerson.setPerson(person);
        casePerson.setRoleInCase(roleInCase);

        return casePersonsRepository.save(casePerson);
    }
}