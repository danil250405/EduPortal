package org.glazweq.eduportal.appUser.teacherSubject;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.glazweq.eduportal.appUser.user.AppUser;
import org.glazweq.eduportal.education.subject.Subject;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class TeacherSubject {


    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private AppUser teacher;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;


}