package org.glazweq.eduportal.controller;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.appUser.AppUser;
import org.glazweq.eduportal.appUser.AppUserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class MainController {
    AppUserService appUserService;
    @GetMapping("/main")
    public String showMainPage(Model model){

    // Получить объект Authentication из SecurityContext
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // Получить объект UserDetails из объекта Authentication
        String currentPrincipalName = authentication.getName();
        AppUser authUser = appUserService.getUserByEmail(currentPrincipalName);

    // Добавить объект UserDetails в модель
//    model.addAttribute("currentUser", principal);
        if (authUser != null) {
            System.out.println("Hello " + authUser.getEmail());
        }
        else System.out.println("You not auth user");
        return "main-page";
    }
}
