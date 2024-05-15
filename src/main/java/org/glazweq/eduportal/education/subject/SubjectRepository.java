package org.glazweq.eduportal.education.subject;

import org.glazweq.eduportal.education.specialty.Specialty;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findAllBySpecialty(Specialty specialty);
}
