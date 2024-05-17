package org.glazweq.eduportal.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import jakarta.mail.Multipart;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.amazonaws.util.IOUtils;
import org.glazweq.eduportal.education.subject.Subject;
import org.glazweq.eduportal.storage.file_metadata.FileMetadata;
import org.glazweq.eduportal.storage.file_metadata.FileMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class StorageService {
    private final String bucketName = "eduportalstustorage";
    @Autowired
    private AmazonS3 s3Client;
    private final FileMetadataService fileMetadataService;

    public String uploadFile(MultipartFile file, Subject subject) {
        File fileObject = convertMultiPartFileToFile(file);
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        saveOriginalFileName(file.getOriginalFilename(), fileName, subject);
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObject));
        fileObject.delete();
        return "File uploaded: " + fileName;
    }

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

    public  byte[] downloadFile(String fileName) throws IOException {
      S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        return IOUtils.toByteArray(inputStream);
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
    public String deleteFile(String fileName){
        s3Client.deleteObject(bucketName, fileName);
        return fileName + " removed ...";
    }
}
