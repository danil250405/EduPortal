package org.glazweq.eduportal.storage.file_metadata;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.glazweq.eduportal.education.specialty.Specialty;
import org.glazweq.eduportal.education.subject.Subject;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "file_metadata")
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false, unique = true)
    private String codingFileName;

    @Column(nullable = false)
    private String extension;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
    @Column(nullable = false)
    private Long fileSize;  // размер файла в байтах
    @Column(nullable = false, updatable = false)
    private LocalDate uploadDate;
    @Column(nullable = false)
    private String place;
    public FileMetadata(String originalFileName, String codingFileName, String extension, Subject subject, Long fileSize, String place) {
        this.originalFileName = originalFileName;
        this.codingFileName = codingFileName;
        this.extension = extension;
        this.subject = subject;
        this.fileSize = fileSize;
        this.place = place;
    }

    public FileMetadata() {
    }

    @PrePersist
    protected void onCreate() {
        this.uploadDate = LocalDate.now();  // Устанавливаем текущую дату при создании записи
    }
}
