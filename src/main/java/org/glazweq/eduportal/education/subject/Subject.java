package org.glazweq.eduportal.education.subject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.glazweq.eduportal.education.specialty.Specialty;

@Entity
@Getter
@Setter

public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String abbreviation;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String semester;
    @Column(nullable = false)
    private String type;
    @ManyToOne
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;

}