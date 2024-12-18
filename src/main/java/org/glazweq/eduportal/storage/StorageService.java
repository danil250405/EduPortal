package org.glazweq.eduportal.storage;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.glazweq.eduportal.education.subject.Subject;
import org.glazweq.eduportal.storage.file_metadata.FileMetadata;
import org.glazweq.eduportal.storage.file_metadata.FileMetadataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    public void uploadFileLocal(MultipartFile file, Subject subject) {
        if (file.isEmpty()) {
            log.error("Failed to upload empty file");
            return;
        }

        log.debug("Uploading file: fileName {}, subject name: {}", file.getOriginalFilename(), subject.getName());

        try {
            String place = "Local";
            String fileName = sanitizeName(UUID.randomUUID().toString() + "_" + file.getOriginalFilename());

            Path uploadPath = createDirectoryStructure(subject).resolve(fileName);

            Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
            saveOriginalFileName(file.getOriginalFilename(), fileName, subject, file.getSize(), place);

            log.info("File uploaded successfully: {}", uploadPath);
        } catch (IOException e) {
            log.error("Error uploading file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }
    public void uploadFileAws(MultipartFile file, Subject subject) {
        log.debug("uploading file to amazon service: fileName {}, subject name: {}", file.getOriginalFilename(), subject.getName());
        try {
            String place = "Amazon";
            File fileObject = convertMultiPartFileToFile(file);
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            saveOriginalFileName(file.getOriginalFilename(), fileName, subject, file.getSize(), place);
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
    private Path createDirectoryStructure(Subject subject) throws IOException {
        Path basePath = Paths.get(uploadDir);
        Path facultyPath = basePath.resolve(sanitizeName(subject.getSpecialty().getFaculty().getName()));
        Path specialtyPath = facultyPath.resolve(sanitizeName(subject.getSpecialty().getName()));
        Path subjectPath = specialtyPath.resolve(sanitizeName(subject.getName()));

        Files.createDirectories(subjectPath);

        return subjectPath;
    }

    private String sanitizeName(String name) {
        return name.replaceAll("[^a-zA-Z0-9.-]", "_");
    }



    private void saveOriginalFileName(String originalFileName, String codingFileName, Subject subject, Long fileSize, String place) {
    String fileExtension = getFileExtension(originalFileName);
        log.debug("Saving file metadata for original file name: {}, S3 file name: {}, extension: {}", originalFileName, codingFileName, fileExtension);

        try {
            FileMetadata fileMetadata = new FileMetadata(sanitizeName(originalFileName), codingFileName, fileExtension, subject, fileSize, place);
            fileMetadataService.saveFileMetadataInDb(fileMetadata);
            log.info("File metadata saved successfully for file: {}", codingFileName);
        } catch (Exception e) {
            log.error("Error saving file metadata for file: {}", codingFileName, e);
        }
    }
    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return ""; // Файл не имеет расширения
        }
        return fileName.substring(dotIndex + 1);
    }


public byte[] downloadLocalFile(String fileName, Subject subject) throws IOException {
    fileName =sanitizeName(fileName);
    Path filePath = getFilePath(fileName, subject);

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
public void deleteLocalFile(String fileName, Subject subject) {

    Path filePath = getFilePath(fileName, subject);
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
//    public byte[] viewFile(String fileName, Subject subject) throws IOException {
//
//        Path filePath = getFilePath(fileName, subject);
//
//        try {
//            if (!Files.exists(filePath)) {
//                log.error("File not found: {}", fileName);
//                throw new IOException("File not found: " + fileName);
//            }
//
//            byte[] fileContent = Files.readAllBytes(filePath);
//            log.info("File downloaded successfully: {}", fileName);
//            return fileContent;
//        } catch (IOException e) {
//            log.error("Error reading file: {}", fileName, e);
//            throw new IOException("Error reading file: " + fileName, e);
//        }
//    }
    public Path getFilePath(String fileName, Subject subject) {
        return Paths.get(uploadDir)
                .resolve(sanitizeName(subject.getSpecialty().getFaculty().getName()))
                .resolve(sanitizeName(subject.getSpecialty().getName()))
                .resolve(sanitizeName(subject.getName()))
                .resolve(sanitizeName(fileName));
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
