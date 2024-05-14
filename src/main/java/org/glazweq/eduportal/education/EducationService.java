package org.glazweq.eduportal.education;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.glazweq.eduportal.education.entity.Faculty;
import org.glazweq.eduportal.education.entity.Specialty;
import org.glazweq.eduportal.education.repository.FacultyRepository;
import org.glazweq.eduportal.education.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EducationService {
    private final FacultyRepository facultyRepository;
    private final SpecialtyRepository specialtyRepository;

    public List<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public void addFaculty(Faculty faculty) {
        facultyRepository.save(faculty);
    }

    public boolean canDeleteFaculty(Long facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new EntityNotFoundException("Faculty not found"));
        return faculty.getSpecialties().isEmpty();
    }
    public boolean deleteFaculty(Long facultyId) {

        if (canDeleteFaculty(facultyId)) {

            facultyRepository.deleteById(facultyId);
            return true;
        } else {

            return false;
        }
    }
    public Faculty getFacultyByAbbreviation(String abbreviation){
        return facultyRepository.findByAbbreviation(abbreviation);
    }
    public  Faculty getFacultyById(Long id){
        return facultyRepository.findFacultyById(id);
    }

//    specialties
    public List<Specialty> getAllSpecialties() {
        return specialtyRepository.findAll();
    }
    public List<Specialty> getAllSpecialtiesByFaculty(Faculty faculty) {
        return specialtyRepository.findAllByFaculty(faculty);
    }
    public void addSpecialty(Specialty specialty) {
        specialtyRepository.save(specialty);
    }
    public boolean canDeleteSpecialty(Long specialtyId) {
        Specialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new EntityNotFoundException("Specialty not found"));
        return specialty.getSubjects().isEmpty();
    }

    public boolean deleteSpecialty(Long specialtyId) {
        if (canDeleteSpecialty(specialtyId)) {
            specialtyRepository.deleteById(specialtyId);
            return true;
        } else {
            return false;
        }
    }
}
