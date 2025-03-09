//package org.glazweq.eduportal.education.subject;
//
//import org.glazweq.eduportal.education.folder.Folder;
//import org.glazweq.eduportal.education.specialty.Specialty;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface SubjectRepository extends JpaRepository<Subject, Long> {
//    List<Subject> findAllByFolder(Folder folder);
//    Subject getSubjectByAbbreviationAndSpecialtyAbbreviation(String subAbbr,String specAbbr);
//    Subject getSubjectById(Long id);
//}
