package org.glazweq.eduportal.storage.file_metadata;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.education.subject.Course;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FileMetadataService {
    FileMetadataRepository fileMetadataRepository;


    public void saveFileMetadataInDb(FileMetadata fileMetadata){
        fileMetadataRepository.save(fileMetadata);

    }
    public List<FileMetadata> takeFilesBySubjectAndExtension(Course course, String extension){

        return fileMetadataRepository.getFileMetadataByCourseAndExtension(course, extension);
    }
    public List<FileMetadata> takeFilesByCourse(Course course){
        return fileMetadataRepository.getFileMetadataByCourse(course);
    }
    public FileMetadata findFileByCodingName(String fileCodingName){
        return fileMetadataRepository.getFileMetadataByCodingFileName(fileCodingName);
    }
    public void deleteFileByName(String fileCodingName){
        fileMetadataRepository.delete(findFileByCodingName(fileCodingName));
    }
}
