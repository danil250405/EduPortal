package org.glazweq.eduportal.education.subject_data;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.appUser.teacherSubject.TeacherAssignmentException;
import org.glazweq.eduportal.appUser.teacherSubject.TeacherSubjectService;
import org.glazweq.eduportal.appUser.user.AppUser;
import org.glazweq.eduportal.appUser.user.AppUserService;
import org.glazweq.eduportal.education.subject.Subject;
import org.glazweq.eduportal.education.subject.SubjectService;
import org.glazweq.eduportal.storage.StorageService;
import org.glazweq.eduportal.storage.file_metadata.FileMetadata;
import org.glazweq.eduportal.storage.file_metadata.FileMetadataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@AllArgsConstructor
public class SubjectDataController {
    FileMetadataService fileMetadataService;
    SubjectService subjectService;
    StorageService storageService;
    AppUserService appUserService;
    TeacherSubjectService teacherSubjectService;
    @GetMapping("/faculties/{facultyAbbr}/{specialtyAbbr}/{subjectAbbr}")
    public String redirectToFacultyPage(@PathVariable("facultyAbbr") String facultyAbbr,
                                        @PathVariable("specialtyAbbr") String specialtyAbbr,
                                        @PathVariable("subjectAbbr") String subjectAbbr,
                                        Model model) {

        Subject subject = subjectService.getSubjectBySubjectAbbrAndSpecialtyAbbr(subjectAbbr, specialtyAbbr);

//        List<FileMetadata> pdfFiles = fileMetadataService.takeFilesBySubjectAndExtension(subject, "pdf");
//        List<FileMetadata> mp4Files = fileMetadataService.takeFilesBySubjectAndExtension(subject, "mp4");
//        model.addAttribute("pdfFiles", pdfFiles);
//        model.addAttribute("mp4Files", mp4Files);
        List<FileMetadata> files = fileMetadataService.takeFilesBySubject(subject);
        List<AppUser> availableTeachers = appUserService.getAvailableTeachers(subject.getId());
        model.addAttribute("availableTeachers", availableTeachers);
        model.addAttribute("files", files);
        model.addAttribute("subject", subject);

        return "subject_data-page";

    }
    @PostMapping("/subject/add-teacher")
    public String addTeacher(@RequestParam Long subjectId,
                             @RequestParam(required = true) Long teacherId,
                             RedirectAttributes redirectAttributes) {
        if (teacherId == null) {
            redirectAttributes.addFlashAttribute("infoMessage", "Error: teacher not selected");
            return getRedirectUrl(subjectId);
        }

        try {
            teacherSubjectService.assignTeacherToSubject(teacherId, subjectId);
            redirectAttributes.addFlashAttribute("infoMessage", "Teacher successfully added");
        } catch (TeacherAssignmentException e) {
            redirectAttributes.addFlashAttribute("infoMessage", e.getMessage());
        }

        return getRedirectUrl(subjectId);
    }

    // Выносим формирование URL редиректа в отдельный метод
    private String getRedirectUrl(Long subjectId) {
        Subject subject = subjectService.getSubjectById(subjectId);
        String facultyAbbr = subject.getSpecialty().getFaculty().getAbbreviation();
        String specialtyAbbr = subject.getSpecialty().getAbbreviation();
        String subjectAbbr = subject.getAbbreviation();

        return "redirect:/faculties/" + facultyAbbr + "/" + specialtyAbbr + "/" + subjectAbbr;
    }
    @PostMapping("/subject/remove-teacher")
    public String removeTeacher(@RequestParam Long subjectId,
                                @RequestParam Long teacherId,
                                RedirectAttributes redirectAttributes) {
        try {
            teacherSubjectService.removeTeacherFromSubject(teacherId, subjectId);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher successfully removed");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error removing teacher: " + e.getMessage());
        }
        Subject subject = subjectService.getSubjectById(subjectId);
        String facultyAbbr = subject.getSpecialty().getFaculty().getAbbreviation();
        String specialtyAbbr = subject.getSpecialty().getAbbreviation();
        String subjectAbbr = subject.getAbbreviation();
        return "redirect:/faculties/" + facultyAbbr + "/" + specialtyAbbr + "/" + subjectAbbr;
    }

    @PostMapping("/file/upload")
    public String uploadFile(@RequestParam("file") MultipartFile[] files,
                             @ModelAttribute("subject-id") Long subjectId,
                             @RequestParam(value = "storage-location" )String storageLocation) {
        System.out.println("in upload method==" + storageLocation);
        Subject subject = subjectService.getSubjectById(subjectId);
        if (storageLocation.equals("Local")) {
        for (MultipartFile file : files) {
            storageService.uploadFileLocal(file, subject);
        }
        }
        else if (   storageLocation.equals("Amazon")) {
            for (MultipartFile file : files) {
                storageService.uploadFileAws(file, subject);
            }
        }
        String facultyAbbr = subject.getSpecialty().getFaculty().getAbbreviation();
        String specialtyAbbr = subject.getSpecialty().getAbbreviation();
        String subjectAbbr = subject.getAbbreviation();
        return "redirect:/faculties/" + facultyAbbr + "/" + specialtyAbbr + "/" + subjectAbbr;
    }
    @PostMapping("/file/delete")
    public String deleteFile(@ModelAttribute("del-coding-file-name") String fileName,
                             @ModelAttribute("del-subject-id") Long subjectId) {
        System.out.println("in delete method");
        Subject subject = subjectService.getSubjectById(subjectId);
         FileMetadata fileMetadata = fileMetadataService.findFileByCodingName(fileName);
         if (fileMetadata.getPlace().equals("Local")) storageService.deleteLocalFile(fileName, subject);
        else if (fileMetadata.getPlace().equals("Amazon")) storageService.deleteAmazonFile(fileName);
        String facultyAbbr = subject.getSpecialty().getFaculty().getAbbreviation();
        String specialtyAbbr = subject.getSpecialty().getAbbreviation();
        String subjectAbbr = subject.getAbbreviation();
        return "redirect:/faculties/" + facultyAbbr + "/" + specialtyAbbr + "/" + subjectAbbr;
    }
}
