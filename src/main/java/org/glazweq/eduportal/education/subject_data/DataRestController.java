package org.glazweq.eduportal.education.subject_data;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.glazweq.eduportal.education.course.Course;
import org.glazweq.eduportal.education.course.CourseService;


import org.glazweq.eduportal.storage.StorageService;
import org.glazweq.eduportal.storage.file_metadata.FileMetadata;
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
import java.io.IOException;

@RestController
@RequestMapping("/file")
@AllArgsConstructor
@Slf4j
public class DataRestController {


    private StorageService storageService;
    private CourseService courseService;
    FileMetadataService fileMetadataService;


    @GetMapping("/download/{fileName}/{subjectId}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName, @PathVariable Long courseId) throws IOException {
        Course course = courseService.getCourseById(courseId);
        FileMetadata fileMetadata = fileMetadataService.findFileByCodingName(fileName);

        byte[] data;
        if (fileMetadata.getPlace().equals("Amazon")) {
            data = storageService.downloadAmazonFile(fileName);
        } else {
            data = storageService.downloadLocalFile(fileName, course);
        }

        String originalName = fileMetadata.getOriginalFileName();
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + originalName + "\"")
                .body(resource);
    }
    @GetMapping("/view/{fileName}/{subjectId}")
    public ResponseEntity<Resource> viewFile(@PathVariable String fileName, @PathVariable Long courseId) {
        try {
            Course course = courseService.getCourseById(courseId);
            FileMetadata fileMetadata = fileMetadataService.findFileByCodingName(fileName);

            Resource resource;

            if (fileMetadata.getPlace().equals("Amazon")) {
                // Для файлов из S3
                resource = storageService.getResourceFromS3(fileName, course);
            } else {
                // Для локальных файлов (существующая логика)
                Path filePath = storageService.getFilePath(fileName, course);
                resource = new UrlResource(filePath.toUri());
            }

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