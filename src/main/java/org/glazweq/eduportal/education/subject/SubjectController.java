//package org.glazweq.eduportal.education.subject;
//
//import lombok.AllArgsConstructor;
//import org.glazweq.eduportal.education.folder.Folder;
//import org.glazweq.eduportal.education.folder.FolderService;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.util.List;
//
//@Controller
//@AllArgsConstructor
//public class SubjectController {
//    private final SubjectService subjectService;
//    private final FolderService folderService;
//
//    @GetMapping("/subjectsAll")
//    public String showSubjectsPage(Model model) {
//        List<Subject> subjects = subjectService.getAllSubjects();
//        model.addAttribute("subjects", subjects);
//        return "subjects-all";
//    }
//
//    @GetMapping("/folders/{folderId}/subjects")
//    public String showSubjectsInFolder(@PathVariable Long folderId, Model model) {
//        Folder folder = folderService.getFolderById(folderId);
//        List<Subject> subjects = subjectService.getSubjectsByFolder(folder);
//
//        model.addAttribute("folder", folder);
//        model.addAttribute("subjects", subjects);
//        return "subjects-page";
//    }
//
//    @PostMapping("/subject/add")
//    public String addSubject(@RequestParam String name,
//                             @RequestParam String abbreviation,
//                             @RequestParam Long folderId,
//                             @RequestParam String subjectType) {
//        Folder folder = folderService.getFolderById(folderId);
//        Subject subject = new Subject();
//        subject.setName(name);
//        subject.setAbbreviation(abbreviation);
//        subject.setFolder(folder);
//        subject.setType(subjectType);
//
//        subjectService.addSubject(subject);
//        return "redirect:/folders/" + folderId + "/subjects";
//    }
//
//    @PostMapping("/subject/delete")
//    public String deleteSubject(@RequestParam Long subjectId,
//                                @RequestParam Long folderId,
//                                RedirectAttributes redirectAttributes) {
//        boolean isDeleted = subjectService.deleteSubject(subjectId);
//        String infoMessage = isDeleted ? "Subject deleted successfully" : "You can't delete this subject";
//        redirectAttributes.addFlashAttribute("infoMessage", infoMessage);
//        return "redirect:/folders/" + folderId + "/subjects";
//    }
//}
