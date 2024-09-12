package org.glazweq.eduportal.storage.file_metadata;

import jakarta.persistence.*;
import lombok.Getter;
import org.glazweq.eduportal.education.specialty.Specialty;
import org.glazweq.eduportal.education.subject.Subject;

@Entity
@Getter
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

    public FileMetadata(String originalFileName, String codingFileName, String extension, Subject subject) {
        this.originalFileName = originalFileName;
        this.codingFileName = codingFileName;
        this.extension = extension;
        this.subject = subject;
    }

    public FileMetadata() {
    }
}
