package org.glazweq.eduportal.education.subject_data;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.education.faculty.Faculty;
import org.glazweq.eduportal.education.specialty.Specialty;
import org.glazweq.eduportal.education.subject.Subject;
import org.glazweq.eduportal.education.subject.SubjectService;
import org.glazweq.eduportal.storage.StorageService;
import org.glazweq.eduportal.storage.file_metadata.FileMetadata;
import org.glazweq.eduportal.storage.file_metadata.FileMetadataService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@AllArgsConstructor
public class SubjectDataController {
    FileMetadataService fileMetadataService;
    SubjectService subjectService;
    StorageService storageService;
    @GetMapping("/faculties/{facultyAbbr}/{specialtyAbbr}/{subjectAbbr}")
    public String redirectToFacultyPage(@PathVariable("facultyAbbr") String facultyAbbr,
                                        @PathVariable("specialtyAbbr") String specialtyAbbr,
                                        @PathVariable("subjectAbbr") String subjectAbbr,
                                        Model model) {

        Subject subject = subjectService.getSubjectBySubjectAbbrAndSpecialtyAbbr(subjectAbbr, specialtyAbbr);
        List<FileMetadata> jpgFiles = fileMetadataService.takeFilesBySubjectAndExtension(subject, "jpg");
        model.addAttribute("jpgFiles", jpgFiles);
        model.addAttribute("subject", subject);

        return "subject_data-page";

    }
    @PostMapping("/file/upload")
    public String uploadFile(@RequestParam("file") MultipartFile[] files,
                             @ModelAttribute("subject-id") Long subjectId) {
        System.out.println("in upload method");
        Subject subject = subjectService.getSubjectById(subjectId);
        for (MultipartFile file : files) {
            storageService.uploadFile(file, subject);
        }
        String facultyAbbr = subject.getSpecialty().getFaculty().getAbbreviation();
        String specialtyAbbr = subject.getSpecialty().getAbbreviation();
        String subjectAbbr = subject.getAbbreviation();
        return "redirect:/faculties/" + facultyAbbr + "/" + specialtyAbbr + "/" + subjectAbbr;
    }
    @PostMapping("/file/delete")
    public String deleteFile(@ModelAttribute("del-s3-file-name") String fileName,
                             @ModelAttribute("del-subject-id") Long subjectId) {
        System.out.println("in delete method");
        Subject subject = subjectService.getSubjectById(subjectId);

        storageService.deleteFile(fileName);
        String facultyAbbr = subject.getSpecialty().getFaculty().getAbbreviation();
        String specialtyAbbr = subject.getSpecialty().getAbbreviation();
        String subjectAbbr = subject.getAbbreviation();
        return "redirect:/faculties/" + facultyAbbr + "/" + specialtyAbbr + "/" + subjectAbbr;
    }
//    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file,
//                                             @RequestParam(value = "subject") Subject subject) {
//        return new ResponseEntity<>(service.uploadFile(file, subject), HttpStatus.OK);
//    }
//@GetMapping("/file/download/subject/{subjectId}/{fileName}")
//public ResponseEntity<byte[]> downloadFile(@PathVariable("fileName") String fileName,
//                                           @PathVariable("subjectId") Long subjectId) throws IOException {
//
//    Subject subject = subjectService.getSubjectById(subjectId);
//    byte[] fileContent = storageService.downloadFile(fileName);
//
//    HttpHeaders headers = new HttpHeaders();
//    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//    headers.setContentDispositionFormData("attachment", fileName);
//    headers.setContentLength(fileContent.length);
//
//    return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
//}
}
