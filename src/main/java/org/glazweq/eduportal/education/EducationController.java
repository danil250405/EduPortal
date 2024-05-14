package org.glazweq.eduportal.education;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.glazweq.eduportal.education.entity.Faculty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class EducationController {
    EducationService educationService;

    @GetMapping("/faculties")
    public String showFacultyPage(Model model){
        List<Faculty> faculties = educationService.getAllFaculties();
        model.addAttribute("faculties", faculties);
        return "faculties-page";
    }
    @GetMapping("/faculties/add")
    public String showAddFacultyForm(Model model) {
        model.addAttribute("faculty", new Faculty());
        return "add-faculty";
    }

    @PostMapping("/faculties/add")
    public String addFaculty(@ModelAttribute("faculty-name") String name) {
        Faculty faculty = new Faculty();
        faculty.setName(name);
        educationService.addFaculty(faculty);

        return "redirect:/faculties";
    }
    @PostMapping("/faculties/delete")
    public String deleteFaculty(@ModelAttribute("faculty-id") Long id, Model model,
                                @ModelAttribute("del-faculty-name") String facultyName){
        if (educationService.deleteFaculty(id)){
            model.addAttribute("infoMessage", "faculty " + facultyName +" is delete successful");
        }
        else  model.addAttribute("infoMessage", "you can't delete " + facultyName + " faculty");
        List<Faculty> faculties = educationService.getAllFaculties();
        model.addAttribute("faculties", faculties);
        return "faculties-page";
    }
}
