package org.glazweq.eduportal.education.faculty;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.glazweq.eduportal.education.specialty.Specialty;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class FacultyService {
    private final FacultyRepository facultyRepository;
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
    public boolean facultyExistsByName(String name) {
        return facultyRepository.existsByName(name);
    }

    public boolean facultyExistsByAbbreviation(String abbreviation) {
        return facultyRepository.existsByAbbreviation(abbreviation);
    }


}
