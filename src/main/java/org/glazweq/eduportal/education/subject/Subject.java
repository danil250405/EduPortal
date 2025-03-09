//package org.glazweq.eduportal.education.subject;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//import org.glazweq.eduportal.appUser.teacherSubject.TeacherCourse;
//import org.glazweq.eduportal.storage.file_metadata.FileMetadata;
//import org.glazweq.eduportal.appUser.teacherSubject.TeacherCourse;
//import org.glazweq.eduportal.education.folder.Folder;
//
//import java.util.List;
//
//@Getter
//@Setter
//@Entity
//public class Subject {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String name;
//
//    @Column(nullable = false)
//    private String type;  // Можно оставить, если нужно
//
//    @ManyToOne
//    @JoinColumn(name = "folder_id", nullable = false)  // Привязка к папке
//    private Folder folder;
//
//    @OneToMany(mappedBy = "subject")
//    private List<FileMetadata> filesMetadata;
//
//    @OneToMany(mappedBy = "subject")
//    private List<TeacherCourse> teacherCourses;
//}
