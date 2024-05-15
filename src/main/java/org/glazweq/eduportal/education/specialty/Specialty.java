package org.glazweq.eduportal.education.specialty;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.glazweq.eduportal.education.subject.Subject;
import org.glazweq.eduportal.education.faculty.Faculty;

import java.util.List;

@Entity
@Getter
@Setter
public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String abbreviation;
    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;
    @OneToMany(mappedBy = "specialty")
    private List<Subject> subjects;

}