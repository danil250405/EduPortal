package org.glazweq.eduportal.education.course;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.glazweq.eduportal.appUser.teacherSubject.TeacherCourse;
import org.glazweq.eduportal.appUser.teacherSubject.TeacherCourseRepository;
import org.glazweq.eduportal.education.folder.Folder;

import org.glazweq.eduportal.storage.file_metadata.FileMetadataRepository;
import org.glazweq.eduportal.storage.file_metadata.FileMetadataService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CourseService {
    CourseRepository courseRepository;
    FileMetadataRepository fileMetadataRepository;
    TeacherCourseRepository teacherCourseRepository;
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getCoursesByFolder(Folder folder) {
        return courseRepository.findAllByFolder(folder);
    }

    public void addCourse(Course course) {
        courseRepository.save(course);
    }
    public boolean hasFiles(Long courseId) {
        return fileMetadataRepository.countByCourseId(courseId) > 0;
    }
    public boolean deleteCourse(Long id) {
        Course course = courseRepository.findById(id).orElse(null);
        if (course == null) {
            return false; // Course doesn't exist, so can't delete
        }
        if (hasFiles(id)) {
            return false;  //Prevent Deletion with Files
        }
        // Remove all teacher associations before deleting the course
        List<TeacherCourse> teacherCourses = teacherCourseRepository.findByCourseId(id);
        teacherCourseRepository.deleteAll(teacherCourses); // Delete in bulk
        courseRepository.delete(course);
        return true; // Deletion was successful
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("course not found"));
    }
    public List<Course> getCourseByFolder(Folder folder) {
        return courseRepository.findAllByFolder(folder);
    }
    public void updateCourse(Long id, Course updatedCourse) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setName(updatedCourse.getName());
        course.setDescription(updatedCourse.getDescription());

        courseRepository.save(course);
    }
}
