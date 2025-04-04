package org.glazweq.eduportal.storage;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;


import org.glazweq.eduportal.education.course.Course;
import org.glazweq.eduportal.education.folder.Folder;
import org.glazweq.eduportal.storage.file_metadata.FileMetadata;
import org.glazweq.eduportal.storage.file_metadata.FileMetadataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@Service
@Slf4j
public class StorageService {
    private final String bucketName = "eduportalstustorage";
    @Autowired
    private AmazonS3 s3Client;
    @Value("${file.upload-dir}")
    private String uploadDir;
    private final FileMetadataService fileMetadataService;
    public StorageService(FileMetadataService fileMetadataService) {
        this.fileMetadataService = fileMetadataService;
    }

    public void uploadFileLocal(MultipartFile file, Course course) {
        if (file.isEmpty()) {
            log.error("Failed to upload empty file");
            return;
        }

        log.debug("Uploading file: fileName {}, course name: {}", file.getOriginalFilename(), course.getName());

        try {
            String place = "Local";
            String fileName = sanitizeName(UUID.randomUUID().toString() + "_" + file.getOriginalFilename());

            Path uploadPath = createDirectoryStructure(course).resolve(fileName);

            Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
            saveOriginalFileName(file.getOriginalFilename(), fileName, course, file.getSize(), place);

            log.info("File uploaded successfully: {}", uploadPath);
        } catch (IOException e) {
            log.error("Error uploading file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }
    public void uploadFileAws(MultipartFile file, Course course) {
        log.debug("uploading file to amazon service: fileName {}, subject name: {}", file.getOriginalFilename(), course.getName());
        try {
            String place = "Amazon";
            File fileObject = convertMultiPartFileToFile(file);
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            saveOriginalFileName(file.getOriginalFilename(), fileName, course, file.getSize(), place);
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObject));
            fileObject.delete();
            log.info("File upload successfully: {}", file.getOriginalFilename());
        }catch (Exception e){
            log.error("Error uploading file in AWS service: fileName {}", file.getOriginalFilename(), e);
        }

    }
    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }
    private Path createDirectoryStructure(Course course) throws IOException {
        Path basePath = Paths.get(uploadDir);

        // Build the folder path recursively
        Path folderPath = buildFolderPath(basePath, course.getFolder());

        // Add the course name as a subdirectory
        Path coursePath = folderPath.resolve(sanitizeName(course.getName()));

        Files.createDirectories(coursePath); // Create the entire directory structure

        return coursePath;
    }

    // Recursive helper method to build the folder path
    private Path buildFolderPath(Path basePath, Folder folder) {
        if (folder == null) {
            return basePath; // Handle cases where folder is null (though it shouldn't be in your case)
        }

        // Recursively build the path from the parent folder
        Path parentPath = (folder.getParentFolder() != null) ?
                buildFolderPath(basePath, folder.getParentFolder()) : // Recursive call
                basePath; // Base case: no parent folder, start from basePath
        return parentPath.resolve(sanitizeName(folder.getName())); // Add the current folder name
    }

    private String sanitizeName(String name) {
        return name.replaceAll("[^a-zA-Z0-9.-]", "_");
    }



