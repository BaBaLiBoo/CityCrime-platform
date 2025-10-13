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

    private PersonDTO convertToDto(Persons entity) {
        return new PersonDTO(
            entity.getPersonId(),
            entity.getName(),
            entity.getIdNumber(),
            entity.getGender(),
            entity.getBirthDate(),
            entity.getAddress(),
            entity.getContactInfo()
        );
    }
}