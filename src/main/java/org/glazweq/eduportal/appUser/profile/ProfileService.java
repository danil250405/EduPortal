package org.glazweq.eduportal.appUser.profile;

import org.glazweq.eduportal.education.course.Course;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class ProfileService {

    public Object getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getPrincipal(); // Возвращает объект пользователя
        }
        return "No authenticated user";
    }

}
