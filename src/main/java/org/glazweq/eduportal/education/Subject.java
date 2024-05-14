package org.glazweq.eduportal.education;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Entity
@Getter
@Setter

public class Subject {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "specialty_id")
    @Column(nullable = false)
    private Specialty specialty;

}