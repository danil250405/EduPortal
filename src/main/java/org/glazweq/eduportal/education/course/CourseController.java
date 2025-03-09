package org.glazweq.eduportal.education.course;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.education.folder.Folder;
import org.glazweq.eduportal.education.folder.FolderService;
import org.glazweq.eduportal.education.subject.Course;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
public class CourseController {
    FolderService folderService;
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

    @PostMapping("/course/delete")
    public String deleteSubject(@RequestParam Long courseId,
                                @RequestParam Long folderId,
                                RedirectAttributes redirectAttributes) {
        boolean isDeleted = courseService.deleteCourse(courseId);
        String infoMessage = isDeleted ? "course deleted successfully" : "You can't delete this course";
        redirectAttributes.addFlashAttribute("infoMessage", infoMessage);
        return "redirect:/folders/" + folderId;
    }
}
