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
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        // Check if assignment already exists
        if (teacherSubjectRepository.existsByTeacherAndSubject(teacher, subject)) {
            throw new RuntimeException("Teacher is already assigned to this subject");
        }

        TeacherSubject teacherSubject = new TeacherSubject();
        teacherSubject.setTeacher(teacher);
        teacherSubject.setSubject(subject);
        teacherSubject.setAssignedAt(LocalDateTime.now());

        teacherSubjectRepository.save(teacherSubject);
    }

    public void removeTeacherFromSubject(Long teacherId, Long subjectId) {
        teacherSubjectRepository.deleteByTeacherIdAndSubjectId(teacherId, subjectId);
    }
}