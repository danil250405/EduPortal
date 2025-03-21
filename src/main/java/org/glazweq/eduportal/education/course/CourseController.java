package org.glazweq.eduportal.education.course;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.appUser.teacherSubject.TeacherAssignmentException;
import org.glazweq.eduportal.appUser.teacherSubject.TeacherCourseService;
import org.glazweq.eduportal.appUser.user.AppUser;
import org.glazweq.eduportal.appUser.user.AppUserService;
import org.glazweq.eduportal.education.folder.Folder;
import org.glazweq.eduportal.education.folder.FolderService;


import org.glazweq.eduportal.storage.StorageService;
import org.glazweq.eduportal.storage.file_metadata.FileMetadata;
import org.glazweq.eduportal.storage.file_metadata.FileMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@AllArgsConstructor
public class CourseController {
    FolderService folderService;
    FileMetadataService fileMetadataService;

    StorageService storageService;
    AppUserService appUserService;
    TeacherCourseService teacherCourseService;

    CourseService courseService;
    @PostMapping("/course/add")
    public String addSubject(@RequestParam String courseName,
                             @RequestParam Long folderId) {
        Folder folder = folderService.getFolderById(folderId);
        System.out.printf("=================++++++++++++============");
        Course course = new Course();
        course.setName(courseName);
        course.setFolder(folder);
        courseService.addCourse(course);
        return "redirect:/folders/" + folderId;
    }
//    @{/folders/{folderId}(folderId=${subfolder.id})/course/{courseId}(courseId=${course.id})}

    @PostMapping("/course/delete")
    public String deleteSubject(@RequestParam Long courseId,
                                @RequestParam Long folderId,
                                RedirectAttributes redirectAttributes) {
        boolean isDeleted = courseService.deleteCourse(courseId);
        String infoMessage = isDeleted ? "course deleted successfully" : "You can't delete this course";
        redirectAttributes.addFlashAttribute("infoMessage", infoMessage);
        return "redirect:/folders/" + folderId;
    }



    @GetMapping("/folders/{folderId}/course/{courseId}")
    public String redirectToSubjectPage(@PathVariable("courseId") Long courseId,
                                        @PathVariable("folderId") Long folderId,
                                        Model model) {

        Course course = courseService.getCourseById(courseId);

//        List<FileMetadata> pdfFiles = fileMetadataService.takeFilesBySubjectAndExtension(subject, "pdf");
//        List<FileMetadata> mp4Files = fileMetadataService.takeFilesBySubjectAndExtension(subject, "mp4");
//        model.addAttribute("pdfFiles", pdfFiles);
//        model.addAttribute("mp4Files", mp4Files);
        List<FileMetadata> files = fileMetadataService.takeFilesByCourse(course);

        List<AppUser> availableTeachers = appUserService.getAvailableTeachers(course.getId());
        if (!availableTeachers.isEmpty()) {
            model.addAttribute("availableTeachers", availableTeachers);
        }

        model.addAttribute("files", files);
        model.addAttribute("course", course);

        return "subject_data-page";

    }
    @PostMapping("/course/add-teacher")
    public String addTeacher(@RequestParam Long courseId,
                             @RequestParam(required = true) Long teacherId,
                             RedirectAttributes redirectAttributes) {
        if (teacherId == null) {
            redirectAttributes.addFlashAttribute("infoMessage", "Error: teacher not selected");
            return getRedirectUrl(courseId);
        }

        try {
            teacherCourseService.assignTeacherToSubject(teacherId, courseId);
            redirectAttributes.addFlashAttribute("infoMessage", "Teacher successfully added");
        } catch (TeacherAssignmentException e) {
            redirectAttributes.addFlashAttribute("infoMessage", e.getMessage());
        }

        return getRedirectUrl(courseId);
    }

    // Выносим формирование URL редиректа в отдельный метод
    private String getRedirectUrl(Long courseId) {
        Course course = courseService.getCourseById(courseId);
        Long folderId = course.getFolder().getId();


        return "redirect:/folders/" + folderId + "/" + courseId ;
    }
    @PostMapping("/course/remove-teacher")
    public String removeTeacher(@RequestParam Long courseId,
                                @RequestParam Long teacherId,
                                RedirectAttributes redirectAttributes) {
        try {
            teacherCourseService.removeTeacherFromSubject(teacherId, courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher successfully removed");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error removing teacher: " + e.getMessage());
        }
        Course course = courseService.getCourseById(courseId);
        return "redirect:/folders/" + course.getFolder().getId() + "/" + courseId;
    }

    @PostMapping("/file/upload")
    public String uploadFile(@RequestParam("file") MultipartFile[] files,
                             @ModelAttribute("course-id") Long courseId,
                             @RequestParam(value = "storage-location" )String storageLocation) {
        System.out.println("in upload method==" + storageLocation);
        Course course = courseService.getCourseById(courseId);
        if (storageLocation.equals("Local")) {
            for (MultipartFile file : files) {
                storageService.uploadFileLocal(file, course);
            }
        }
        else if (   storageLocation.equals("Amazon")) {
            for (MultipartFile file : files) {
                storageService.uploadFileAws(file, course);
            }
        }

        return "redirect:/folders/" + course.getFolder().getId() + "/" + courseId;
    }
    @PostMapping("/file/delete")
    public String deleteFile(@ModelAttribute("del-coding-file-name") String fileName,
                             @ModelAttribute("del-subject-id") Long courseId) {
        System.out.println("in delete method");
        Course course = courseService.getCourseById(courseId);
        FileMetadata fileMetadata = fileMetadataService.findFileByCodingName(fileName);
        if (fileMetadata.getPlace().equals("Local")) storageService.deleteLocalFile(fileName, course);
        else if (fileMetadata.getPlace().equals("Amazon")) storageService.deleteAmazonFile(fileName);

        return "redirect:/folders/" + course.getFolder().getId() + "/" + courseId;

    }


    @PostMapping("/updateCourse/{id}")
    public String updateCourse(@PathVariable Long id, @ModelAttribute Course course) {
        courseService.updateCourse(id, course);
        Course course1 = courseService.getCourseById(course.getId());

        return "redirect:/folders/" + course1.getFolder().getId() + "/course/" + course1.getId(); // Перенаправляем обратно на страницу курса
    }


}
