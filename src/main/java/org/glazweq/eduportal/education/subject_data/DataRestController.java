package org.glazweq.eduportal.education.subject_data;


import lombok.AllArgsConstructor;
import org.glazweq.eduportal.education.subject.Subject;
import org.glazweq.eduportal.storage.StorageService;
import org.glazweq.eduportal.storage.file_metadata.FileMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
@AllArgsConstructor
public class DataRestController {


    private StorageService storageService;
    FileMetadataService fileMetadataService;

//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file,
//                                             @RequestParam(value = "subject") Subject subject) {
//        return new ResponseEntity<>(service.uploadFile(file, subject), HttpStatus.OK);
//    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) throws IOException {
        byte[] data = storageService.downloadFile(fileName);
        String originalName = fileMetadataService.findFileByS3Name(fileName).getOriginalFileName();
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + originalName + "\"")
                .body(resource);
    }
}