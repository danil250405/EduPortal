package org.glazweq.eduportal.education.course;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.glazweq.eduportal.education.folder.Folder;
import org.glazweq.eduportal.education.subject.Course;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CourseService {
    CourseRepository courseRepository;
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getCoursesByFolder(Folder folder) {
        return courseRepository.findAllByFolder(folder);
    }

    public void addCourse(Course course) {
        courseRepository.save(course);
    }

    public boolean deleteCourse(Long courseId) {
        if (courseRepository.existsById(courseId)) {
            courseRepository.deleteById(courseId);
            return true;
        }
        return false;
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("course not found"));
    }
}
