package org.glazweq.eduportal.education.folder;

import org.glazweq.eduportal.appUser.user.AppUser;
import org.glazweq.eduportal.education.course.Course;
import org.glazweq.eduportal.education.course.CourseService;

import org.glazweq.eduportal.exeptions.DuplicateFolderNameException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/folders")
public class FolderController {
    @Autowired
    private FolderService folderService;
    @Autowired
    private CourseService courseService;

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
        List<Course> courses = courseService.getCoursesByFolder(folder);
        model.addAttribute("courses", courses);
        model.addAttribute("folder", folder);
        model.addAttribute("subfolders", subfolders);
        model.addAttribute("newFolder", new Folder());
        return "folder-detail";
    }
//TODO: removing folders
    // Добавление новой папки
    @PostMapping("/add")
    public String addFolder(@ModelAttribute Folder folder, @RequestParam(required = false) Long parentId, RedirectAttributes redirectAttributes) {
        try {
            AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            folderService.createFolder(folder.getName(), user, parentId);

        } catch (DuplicateFolderNameException e) {
            // Add error message for Thymeleaf template
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            System.out.println(e.getMessage());
            // Return to the form page instead of redirecting

        }
        if (parentId == null) {
            return "redirect:/folders";
        }
        else return "redirect:/folders/" + parentId;


    }

    // Удаление папки
    @PostMapping("/delete")
    public String deleteFolder(@RequestParam("folder-id") Long id, RedirectAttributes redirectAttributes) {
        Folder folder = folderService.getFolderById(id);

        redirectAttributes.addFlashAttribute("infoMessage", folderService.deleteFolder(id));
        if (folder.getParentFolder() == null) {
            return "redirect:/folders";
        }
        else return "redirect:/folders/" + folder.getParentFolder().getId();
    }
}

