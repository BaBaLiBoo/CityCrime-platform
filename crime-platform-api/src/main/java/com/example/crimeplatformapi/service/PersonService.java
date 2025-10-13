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

    public Persons findPersonById(Integer personId) {
        return personsRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到ID为 " + personId + " 的人员"));
    }

    @Transactional
    public Persons createPerson(Persons person) {
        // 可以在此添加逻辑，例如检查身份证号是否已存在
        return personsRepository.save(person);
    }

    @Transactional
    public Persons updatePerson(Integer personId, Persons personDetails) {
        Persons existingPerson = findPersonById(personId);
        
        existingPerson.setName(personDetails.getName());
        existingPerson.setIdNumber(personDetails.getIdNumber());
        existingPerson.setGender(personDetails.getGender());
        existingPerson.setBirthDate(personDetails.getBirthDate());
        existingPerson.setAddress(personDetails.getAddress());
        existingPerson.setContactInfo(personDetails.getContactInfo());

        return personsRepository.save(existingPerson);
    }

    @Transactional
    public void deletePerson(Integer personId) {
        if (!personsRepository.existsById(personId)) {
            throw new ResourceNotFoundException("删除人员失败：未找到ID为 " + personId + " 的人员");
        }
        personsRepository.deleteById(personId);
    }
}