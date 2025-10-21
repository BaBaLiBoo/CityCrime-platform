package com.example.crimeplatformapi.service;

import com.example.crimeplatformapi.entity.Persons;
import com.example.crimeplatformapi.exception.ResourceNotFoundException;
import com.example.crimeplatformapi.repository.PersonsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonService {

    @Autowired
    private PersonsRepository personsRepository;

    public List<Persons> findAllPersons() {
        return personsRepository.findAll();
    }

    public Persons findById(String idNumber) {
        return personsRepository.findById(idNumber)
                .orElseThrow(() -> new ResourceNotFoundException("未找到身份证号为 " + idNumber + " 的人员"));
    }

    @Transactional
    public Persons createPerson(Persons person) {
        // 检查身份证号是否已存在
        if (personsRepository.existsById(person.getIdNumber())) {
            throw new IllegalArgumentException("身份证号 " + person.getIdNumber() + " 已存在");
        }
        return personsRepository.save(person);
    }

    @Transactional
    public Persons update(String idNumber, Persons personDetails) {
        Persons existingPerson = findById(idNumber);
        
        existingPerson.setName(personDetails.getName());
        existingPerson.setGender(personDetails.getGender());
        existingPerson.setBirthDate(personDetails.getBirthDate());
        existingPerson.setAddress(personDetails.getAddress());
        existingPerson.setContactInfo(personDetails.getContactInfo());

        return personsRepository.save(existingPerson);
    }

    @Transactional
    public void delete(String idNumber) {
        if (!personsRepository.existsById(idNumber)) {
            throw new ResourceNotFoundException("删除人员失败：未找到身份证号为 " + idNumber + " 的人员");
        }
        personsRepository.deleteById(idNumber);
    }
}