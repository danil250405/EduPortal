package org.glazweq.eduportal.education.faculty;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.glazweq.eduportal.education.specialty.Specialty;

import java.util.List;

@Entity
@Getter
@Setter
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String abbreviation;
    @Column(nullable = false)
    private String name;
    @OneToMany(mappedBy = "faculty")
    private List<Specialty> specialties;

}