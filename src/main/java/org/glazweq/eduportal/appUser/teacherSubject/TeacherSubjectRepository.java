package org.glazweq.eduportal.appUser.teacherSubject;

import org.glazweq.eduportal.appUser.user.AppUser;
import org.glazweq.eduportal.education.subject.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherSubjectRepository extends JpaRepository<TeacherSubject, Long> {
    List<TeacherSubject> findBySubjectId(Long subjectId);
    boolean existsByTeacherAndSubject(AppUser teacher, Subject subject);
    void deleteByTeacherIdAndSubjectId(Long teacherId, Long subjectId);
}