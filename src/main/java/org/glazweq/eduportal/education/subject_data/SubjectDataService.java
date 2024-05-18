package org.glazweq.eduportal.education.subject_data;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.storage.file_metadata.FileMetadata;
import org.glazweq.eduportal.storage.file_metadata.FileMetadataService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SubjectDataService {
    FileMetadataService fileMetadataService;
}
