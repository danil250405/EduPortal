package org.glazweq.eduportal.controller;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.appUser.AppUserRole;
import org.glazweq.eduportal.registration.RegistrationService;
import org.glazweq.eduportal.appUser.AppUser;
import org.glazweq.eduportal.appUser.AppUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
@AllArgsConstructor
public class AuthController {
    AppUserService appUserService;
    RegistrationService registrationService;
    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        // create model object to store form data
        AppUser user = new AppUser();
        model.addAttribute("user", user);
        return "registration-page";
    }
    @PostMapping("/registration/save")
    public String registration(@ModelAttribute("user") AppUser appUser,
                               BindingResult result,
                               Model model) {

        String messageToClient;

        // Check email
        messageToClient = appUserService.getMsgAboutEmail(appUser.getEmail());
        if (!messageToClient.equals("success")) {
            result.rejectValue("email", null, messageToClient);
            return "registration-page";
        }
        // Check firstName
        messageToClient = appUserService.getMsgAboutFirstName(appUser.getFirstName());
        if (!messageToClient.equals("success")) {
            result.rejectValue("firstName", null, messageToClient);
            return "registration-page";
        }
        // Check lastName
        messageToClient = appUserService.getMsgAboutLastName(appUser.getLastName());
        if (!messageToClient.equals("success")) {
            result.rejectValue("lastName", null, messageToClient);
            return "registration-page";
        }
        // Check password
        messageToClient = appUserService.getMsgAboutPassword(appUser.getPassword());
        if (!messageToClient.equals("success")) {
            result.rejectValue("password", null, messageToClient);
            return "registration-page";
        }
        appUser.setEnabled(true);
        appUser.setCreatedAt(getCurrentDateTime());
        appUser.setAppUserRole(AppUserRole.USER);
        // if everything is good we are make register user
        appUserService.signUpUser(appUser);

        return "redirect:/main";
    }
    private LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}
