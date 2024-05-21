package org.glazweq.eduportal.storage.file_metadata;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.education.subject.Subject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FileMetadataService {
    FileMetadataRepository fileMetadataRepository;


    public void saveFileMetadataInDb(FileMetadata fileMetadata){
        fileMetadataRepository.save(fileMetadata);

    }
    public List<FileMetadata> takeFilesBySubjectAndExtension(Subject subject, String extension){
        return fileMetadataRepository.getFileMetadataBySubjectAndExtension(subject, extension);
    }
    public FileMetadata findFileByS3Name(String fileS3Name){
        return fileMetadataRepository.getFileMetadataByS3FileName(fileS3Name);
    }
    public void deleteFileByS3Name(String fileS3Name){
        fileMetadataRepository.delete(findFileByS3Name(fileS3Name));
    }
}
