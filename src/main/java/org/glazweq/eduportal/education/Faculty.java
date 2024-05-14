package org.glazweq.eduportal.education;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Faculty {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String name;
    @OneToMany(mappedBy = "faculty")
    private List<Specialty> specialties;

}