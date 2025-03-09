package org.glazweq.eduportal.education.folder;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.glazweq.eduportal.education.specialty.Specialty;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private String access;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Folder parentFolder;

    // Список вложенных папок
    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Folder> subFolders = new ArrayList<>();
}
