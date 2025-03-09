//package org.glazweq.eduportal.education.subject;
//
//import jakarta.persistence.EntityNotFoundException;
//import lombok.AllArgsConstructor;
//import org.glazweq.eduportal.education.folder.Folder;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@AllArgsConstructor
//public class SubjectService {
//    private final SubjectRepository subjectRepository;
//
//    public List<Subject> getAllSubjects() {
//        return subjectRepository.findAll();
//    }
//
//    public List<Subject> getSubjectsByFolder(Folder folder) {
//        return subjectRepository.findAllByFolder(folder);
//    }
//
//    public void addSubject(Subject subject) {
//        subjectRepository.save(subject);
//    }
//
//    public boolean deleteSubject(Long subjectId) {
//        if (subjectRepository.existsById(subjectId)) {
//            subjectRepository.deleteById(subjectId);
//            return true;
//        }
//        return false;
//    }
//
//    public Subject getSubjectById(Long id) {
//        return subjectRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Subject not found"));
//    }
//}
