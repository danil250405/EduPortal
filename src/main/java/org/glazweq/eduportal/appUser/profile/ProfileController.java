package org.glazweq.eduportal.appUser.profile;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.appUser.licenseRequest.LicenseRequest;
import org.glazweq.eduportal.appUser.licenseRequest.LicenseRequestRepository;
import org.glazweq.eduportal.appUser.licenseRequest.LicenseRequestService;
import org.glazweq.eduportal.appUser.licenseRequest.LicenseRequestStatus;
import org.glazweq.eduportal.appUser.teacherSubject.TeacherCourse;
import org.glazweq.eduportal.appUser.teacherSubject.TeacherCourseService;
import org.glazweq.eduportal.appUser.user.AppUser;

import org.glazweq.eduportal.appUser.user.AppUserRole;
import org.glazweq.eduportal.appUser.user.AppUserService;
import org.glazweq.eduportal.education.course.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Controller
@AllArgsConstructor
public class ProfileController {
    private final LicenseRequestService licenseRequestService;
    ProfileService profileService;
    AppUserService appUserService;
    TeacherCourseService teacherCourseService;
    LicenseRequestRepository licenseRequestRepository;

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


    @PostMapping("/licenseRequest")
    public String submitLicenseRequest(@RequestParam("reason") String reason, Model model) {

        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Create LicenseRequest entity and populate it
        LicenseRequest licenseRequest = new LicenseRequest();
        licenseRequest.setReason(reason);
        licenseRequest.setUser(user);
        licenseRequest.setCreatedAt(LocalDateTime.now());
        licenseRequest.setStatus(LicenseRequestStatus.PENDING); // Initial status

        // Save to database
        licenseRequestRepository.save(licenseRequest);

        model.addAttribute("successMessage", "License request submitted successfully.");
        return "redirect:/profile";
    }

    @GetMapping("/admin/licenseRequests")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showLicenseRequests(Model model) {
        List<LicenseRequest> licenseRequests = licenseRequestRepository.findAll();
        model.addAttribute("licenseRequests", licenseRequests);
        return "license-requests";
    }

    @PostMapping("/admin/licenseRequests/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String approveLicenseRequest(@RequestParam("requestId") Long requestId, Model model) {
        LicenseRequest licenseRequest = licenseRequestService.findLicenseRequestById(requestId);
        appUserService.changeUserRole(licenseRequest.getUser(), AppUserRole.TEACHER);
        return processLicenseRequest(requestId, LicenseRequestStatus.APPROVED);
    }

    @PostMapping("/admin/licenseRequests/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String rejectLicenseRequest(@RequestParam("requestId") Long requestId, Model model) {
        return processLicenseRequest(requestId, LicenseRequestStatus.REJECTED);
    }
    private String processLicenseRequest(Long requestId, LicenseRequestStatus status) {
        AppUser adminUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LicenseRequest licenseRequest = licenseRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("License request not found with ID: " + requestId));

        licenseRequest.setStatus(status);
        licenseRequest.setApprovedBy(adminUser);
        licenseRequest.setApprovedAt(LocalDateTime.now());

        licenseRequestRepository.save(licenseRequest);
        return "redirect:/admin/licenseRequests";
    }
}
