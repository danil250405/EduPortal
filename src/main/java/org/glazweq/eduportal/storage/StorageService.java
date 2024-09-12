package org.glazweq.eduportal.storage;

import lombok.extern.slf4j.Slf4j;
import org.glazweq.eduportal.education.subject.Subject;
import org.glazweq.eduportal.storage.file_metadata.FileMetadata;
import org.glazweq.eduportal.storage.file_metadata.FileMetadataService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@Service
@Slf4j
public class StorageService {
    @Value("${file.upload-dir}")
    private String uploadDir;
    private final FileMetadataService fileMetadataService;
    public StorageService(FileMetadataService fileMetadataService) {
        this.fileMetadataService = fileMetadataService;
    }

    public void uploadFile(MultipartFile file, Subject subject) {
        if (file.isEmpty()) {
            log.error("Failed to upload empty file");
            return;
        }

        log.debug("Uploading file: fileName {}, subject name: {}", file.getOriginalFilename(), subject.getName());

        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path uploadPath = createDirectoryStructure(subject).resolve(fileName);

            Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);

            saveOriginalFileName(file.getOriginalFilename(), fileName, subject);

            log.info("File uploaded successfully: {}", uploadPath);
        } catch (IOException e) {
            log.error("Error uploading file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    private Path createDirectoryStructure(Subject subject) throws IOException {
        Path basePath = Paths.get(uploadDir);
        Path facultyPath = basePath.resolve(sanitizePath(subject.getSpecialty().getFaculty().getName()));
        Path specialtyPath = facultyPath.resolve(sanitizePath(subject.getSpecialty().getName()));
        Path subjectPath = specialtyPath.resolve(sanitizePath(subject.getName()));

        Files.createDirectories(subjectPath);

        return subjectPath;
    }

    private String sanitizePath(String path) {
        return path.replaceAll("[^a-zA-Z0-9.-]", "_");
    }



//    private final String bucketName = "eduportalstustorage";
//    @Autowired
//    private AmazonS3 s3Client;


//    public void uploadFile(MultipartFile file, Subject subject) {
//        log.debug("uploading file to amazon service: fileName {}, subject name: {}", file.getOriginalFilename(), subject.getName());
//       try {
//           File fileObject = convertMultiPartFileToFile(file);
//           String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
//           saveOriginalFileName(file.getOriginalFilename(), fileName, subject);
//           s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObject));
//           fileObject.delete();
//           log.info("File upload successfully: {}", file.getOriginalFilename());
//       }catch (Exception e){
//           log.error("Error uploading file in AWS service: fileName {}", file.getOriginalFilename(), e);
//       }
//
//    }

    private void saveOriginalFileName(String originalFileName, String s3FileName, Subject subject) {
    String fileExtension = getFileExtension(originalFileName);
        log.debug("Saving file metadata for original file name: {}, S3 file name: {}, extension: {}", originalFileName, s3FileName, fileExtension);

        try {
            FileMetadata fileMetadata = new FileMetadata(originalFileName, s3FileName, fileExtension, subject);
            fileMetadataService.saveFileMetadataInDb(fileMetadata);
            log.info("File metadata saved successfully for file: {}", s3FileName);
        } catch (Exception e) {
            log.error("Error saving file metadata for file: {}", s3FileName, e);
        }
    }
    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return ""; // Файл не имеет расширения
        }
        return fileName.substring(dotIndex + 1);
    }

//    public  byte[] downloadFile(String fileName) throws IOException {
//        try {
//            S3Object s3Object = s3Client.getObject(bucketName, fileName);
//            S3ObjectInputStream inputStream = s3Object.getObjectContent();
//            return IOUtils.toByteArray(inputStream);
//        } catch (AmazonS3Exception e) {
//            log.error("Error getting file from S3: {}", fileName, e);
//            throw new RuntimeException("Error getting file from S3: " + fileName, e);
//        } catch (SdkClientException e) {
//            log.error("AWS SDK client error while getting file: {}", fileName, e);
//            throw new RuntimeException("AWS SDK client error while getting file: " + fileName, e);
//        } catch (IOException e) {
//            log.error("Error reading file content: {}", fileName, e);
//            throw new RuntimeException("Error reading file content: " + fileName, e);
//        }
//    }
public byte[] downloadFile(String fileName, Subject subject) throws IOException {

    Path filePath = Paths.get(uploadDir)
            .resolve(subject.getSpecialty().getFaculty().getName().replace(" ", "_"))
            .resolve(subject.getSpecialty().getName().replace(" ", "_"))
            .resolve(subject.getName().replace(" ", "_"))
            .resolve(fileName.replace(" ", "_"));

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
//    private File convertMultiPartFileToFile(MultipartFile file) {
//        File convertedFile = new File(file.getOriginalFilename());
//        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
//            fos.write(file.getBytes());
//        } catch (IOException e) {
//            log.error("Error converting multipartFile to file", e);
//        }
//        return convertedFile;
//    }
//    public String deleteFile(String fileName){
//        try {
//            s3Client.deleteObject(bucketName, fileName);
//            fileMetadataService.deleteFileByS3Name(fileName);
//            log.info("File deleted successfully: {}", fileName);
//        } catch (AmazonS3Exception e) {
//            log.error("Error deleting file from S3: {}", fileName, e);
//        } catch (SdkClientException e) {
//            log.error("AWS SDK client error while deleting file: {}", fileName, e);
//        } catch (Exception e) {
//            log.error("Unexpected error while deleting file: {}", fileName, e);
//        }
//
//        return fileName + " removed ...";
//    }
public void deleteFile(String fileName, Subject subject) {

    Path filePath = Paths.get(uploadDir)
            .resolve(subject.getSpecialty().getFaculty().getName().replace(" ", "_"))
            .resolve(subject.getSpecialty().getName().replace(" ", "_"))
            .resolve(subject.getName().replace(" ", "_"))
            .resolve(fileName.replace(" ", "_"));
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
}