    private void saveOriginalFileName(String originalFileName, String codingFileName, Course course, Long fileSize, String place) {
    String fileExtension = getFileExtension(originalFileName);
        log.debug("Saving file metadata for original file name: {}, S3 file name: {}, extension: {}", originalFileName, codingFileName, fileExtension);

        try {
            FileMetadata fileMetadata = new FileMetadata(sanitizeName(originalFileName), codingFileName, fileExtension, course, fileSize, place);
            fileMetadataService.saveFileMetadataInDb(fileMetadata);
            log.info("File metadata saved successfully for file: {}", codingFileName);
        } catch (Exception e) {
            log.error("Error saving file metadata for file: {}", codingFileName, e);
        }
    }



public byte[] downloadLocalFile(String fileName, Course course) throws IOException {
    fileName =sanitizeName(fileName);
    Path filePath = getFilePath(fileName, course);

    System.out.println(filePath);
    try {
        if (!Files.exists(filePath)) {
            log.error("File not found: {}", fileName);
            throw new IOException("File not found: " + fileName);
        }

        byte[] fileContent = Files.readAllBytes(filePath);
        log.info("File downloaded successfully: {}", fileName);
        return fileContent;
    } catch (IOException e) {
        log.error("Error reading file: {}", fileName, e);
        throw new IOException("Error reading file: " + fileName, e);
    }
}
    public  byte[] downloadAmazonFile(String fileName) throws IOException {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, fileName);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            return IOUtils.toByteArray(inputStream);
        } catch (AmazonS3Exception e) {
            log.error("Error getting file from S3: {}", fileName, e);
            throw new RuntimeException("Error getting file from S3: " + fileName, e);
        } catch (SdkClientException e) {
            log.error("AWS SDK client error while getting file: {}", fileName, e);
            throw new RuntimeException("AWS SDK client error while getting file: " + fileName, e);
        } catch (IOException e) {
            log.error("Error reading file content: {}", fileName, e);
            throw new RuntimeException("Error reading file content: " + fileName, e);
        }
    }
public void deleteLocalFile(String fileName, Course course) {

    Path filePath = getFilePath(fileName, course);
    System.out.printf(filePath.toString() + "----------");
    try {
        if (Files.deleteIfExists(filePath)) {
            fileMetadataService.deleteFileByName(fileName);
            log.info("File deleted successfully: {}", fileName);
        } else {
            log.warn("File not found: {}", fileName);
        }
    } catch (IOException e) {
        log.error("Error deleting file: {}", fileName, e);
        e.getMessage();
    } catch (Exception e) {
        log.error("Unexpected error while deleting file: {}", fileName, e);
        e.getMessage();
    }
}
    public String deleteAmazonFile(String fileName){
        try {
            s3Client.deleteObject(bucketName, fileName);
            fileMetadataService.deleteFileByName(fileName);
            log.info("File deleted successfully: {}", fileName);
        } catch (AmazonS3Exception e) {
            log.error("Error deleting file from S3: {}", fileName, e);
        } catch (SdkClientException e) {
            log.error("AWS SDK client error while deleting file: {}", fileName, e);
        } catch (Exception e) {
            log.error("Unexpected error while deleting file: {}", fileName, e);
        }

        return fileName + " removed ...";
    }

public ByteArrayResource getResourceFromS3(String fileName, Course course) throws IOException {
    try {
        // Получаем объект из S3
        S3Object s3Object = s3Client.getObject(bucketName, fileName);

        // Получаем содержимое объекта
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        // Преобразуем содержимое в байты
        byte[] fileBytes = IOUtils.toByteArray(inputStream);

        // Создаем ByteArrayResource из байтов
        return new ByteArrayResource(fileBytes);
    } catch (AmazonS3Exception e) {
        log.error("Error getting file from S3: {}", fileName, e);
        throw new RuntimeException("Error getting file from S3: " + fileName, e);
    } catch (SdkClientException | IOException e) {
        log.error("Error processing S3 file: {}", fileName, e);
        throw new RuntimeException("Error processing S3 file: " + fileName, e);
    }
}


    // Метод для получения расширения файла
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        String ext = dotIndex > 0 ? fileName.substring(dotIndex + 1) : "";
        System.out.println("file extension ======== " + ext);
        return ext;
    }

    public Path getFilePath(String fileName, Course course) {
        Path basePath = Paths.get(uploadDir);
        // Build the folder path recursively
        Path folderPath = buildFolderPath(basePath, course.getFolder());

        // Add the course name as a subdirectory
        Path coursePath = folderPath.resolve(sanitizeName(course.getName()));

        return coursePath.resolve(sanitizeName(fileName)); // Add course name as a sub-directory
    }

    public String getMimeType(String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName);
        switch (extension.toLowerCase()) {
            case "png": return "image/png";
            case "jpg":
            case "jpeg": return "image/jpeg";
            case "pdf": return "application/pdf";
            case "txt": return "text/plain";
            case "mp4": return "video/mp4";
            default: return "application/octet-stream";
        }
    }
}
