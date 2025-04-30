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

    @GetMapping("/{folderId}")
    public String viewFolder(@PathVariable Long folderId, Model model, RedirectAttributes redirectAttributes) {

        Folder folder = folderService.getFolderById(folderId);

        // Handle case where folder doesn't exist
        if (folder == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Folder with ID " + folderId + " not found.");
            // Determine where to redirect if folder not found
            // Maybe redirect to the root folder list?
            return "redirect:/folders"; // Adjust as needed
        }

        // --- Check if it's a link ---
        if (folder.isLink()) {
            String redirectUrl = folder.getLinkUrl();
            // Basic validation: Ensure URL is not empty
            if (redirectUrl != null && !redirectUrl.isBlank()) {
                // Prepend http:// if it's likely an external link without a protocol
                if (!redirectUrl.toLowerCase().startsWith("http://") &&
                        !redirectUrl.toLowerCase().startsWith("https://") &&
                        !redirectUrl.startsWith("/")) {
                    redirectUrl = "http://" + redirectUrl;
                }
                return "redirect:" + redirectUrl; // Perform the redirect
            } else {
                // Handle invalid/empty link URL case
                redirectAttributes.addFlashAttribute("errorMessage", "The link URL for folder '" + folder.getName() + "' is invalid or missing.");
                // Redirect to parent folder if possible, otherwise root
                Long parentId = (folder.getParentFolder() != null) ? folder.getParentFolder().getId() : null;
                String redirectPath = (parentId == null) ? "/folders" : "/folders/" + parentId;
                return "redirect:" + redirectPath;
            }
        }

        // --- If it's NOT a link, proceed to show the folder details page ---
        List<Folder> subfolders = folderService.getSubfolders(folderId); // Assuming this method exists
        List<Course> courses = courseService.getCoursesByFolder(folder); // Assuming this method exists

        model.addAttribute("courses", courses);
        model.addAttribute("folder", folder);
        model.addAttribute("subfolders", subfolders);
        // Add a new Folder object for the add form if needed, but maybe handle this differently
        // model.addAttribute("newFolder", new Folder()); // Consider if this is still needed here

        return "folder-detail"; // Return the name of your folder details template
    }
//TODO: removing folders
    // Добавление новой папки
    @PostMapping("/add")

    public String addFolder(@RequestParam String name, // Получаем имя папки
                            @RequestParam(required = false) Long parentId,
                            @RequestParam(required = false, defaultValue = "false") boolean isLink, // Получаем значение чекбокса
                            @RequestParam(required = false) String linkUrl, // Получаем URL, если это ссылка
                            RedirectAttributes redirectAttributes) {

        // Валидация: если это ссылка, URL не должен быть пустым
        if (isLink && (linkUrl == null || linkUrl.isBlank())) {
            redirectAttributes.addFlashAttribute("errorMessage", "URL cannot be empty when creating a link folder.");
            // Определяем, куда перенаправить в случае ошибки
            String redirectPath = (parentId == null) ? "/folders" : "/folders/" + parentId;
            return "redirect:" + redirectPath;
        }

        try {
            AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // Вызываем обновленный метод сервиса, передавая isLink и linkUrl
            folderService.createFolder(name, user, parentId, isLink, linkUrl);
            redirectAttributes.addFlashAttribute("infoMessage", "Folder '" + name + "' created successfully.");

        } catch (DuplicateFolderNameException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            System.out.println(e.getMessage()); // Логирование ошибки
        } catch (Exception e) { // Общий обработчик ошибок
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while creating the folder.");
            // Рекомендуется логировать e.printStackTrace();
        }

        // Перенаправляем на страницу родительской папки или на главную страницу папок
        if (parentId == null) {
            return "redirect:/folders";
        } else {
            return "redirect:/folders/" + parentId;
        }
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

