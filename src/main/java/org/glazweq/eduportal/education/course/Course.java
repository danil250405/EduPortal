package org.glazweq.eduportal.education.course;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.glazweq.eduportal.appUser.user.AppUser;
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
    @Lob
    @Column(columnDefinition = "TEXT") // Explicitly define the column type as TEXT
    private String description;

    @Column
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false) // Foreign key to AppUser table
    private AppUser owner; // Course owner

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
// In Course.java

    @Column(name = "is_link")
    private boolean isLink = false; // default to not being a link

    @Column(name = "link_url")
    private String linkUrl; // URL if this is a link, null otherwise


}
