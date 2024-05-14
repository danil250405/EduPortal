package org.glazweq.eduportal.education.repository;

import org.glazweq.eduportal.education.entity.Faculty;
import org.glazweq.eduportal.education.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    List<Specialty> findAllByFaculty(Faculty faculty);
}
