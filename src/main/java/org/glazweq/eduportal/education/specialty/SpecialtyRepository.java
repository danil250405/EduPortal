package org.glazweq.eduportal.education.specialty;

import org.glazweq.eduportal.education.faculty.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    List<Specialty> findAllByFaculty(Faculty faculty);
    Specialty findSpecialtyByAbbreviation(String abbreviation);
    Specialty findSpecialtyById(Long id);

    boolean existsByNameAndFacultyId(String name, Long facultyId);
    boolean existsByAbbreviationAndFacultyId(String abbr, Long facultyId);
}
