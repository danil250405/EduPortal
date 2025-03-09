package org.glazweq.eduportal.education.folder;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/folders")
public class FolderController {
    @Autowired
    private FolderService folderService;

    // Просмотр всех корневых папок
    @GetMapping
    public String listFolders(Model model) {
        List<Folder> rootFolders = folderService.getRootFolders();
        model.addAttribute("folders", rootFolders);
        model.addAttribute("newFolder", new Folder()); // Форма для добавления папки
        return "folders";
    }

    // Просмотр содержимого папки
    @GetMapping("/{folderId}")
    public String viewFolder(@PathVariable Long folderId, Model model) {
        System.out.printf("wwwwwww");
        Folder folder = folderService.getFolderById(folderId);
        List<Folder> subfolders = folderService.getSubfolders(folderId);
        model.addAttribute("folder", folder);
        model.addAttribute("subfolders", subfolders);
        model.addAttribute("newFolder", new Folder());
        return "folder-detail";
    }

    // Добавление новой папки
    @PostMapping("/add")
    public String addFolder(@ModelAttribute Folder folder, @RequestParam(required = false) Long parentId) {
        folderService.createFolder(folder.getName(), folder.getAccess(), parentId);
        System.out.printf("chuj");
        return "redirect:/folders/" + folder.getId();
    }

    // Удаление папки
    @PostMapping("/delete/{id}")
    public String deleteFolder(@PathVariable Long id) {
        folderService.deleteFolder(id);
        return "redirect:/folders";
    }
}

