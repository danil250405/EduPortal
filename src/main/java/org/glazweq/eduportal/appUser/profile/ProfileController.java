package org.glazweq.eduportal.appUser.profile;

import org.glazweq.eduportal.appUser.user.AppUser;
import org.glazweq.eduportal.education.faculty.Faculty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;
@Controller
public class ProfileController {
    ProfileService profileService;

    @GetMapping("/profile")
    public String showProfilePage(Model model) {
        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String formattedCreatedAt = user.getCreatedAt().format(formatter); // Форматируем дату
        model.addAttribute("user", user);
        model.addAttribute("formattedCreatedAt", formattedCreatedAt); // Добавляем отформатированную дату
        return "profile-page";
    }


}
