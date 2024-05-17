package org.glazweq.eduportal.storage.file_metadata;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FileMetadataService {
    FileMetadataRepository fileMetadataRepository;


    public boolean saveFileMetadataInDb(FileMetadata fileMetadata){
        fileMetadataRepository.save(fileMetadata);

        return true;

    }
}
