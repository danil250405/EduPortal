package org.glazweq.eduportal.controller;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.registration.RegistrationService;
import org.glazweq.eduportal.user.AppUser;
import org.glazweq.eduportal.user.AppUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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

        // Проверка email
        messageToClient = appUserService.getMsgAboutEmail(appUser.getEmail());
        if (!messageToClient.equals("success")) {
            result.rejectValue("email", null, messageToClient);
            return "registration-page";
        }

        // Проверка firstName
        messageToClient = appUserService.getMsgAboutFirstName(appUser.getFirstName());
        if (!messageToClient.equals("success")) {
            result.rejectValue("firstName", null, messageToClient);
            return "registration-page";
        }

        // Проверка lastName
        messageToClient = appUserService.getMsgAboutLastName(appUser.getLastName());
        if (!messageToClient.equals("success")) {
            result.rejectValue("lastName", null, messageToClient);
            return "registration-page";
        }

        // Проверка password
        messageToClient = appUserService.getMsgAboutPassword(appUser.getPassword());
        if (!messageToClient.equals("success")) {
            result.rejectValue("password", null, messageToClient);
            return "registration-page";
        }

        // Если все проверки пройдены успешно, выполняется регистрация пользователя
        appUserService.signUpUser(appUser);

        // Возможно, здесь нужно перенаправление на другую страницу или какие-то другие действия

//        appUserService.saveUser(appUser);
        return "redirect:/main";
    }
}
