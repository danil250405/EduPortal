package org.glazweq.eduportal.registration;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.appUser.AppUser;
import org.glazweq.eduportal.appUser.AppUserRole;
import org.glazweq.eduportal.appUser.AppUserService;
import org.glazweq.eduportal.registration.token.ConfirmationToken;
import org.glazweq.eduportal.registration.token.ConfirmationTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {
    EmailValidator emailValidator;
    AppUserService appUserService;
    ConfirmationTokenService confirmationTokenService;
    public String register(AppUser appUser) {
        appUserService.signUpUser(appUser);
        return "";
    }
    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(
                confirmationToken.getAppUser().getEmail());
        return "confirmed";
    }

}
