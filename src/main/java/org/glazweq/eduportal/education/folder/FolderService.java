package org.glazweq.eduportal.education.folder;

import org.glazweq.eduportal.exeptions.DuplicateFolderNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class FolderService {
    @Autowired
    private FolderRepository folderRepository;


    // Получить корневые папки
    public List<Folder> getRootFolders() {
        return folderRepository.findByParentFolderIsNull();
    }

    // Получить все подкатегории (папки внутри папки)
    public List<Folder> getSubfolders(Long parentId) {
        return folderRepository.findByParentFolderId(parentId);
    }

    // Получить папку по ID
    public Folder getFolderById(Long id) {
        System.out.println("11111111111111111 " + id);
        return folderRepository.findFolderById(id);
    }

    // Создать папку
    public void createFolder(String name, String access, Long parentId) {

        boolean exists = folderRepository.existsByName(name);
        if (exists) {
            throw new DuplicateFolderNameException("A folder with the '" + name + "' already exists");
        }
        Folder folder = new Folder();
        folder.setName(name);
        folder.setAccess(access);

        if (parentId != null) {
            Folder parentFolder = folderRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Родительская папка не найдена"));
            folder.setParentFolder(parentFolder);
        }

        folderRepository.save(folder);
    }

    // Удалить папку
    public String deleteFolder(Long id) {
        if (folderRepository.existsFolderByParentFolderId(id)) {
            return "Unable to delete folder. Please delete all contents of the folder first. ";
        }
        else{
            folderRepository.deleteById(id);
            return  "Folder is deleted successfully";
        }
    }
}
