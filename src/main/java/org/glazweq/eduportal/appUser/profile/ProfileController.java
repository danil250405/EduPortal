package org.glazweq.eduportal.appUser.profile;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.appUser.teacherSubject.TeacherCourse;
import org.glazweq.eduportal.appUser.teacherSubject.TeacherCourseService;
import org.glazweq.eduportal.appUser.user.AppUser;

import org.glazweq.eduportal.appUser.user.AppUserRole;
import org.glazweq.eduportal.appUser.user.AppUserService;
import org.glazweq.eduportal.education.course.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.format.DateTimeFormatter;
import java.util.List;
@Controller
@AllArgsConstructor
public class ProfileController {
    ProfileService profileService;
    AppUserService appUserService;
    TeacherCourseService teacherCourseService;

    @GetMapping("/profile")
    public String showProfilePage(Model model) {
        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String formattedCreatedAt = user.getCreatedAt().format(formatter); // Форматируем дату
        if (user.getAppUserRole() == AppUserRole.TEACHER) {
            List<TeacherCourse> coursesAssignedToTeacher = teacherCourseService.getCoursesAssignedToUser((long) user.getId());
            model.addAttribute("teacherCoursesAssignedToUser", coursesAssignedToTeacher);
        }
        model.addAttribute("user", user);
        model.addAttribute("formattedCreatedAt", formattedCreatedAt); // Добавляем отформатированную дату
        return "profile-page";
    }
    @GetMapping("/teachers")
    public String showTeachers(Model model) {
        List<AppUser> teachers = appUserService.getAllTeachers();
        model.addAttribute("teachers", teachers);
        return "teachers-page";
    }
    @GetMapping("/teacher/{teacherId}")
    public String showTeacherPage(@PathVariable Long teacherId,
                                  Model model
                                 ) {
        AppUser teacher = appUserService.getUserById(teacherId);
        List<TeacherCourse> coursesAssignedToTeacher = teacherCourseService.getCoursesAssignedToUser(teacherId);

        model.addAttribute("teacherCoursesAssignedToUser", coursesAssignedToTeacher);
        model.addAttribute("teacher", teacher);

        return "teacher-page";
    }
}
