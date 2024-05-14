package org.glazweq.eduportal.registration.email;

public interface EmailSender {
    void send(String to, String email);
}
