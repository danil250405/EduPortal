package org.glazweq.eduportal.education;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.glazweq.eduportal.education.entity.Faculty;
import org.glazweq.eduportal.education.repository.FacultyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EducationService {
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
}
