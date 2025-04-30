package org.glazweq.eduportal.education.folder;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.glazweq.eduportal.appUser.user.AppUser;


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
    @ManyToOne(fetch = FetchType.LAZY) // Consider using LAZY loading
    @JoinColumn(name = "owner_id", nullable = false) // Correct foreign key column name
    private AppUser owner;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Folder parentFolder;

    // Список вложенных папок
    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Folder> subFolders = new ArrayList<>();
// In Folder.java

    @Column(name = "is_link")
    private boolean isLink = false; // default to not being a link

    @Column(name = "link_url")
    private String linkUrl; // URL if this is a link, null otherwise
}
