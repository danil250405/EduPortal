package org.glazweq.eduportal.education.specialty;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.glazweq.eduportal.education.faculty.Faculty;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class SpecialtyService {
//
//    private final SpecialtyRepository specialtyRepository;
//
//    public List<Specialty> getSpecialtiesByFacultyRange(Faculty faculty, int page, int size) {
//        int offset = page * size;  // Вычисляем смещение
//        return specialtyRepository.findSpecialtiesByFacultyWithRange(
//                faculty.getId(),
//                offset,
//                size
//        );
//    }
//
//
//    //    specialties
//    public List<Specialty> getAllSpecialties() {
//        return specialtyRepository.findAll();
//    }
//    public List<Specialty> getAllSpecialtiesByFaculty(Faculty faculty) {
//        return specialtyRepository.findAllByFaculty(faculty);
//    }
//
//    public void addSpecialty(Specialty specialty) {
//        specialtyRepository.save(specialty);
//    }
//    public boolean canDeleteSpecialty(Long specialtyId) {
//        Specialty specialty = specialtyRepository.findById(specialtyId)
//                .orElseThrow(() -> new EntityNotFoundException("Specialty not found"));
//        return specialty.getSubjects().isEmpty();
//    }
//
//    public boolean deleteSpecialty(Long specialtyId) {
//        if (canDeleteSpecialty(specialtyId)) {
//            specialtyRepository.deleteById(specialtyId);
//            return true;
//        } else {
//            return false;
//        }
//    }
//    public Specialty getSpecialtyByAbbreviation(String abbreviation){
//        return specialtyRepository.findSpecialtyByAbbreviation(abbreviation);
//    }
//    public Specialty getSpecialtyById(Long id){
//        return specialtyRepository.findSpecialtyById(id);
//    }
//
//    public boolean specialtyAbbreviationExistsOnSameFaculty(String abbreviation, Long facultyId) {
//        return specialtyRepository.existsByAbbreviationAndFacultyId(abbreviation, facultyId);
//    }
//    public boolean specialtyNameExistsOnSameFaculty(String name, Long facultyId) {
//        return specialtyRepository.existsByNameAndFacultyId(name, facultyId);
//    }
//
//    public long countByFaculty(Faculty faculty) {
//        return specialtyRepository.countByFaculty(faculty);
//    }
}
