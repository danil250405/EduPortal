package org.glazweq.eduportal.education.folder;

import jakarta.transaction.Transactional;
import org.glazweq.eduportal.appUser.user.AppUser;
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
    @Transactional // Рекомендуется для операций сохранения/изменения
    public Folder createFolder(String name, AppUser owner, Long parentId, boolean isLink, String linkUrl) throws DuplicateFolderNameException {

        Folder parentFolder = null;
        if (parentId != null) {
            parentFolder = folderRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent folder not found with ID: " + parentId));
        }

        // Проверка на дубликат имени в той же родительской папке
        if (folderRepository.existsByNameAndParentFolder(name, parentFolder)) {
            throw new DuplicateFolderNameException("A folder with the name '" + name + "' already exists in this location.");
        }

        Folder folder = new Folder();
        folder.setName(name);
        folder.setOwner(owner);
        folder.setParentFolder(parentFolder);
        folder.setLink(isLink); // Устанавливаем флаг ссылки
        folder.setLinkUrl(linkUrl); // Устанавливаем URL ссылки

        return folderRepository.save(folder);
    }

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
