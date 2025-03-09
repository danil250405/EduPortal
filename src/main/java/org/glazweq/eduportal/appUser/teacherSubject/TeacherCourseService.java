package org.glazweq.eduportal.appUser.teacherSubject;

import jakarta.transaction.Transactional;
import org.glazweq.eduportal.appUser.user.AppUser;
import org.glazweq.eduportal.appUser.user.AppUserRepository;
import org.glazweq.eduportal.education.course.CourseRepository;
import org.glazweq.eduportal.education.subject.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class TeacherCourseService {
    @Autowired
    private TeacherCourseRepository teacherCourseRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private CourseRepository courseRepository;

    public void assignTeacherToSubject(Long teacherId, Long courseId) {
        AppUser teacher = appUserRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherAssignmentException("Teacher not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new TeacherAssignmentException("Subject not found"));

        // Check if assignment already exists
        if (teacherCourseRepository.existsByTeacherAndCourse(teacher, course)) {
            throw new TeacherAssignmentException("This teacher is already assigned to the subject");
        }

        try {
            TeacherCourse teacherCourse = new TeacherCourse();
            teacherCourse.setTeacher(teacher);
            teacherCourse.setCourse(course);
            teacherCourse.setAssignedAt(LocalDateTime.now());

            teacherCourseRepository.save(teacherCourse);
        } catch (Exception e) {
            throw new TeacherAssignmentException("Failed to assign teacher to course: " + e.getMessage());
        }
    }
    public void removeTeacherFromSubject(Long teacherId, Long subjectId) {
        teacherCourseRepository.deleteByTeacherIdAndCourseId(teacherId, subjectId);
    }
}