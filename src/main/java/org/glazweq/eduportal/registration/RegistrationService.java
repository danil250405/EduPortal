package org.glazweq.eduportal.registration;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.user.AppUser;
import org.glazweq.eduportal.user.AppUserRole;
import org.glazweq.eduportal.user.AppUserService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {
    EmailValidator emailValidator;
    AppUserService appUserService;
    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) throw new IllegalStateException("email is not valid");
        return appUserService.signUpUser(
                new AppUser(request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getPassword(),
                            AppUserRole.USER)
        );
    }
}
