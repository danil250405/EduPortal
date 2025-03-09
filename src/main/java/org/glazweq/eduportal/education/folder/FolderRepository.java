package org.glazweq.eduportal.education.folder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByParentFolderIsNull();
    List<Folder> findByParentFolderId(Long parentFolder_id);
}
