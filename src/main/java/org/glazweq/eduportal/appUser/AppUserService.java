package org.glazweq.eduportal.appUser;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.registration.token.ConfirmationToken;
import org.glazweq.eduportal.registration.token.ConfirmationTokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final static String USER_NOT_FOUND_MSG = "user with email %s not found";

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("in load user");
        return appUserRepository.findByEmail(email).
                orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public AppUser getUserByEmail(String email) {
        return appUserRepository.findAppUserByEmail(email);
    }

    //    check if everything is good with register fields
    public String getMsgAboutEmail(String email) {
        AppUser userByEmail = getUserByEmail(email);
        if (userByEmail != null) {
            if (userByEmail.getUsername() != null && !userByEmail.getUsername().isEmpty()) {
                return "This email is already taken";
            }
        }
        if (!email.endsWith("@stuba.sk")) {
            return "Email must end with @stuba.sk.";
        }

        return "success";
    }

    public String getMsgAboutFirstName(String firstName) {
        if (firstName.length() > 20) {
            return "First name must be up to 20 characters long";
        }
        if (!firstName.matches("^[a-zA-Z\\s']*$")) {
            return "First name must contain only letters, spaces, and apostrophes";
        }
        return "success";
    }

    public String getMsgAboutLastName(String lastName) {
        if (lastName.length() > 20) {
            return "Last name must be up to 20 characters long";
        }
        if (!lastName.matches("^[a-zA-Z\\s']*$")) {
            return "Last name must contain only letters, spaces, and apostrophes";
        }
        return "success";
    }

    public String getMsgAboutPassword(String password) {
        // Регулярное выражение для проверки пароля

        // Проверка требований по отдельности
        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one digit";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter";
        }


        return "success";
    }
//    ------------------------------------------------------------

    public String signUpUser(AppUser appUser) {
        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());

        appUser.setPassword(encodedPassword);

        appUserRepository.save(appUser);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
//        TODO: send email
        System.out.println(token);
        return token;
    }


    private LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}
