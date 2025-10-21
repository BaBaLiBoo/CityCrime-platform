package com.example.crimeplatformapi.controller;

import com.example.crimeplatformapi.dto.PersonDTO;
import com.example.crimeplatformapi.entity.Persons;
import com.example.crimeplatformapi.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    // GET /api/persons - 获取所有人员列表
    @GetMapping
    public List<PersonDTO> getAllPersons() {
        return personService.findAllPersons().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // POST /api/persons - 创建一个新的人员记录
    @PostMapping
    public ResponseEntity<PersonDTO> createPerson(@RequestBody Persons person) {
        Persons newPerson = personService.createPerson(person);
        return new ResponseEntity<>(convertToDto(newPerson), HttpStatus.CREATED);
    }

    // GET /api/persons/{id} - 查询人员
    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> getPerson(@PathVariable String id) {
        Persons p = personService.findById(id);
        return ResponseEntity.ok(convertToDto(p));
    }

    // PUT /api/persons/{id} - 更新人员
    @PutMapping("/{id}")
    public ResponseEntity<PersonDTO> updatePerson(@PathVariable String id, @RequestBody Persons payload) {
        Persons p = personService.update(id, payload);
        return ResponseEntity.ok(convertToDto(p));
    }

    // DELETE /api/persons/{id} - 删除人员
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable String id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private PersonDTO convertToDto(Persons entity) {
        return new PersonDTO(
            entity.getIdNumber(),
            entity.getName(),
            entity.getGender(),
            entity.getBirthDate(),
            entity.getAddress(),
            entity.getContactInfo()
        );
    }
}