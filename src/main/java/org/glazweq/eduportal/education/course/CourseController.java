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
import org.springframework.security.core.context.SecurityContextHolder;
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
    public String addCourse(@RequestParam String courseName,
                            @RequestParam Long folderId,
                            @RequestParam(required = false, defaultValue = "false") boolean isLink, // Получаем значение чекбокса
                            @RequestParam(required = false) String linkUrl, // Получаем URL, если это ссылка
                            RedirectAttributes redirectAttributes) {

        // Валидация: если это ссылка, URL не должен быть пустым
        if (isLink && (linkUrl == null || linkUrl.isBlank())) {
            redirectAttributes.addFlashAttribute("errorMessage", "URL cannot be empty when creating a link course.");
            return "redirect:/folders/" + folderId; // Возвращаемся на страницу папки
        }

        Folder folder = folderService.getFolderById(folderId);
        if (folder == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Parent folder not found.");
            return "redirect:/folders"; // Или другая обработка ошибки
        }

        Course course = new Course();
        course.setName(courseName);
        course.setFolder(folder);
        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        course.setOwner(user);
        course.setLink(isLink); // Устанавливаем флаг ссылки
        course.setLinkUrl(linkUrl); // Устанавливаем URL ссылки

        Course savedCourse = courseService.addCourse(course); // Сохраняем курс

        // Назначаем создателя преподавателем (если такая логика нужна и это не просто ссылка)
        if (savedCourse != null && !isLink) {
            teacherCourseService.assignTeacherToSubject((long) user.getId(), savedCourse.getId());
        }

        redirectAttributes.addFlashAttribute("infoMessage", "Course '" + courseName + "' created successfully.");
        return "redirect:/folders/" + folderId;
    }
//    @{/folders/{folderId}(folderId=${subfolder.id})/course/{courseId}(courseId=${course.id})}

    @PostMapping("/course/delete/{id}")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model) {
        Course course = courseService.getCourseById(id);
        Long folderId = course.getFolder().getId();
        if (!courseService.hasFiles(id)) {
            boolean isDeleted = courseService.deleteCourse(id);
            if (isDeleted) {
                redirectAttributes.addFlashAttribute("infoMessage", "Course deleted successfully");
            } else {
                redirectAttributes.addFlashAttribute("infoMessage", "Failed to delete course");
            }
        } else {
            redirectAttributes.addFlashAttribute("infoMessage", "Cannot delete course with files.");
        }
        model.addAttribute("size",0); // You also should add this parameter in view, to prevent errors
        return "redirect:/folders/" + folderId;
    }

        @GetMapping("/coursesAll")
    public String showCoursesPage(Model model) {
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "subjects-all";
    }

    @GetMapping("/folders/{folderId}/course/{courseId}")
    public String redirectToCoursePage(@PathVariable("courseId") Long courseId,
                                       @PathVariable("folderId") Long folderId, // Keep folderId if needed for context/breadcrumbs
                                       Model model,
                                       RedirectAttributes redirectAttributes) { // Use RedirectAttributes

        Course course = courseService.getCourseById(courseId);

        // Handle case where course doesn't exist
        if (course == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Course with ID " + courseId + " not found.");
            return "redirect:/folders/" + folderId; // Redirect back to the folder page
        }

        // --- Check if it's a link ---
        if (course.isLink()) {
            String redirectUrl = course.getLinkUrl();
            // Basic validation: Ensure URL is not empty
            if (redirectUrl != null && !redirectUrl.isBlank()) {
                // Prepend http:// if it's likely an external link without a protocol (basic check)
                if (!redirectUrl.toLowerCase().startsWith("http://") &&
                        !redirectUrl.toLowerCase().startsWith("https://") &&
                        !redirectUrl.startsWith("/")) { // Don't prepend for internal paths
                    redirectUrl = "http://" + redirectUrl;
                }
                return "redirect:" + redirectUrl; // Perform the redirect
            } else {
                // Handle invalid/empty link URL case
                redirectAttributes.addFlashAttribute("errorMessage", "The link URL for course '" + course.getName() + "' is invalid or missing.");
                return "redirect:/folders/" + folderId; // Redirect back to the folder page
            }
        }

        // --- If it's NOT a link, proceed to show the course details page ---
        List<FileMetadata> files = fileMetadataService.takeFilesByCourse(course); // Assuming this method exists
        AppUser currentUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Determine if the current user can delete files (owner, admin, or assigned teacher)
        boolean canDeleteFiles = currentUser != null && (
                currentUser.getAppUserRole().name().equals("ADMIN") ||
                        (course.getOwner() != null && course.getOwner().getId() == currentUser.getId()) ||
                        course.getTeacherCourses().stream()
                                .anyMatch(tc -> tc.getTeacher().getId() == currentUser.getId())
        );

        model.addAttribute("canDeleteFiles", canDeleteFiles);

        // Get available teachers (example, adjust method signature as needed)
        List<AppUser> availableTeachers = appUserService.getAvailableTeachers(course.getId());
        if (availableTeachers != null && !availableTeachers.isEmpty()) { // Check if list is not null
            model.addAttribute("availableTeachers", availableTeachers);
        } else {
            model.addAttribute("availableTeachers", List.of()); // Ensure it's never null for Thymeleaf
        }


        model.addAttribute("files", files);
        model.addAttribute("course", course);
        model.addAttribute("folder", course.getFolder()); // Add folder for context if needed

        return "subject_data-page"; // Return the name of your course details template
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


        return "redirect:/folders/" + folderId + "/course/" + courseId ;
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
        return getRedirectUrl(courseId);
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

        return "redirect:/folders/" + course.getFolder().getId() + "/course/" + courseId;
    }
    @PostMapping("/file/delete")
    public String deleteFile(
            @RequestParam("del-coding-file-name") String codingFileName,
            @RequestParam("del-course-id") Long courseId // Исправлено имя параметра
    ) {
        System.out.println("in delete method");
        Course course = courseService.getCourseById(courseId);
        FileMetadata fileMetadata = fileMetadataService.findFileByCodingName(codingFileName); // codingFileName вместо fileName
        if (fileMetadata.getPlace().equals("Local")) {
            storageService.deleteLocalFile(codingFileName, course); // codingFileName вместо fileName
        } else if (fileMetadata.getPlace().equals("Amazon")) {
            storageService.deleteAmazonFile(codingFileName); // codingFileName вместо fileName
        }

        return "redirect:/folders/" + course.getFolder().getId() + "/course/" + courseId;
    }


    @PostMapping("/updateCourse/{id}")
    public String updateCourse(@PathVariable Long id, @ModelAttribute Course course) {
        courseService.updateCourse(id, course);
        Course course1 = courseService.getCourseById(course.getId());

        return "redirect:/folders/" + course1.getFolder().getId() + "/course/" + course1.getId(); // Перенаправляем обратно на страницу курса
    }


}
