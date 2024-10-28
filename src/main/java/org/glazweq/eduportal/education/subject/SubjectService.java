package org.glazweq.eduportal.education.subject;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.glazweq.eduportal.education.specialty.Specialty;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;



//    Subjects
    public List<Subject> getAllSubjects() {
    return subjectRepository.findAll();
}
    public List<Subject> getAllSubjectsBySpecialty(Specialty specialty) {
        return subjectRepository.findAllBySpecialty(specialty);
    }

    public void addSubject(Subject subject) {
        subjectRepository.save(subject);
    }



    //    TODO: make entity with subject information and this method make better
    public boolean canDeleteSubject(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("subject not found"));
        return true;
    }

    public boolean deleteSubject(Long subjectId) {

        if (canDeleteSubject(subjectId)) {
            subjectRepository.deleteById(subjectId);
            return true;
        } else {
            return false;
        }



//        if (canDeleteSpecialty(specialtyId)) {
//            specialtyRepository.deleteById(specialtyId);
//            return true;
//        } else {
//            return false;
//        }
    }

    List<String> getSortedSemesters(List<Subject> subjects){
    Set<String> semestersSet = new HashSet<>();
        for (Subject subject : subjects) {
        semestersSet.add(subject.getSemester());
    }
    List<String> sortedSemesters = semestersSet.stream()
            .sorted(Comparator.comparing(s -> extractSemesterNumber(s)))
            .collect(Collectors.toList());
        return  sortedSemesters;
    }
    // Вспомогательный метод для извлечения номера семестра из строки
    private int extractSemesterNumber(String semester) {
        String[] parts = semester.split(" ");
        if (parts.length == 2 && parts[0].equalsIgnoreCase("semester")) {
            try {
                return Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                // Обработка исключения, если номер семестра не является числом
            }
        }
        return 0; // Или другое значение по умолчанию
    }
    public Subject getSubjectBySubjectAbbrAndSpecialtyAbbr(String subAbbr, String specAbbr){
        return subjectRepository.getSubjectByAbbreviationAndSpecialtyAbbreviation(subAbbr,specAbbr);
    }
    public Subject getSubjectById(Long id){
        return subjectRepository.getSubjectById(id);
    }
}
