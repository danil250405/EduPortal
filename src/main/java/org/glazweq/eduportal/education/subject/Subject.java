package org.glazweq.eduportal.education.subject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.glazweq.eduportal.education.specialty.Specialty;
import org.glazweq.eduportal.storage.file_metadata.FileMetadata;
import org.glazweq.eduportal.appUser.teacherSubject.TeacherSubject;
import java.util.List;


@Getter
@Setter
@Entity
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
    @OneToMany(mappedBy = "subject")
    private List<FileMetadata> filesMetadata;
    @OneToMany(mappedBy = "subject")
    private List<TeacherSubject> teacherSubjects;
}