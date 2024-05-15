package org.glazweq.eduportal.education.faculty;

import org.glazweq.eduportal.education.faculty.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Faculty findByAbbreviation(String abbreviation);
    Faculty findFacultyById(Long id);
    boolean existsByName(String name);
    boolean existsByAbbreviation(String abbreviation);
}
