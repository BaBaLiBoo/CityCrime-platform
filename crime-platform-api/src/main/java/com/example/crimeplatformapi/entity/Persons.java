package com.example.crimeplatformapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "Persons")
public class Persons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Integer personId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "id_number", nullable = false, unique = true, length = 18)
    private String idNumber;

    @Column(name = "gender", nullable = false)
    private String gender; // ENUM 映射为 String

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "address")
    private String address;

    @Column(name = "contact_info", length = 100)
    private String contactInfo;
}