package org.glazweq.eduportal.storage.file_metadata;

import org.glazweq.eduportal.education.course.Course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> getFileMetadataByCourseAndExtension(Course course, String Extension);
    List<FileMetadata> getFileMetadataByCourse(Course course);
    FileMetadata getFileMetadataByCodingFileName(String codingFileName);
}
