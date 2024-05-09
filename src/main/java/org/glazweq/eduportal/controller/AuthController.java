package org.glazweq.eduportal.controller;

import lombok.AllArgsConstructor;
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
        System.out.println("IN Save mapping");

        UserDetails existingUserByEmail = appUserService.loadUserByUsername(appUser.getEmail());
        System.out.println("IN Save mapping");
        if (result.hasErrors()) {
            model.addAttribute("user", appUser);
            System.out.println("1");
            return "registration-page";
        }
        //check email
        else if (existingUserByEmail.getUsername() != null && !existingUserByEmail.getUsername().isEmpty()) {
            result.rejectValue("email", null,
                    "You already registered with this email!");
            System.out.println("2");
            return "registration-page";
        }

        else if (!appUser.getEmail().endsWith("@stuba.sk")) {
            result.rejectValue("email", null,
                    "Email must be type xeinstein@stuba.sk");
            System.out.println("3");
            return "registration-page";
        }


        System.out.println("4");
        model.addAttribute("success", true);
//        appUserService.saveUser(appUser);
        return "redirect:/main";
    }
}
