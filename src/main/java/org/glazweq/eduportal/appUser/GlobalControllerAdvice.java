package org.glazweq.eduportal.appUser;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private AppUserService appUserService;

    @ModelAttribute("currentUser")
    public AppUser getCurrentUser(Principal principal) {
        System.out.println("in global controller");
        if (principal != null) {
            System.out.println(principal.getName());
            AppUser appUser = appUserService.getUserByEmail(principal.getName());
            System.out.println(appUser.getAppUserRole()+"------------");
            return appUser;
        }
        return null;
    }
}