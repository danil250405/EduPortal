package org.glazweq.eduportal.education.course;

import org.glazweq.eduportal.education.folder.Folder;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByFolder(Folder folder);

    Course getCourseById(Long id);
}
