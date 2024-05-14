package org.glazweq.eduportal.education.repository;

import org.glazweq.eduportal.education.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Faculty findByAbbreviation(String abbreviation);
    Faculty findFacultyById(Long id);
}
