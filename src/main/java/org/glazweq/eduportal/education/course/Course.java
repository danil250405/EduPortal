package org.glazweq.eduportal.education.course;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.glazweq.eduportal.storage.file_metadata.FileMetadata;
import org.glazweq.eduportal.appUser.teacherSubject.TeacherCourse;
import org.glazweq.eduportal.education.folder.Folder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column
    private String description;
    @Column
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "folder_id", nullable = false)  // Привязка к папке
    private Folder folder;

    @OneToMany(mappedBy = "course")
    private List<FileMetadata> filesMetadata;

    @OneToMany(mappedBy = "course")
    private List<TeacherCourse> teacherCourses;
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();  // Устанавливаем текущую дату и время при создании записи
    }

}
