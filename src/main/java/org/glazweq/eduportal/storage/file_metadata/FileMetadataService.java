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
    public List<FileMetadata> takeFilesBySubject(Subject subject){
        return fileMetadataRepository.getFileMetadataBySubject(subject);
    }
    public FileMetadata findFileByCodingName(String fileCodingName){
        return fileMetadataRepository.getFileMetadataByCodingFileName(fileCodingName);
    }
    public void deleteFileByName(String fileCodingName){
        fileMetadataRepository.delete(findFileByCodingName(fileCodingName));
    }
}
