package org.glazweq.eduportal.education.subject_data;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.glazweq.eduportal.education.subject.Subject;
import org.glazweq.eduportal.education.subject.SubjectService;
import org.glazweq.eduportal.storage.StorageService;
import org.glazweq.eduportal.storage.file_metadata.FileMetadataService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

@RestController
@RequestMapping("/file")
@AllArgsConstructor
@Slf4j
public class DataRestController {

    private SubjectService subjectService;
    private StorageService storageService;
    FileMetadataService fileMetadataService;

    @GetMapping("/download/{fileName}/{subjectId}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName, @PathVariable Long subjectId) throws IOException {
        Subject subject = subjectService.getSubjectById(subjectId);
        byte[] data = storageService.downloadFile(fileName, subject);
        String originalName = fileMetadataService.findFileByCodingName(fileName).getOriginalFileName();
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + originalName + "\"")
                .body(resource);
    }
@GetMapping("/view/{fileName}/{subjectId}")
public ResponseEntity<Resource> viewFile(@PathVariable String fileName, @PathVariable Long subjectId) {
    try {
        Subject subject = subjectService.getSubjectById(subjectId);
        Path filePath = storageService.getFilePath(fileName, subject);
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = storageService.getMimeType(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    } catch (IOException e) {
        log.error("Failed to view file", e);
        return ResponseEntity.badRequest().build();
    }
}


}