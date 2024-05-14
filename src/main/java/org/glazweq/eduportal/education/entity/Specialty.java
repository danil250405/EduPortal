package org.glazweq.eduportal.education.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;
    @OneToMany(mappedBy = "specialty")
    private List<Subject> subjects;

}