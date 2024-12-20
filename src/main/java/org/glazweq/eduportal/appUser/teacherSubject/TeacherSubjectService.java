package org.glazweq.eduportal.appUser.teacherSubject;

import jakarta.transaction.Transactional;
import org.glazweq.eduportal.appUser.user.AppUser;
import org.glazweq.eduportal.appUser.user.AppUserRepository;
import org.glazweq.eduportal.education.subject.Subject;
import org.glazweq.eduportal.education.subject.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class TeacherSubjectService {
    @Autowired
    private TeacherSubjectRepository teacherSubjectRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private AppUserRepository appUserRepository;

    public void assignTeacherToSubject(Long teacherId, Long subjectId) {
        AppUser teacher = appUserRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherAssignmentException("Teacher not found"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new TeacherAssignmentException("Subject not found"));

        // Check if assignment already exists
        if (teacherSubjectRepository.existsByTeacherAndSubject(teacher, subject)) {
            throw new TeacherAssignmentException("This teacher is already assigned to the subject");
        }

        try {
            TeacherSubject teacherSubject = new TeacherSubject();
            teacherSubject.setTeacher(teacher);
            teacherSubject.setSubject(subject);
            teacherSubject.setAssignedAt(LocalDateTime.now());

            teacherSubjectRepository.save(teacherSubject);
        } catch (Exception e) {
            throw new TeacherAssignmentException("Failed to assign teacher to subject: " + e.getMessage());
        }
    }
    public void removeTeacherFromSubject(Long teacherId, Long subjectId) {
        teacherSubjectRepository.deleteByTeacherIdAndSubjectId(teacherId, subjectId);
    }
}