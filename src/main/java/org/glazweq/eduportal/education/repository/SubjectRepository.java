package org.glazweq.eduportal.education.repository;

import org.glazweq.eduportal.education.entity.Specialty;
import org.glazweq.eduportal.education.entity.Subject;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findAllBySpecialty(Specialty specialty);
}
