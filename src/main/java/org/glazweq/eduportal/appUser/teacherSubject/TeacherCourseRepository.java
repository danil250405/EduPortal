package org.glazweq.eduportal.appUser.teacherSubject;

import org.glazweq.eduportal.appUser.user.AppUser;
import org.glazweq.eduportal.education.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherCourseRepository extends JpaRepository<TeacherCourse, Long> {
    List<TeacherCourse> findByCourseId(Long courseId);
    boolean existsByTeacherAndCourse(AppUser teacher, Course course);
    void deleteByTeacherIdAndCourseId(Long teacherId, Long courseId);
}

