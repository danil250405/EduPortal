package org.glazweq.eduportal.registration;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.appUser.user.AppUserRole;
import org.glazweq.eduportal.appUser.user.AppUser;
import org.glazweq.eduportal.appUser.user.AppUserService;
import org.glazweq.eduportal.registration.token.ConfirmationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@AllArgsConstructor
public class AuthController {
    AppUserService appUserService;
    RegistrationService registrationService;
    @GetMapping("/login")
    public String login() {
        return "login-page";
    }

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
        appUser.setEnabled(false);
        appUser.setCreatedAt(getCurrentDateTime());
        appUser.setAppUserRole(AppUserRole.USER);
        // if everything is good we are make register user
        registrationService.register(appUser);

        return "confirm-email-page";
    }

    @GetMapping(path = "/registration/confirm")
    public String confirm(@RequestParam("token") String token) throws InterruptedException {
        System.out.println("in confirm getMapping");
        ConfirmationToken confirmationToken = registrationService.confirmToken(token);

        if (confirmationToken != null) {
            System.out.println("in conf gM with confirmed token");

            return "redirect:/login?tokensuccess=true";
        }
        else{
            System.out.println("in conf gM with NOT! confirmed token");
            return "redirect:/login?tokensuccess=false";
        }
//        return "redirect:/main";
    }
    private LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
    @GetMapping("/error-page")
    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException ex, Model model) {
        // Обработка исключения
        model.addAttribute("errorMessage", ex.getMessage());
        return "error-page"; // Возвращаем имя шаблона для отображения страницы с ошибкой
    }
}
