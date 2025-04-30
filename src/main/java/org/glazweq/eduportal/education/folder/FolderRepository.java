package org.glazweq.eduportal.education.folder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    // Проверка существования папки с таким же именем в указанной родительской папке
    boolean existsByName(String name);
    boolean existsFolderByParentFolderId(Long id);
    // Проверка существования папки с таким же именем в корневой директории
    boolean existsByNameAndParentFolderIsNull(String name);
    List<Folder> findByParentFolderIsNull();
    List<Folder> findByParentFolderId(Long parentFolder_id);

    Folder findFolderById(Long id);

    boolean existsByNameAndParentFolder(String name, Folder parentFolder);
}
