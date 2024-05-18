package org.glazweq.eduportal.storage.file_metadata;

import org.glazweq.eduportal.education.subject.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> getFileMetadataBySubjectAndExtension(Subject subject, String Extension);
    FileMetadata getFileMetadataByS3FileName(String s3FileName);
}
