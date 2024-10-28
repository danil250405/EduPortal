package org.glazweq.eduportal.education.specialty;

import org.glazweq.eduportal.education.faculty.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    List<Specialty> findAllByFaculty(Faculty faculty);
    Specialty findSpecialtyByAbbreviation(String abbreviation);
    Specialty findSpecialtyById(Long id);
    boolean existsByNameAndFacultyId(String name, Long facultyId);
    boolean existsByAbbreviationAndFacultyId(String abbr, Long facultyId);
    Long countByFaculty(Faculty faculty);
    @Query(
            value = "SELECT * FROM specialty s WHERE s.faculty_id = :facultyId LIMIT :limit OFFSET :offset",
            nativeQuery = true
    )
    List<Specialty> findSpecialtiesByFacultyWithRange(
            @Param("facultyId") Long facultyId,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

}
