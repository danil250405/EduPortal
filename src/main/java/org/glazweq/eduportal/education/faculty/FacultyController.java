package org.glazweq.eduportal.education.faculty;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.education.specialty.Specialty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@AllArgsConstructor
public class FacultyController {
    FacultyService facultyService;
    // faculty
    @GetMapping("/faculties")
    public String showFacultyPage(Model model) {
        List<Faculty> faculties = facultyService.getAllFaculties();
        model.addAttribute("faculties", faculties);
        return "faculties-page";
    }


    @PostMapping("/faculties/add")
    public String addFaculty(@ModelAttribute("faculty-name") String name,
                             @ModelAttribute("faculty-abbreviation") String abbreviation,
                             RedirectAttributes redirectAttributes) {
        // Проверка на существование факультета с таким же названием
        if (facultyService.facultyExistsByName(name)) {
            redirectAttributes.addFlashAttribute("infoMessage", "Faculty with the same name already exists.");

            return "redirect:/faculties";
        }

        // Проверка на существование факультета с такой же аббревиатурой
        if (facultyService.facultyExistsByAbbreviation(abbreviation)) {
            redirectAttributes.addFlashAttribute("infoMessage", "Faculty with the same abbreviation already exists.");
            return "redirect:/faculties";
        }
        Faculty faculty = new Faculty();
        faculty.setName(name);
        faculty.setAbbreviation(abbreviation);
        facultyService.addFaculty(faculty);

        return "redirect:/faculties";
    }

    @PostMapping("/faculties/delete")
    public String deleteFaculty(@ModelAttribute("faculty-id") Long id,
                                @ModelAttribute("del-faculty-name") String facultyName,
                                RedirectAttributes redirectAttributes) {
        boolean isDeleted = facultyService.deleteFaculty(id);
        System.out.println("fac name " + facultyName);
        String infoMessage = isDeleted ? facultyName + " deleted successfully" : "You can't delete " + facultyName;
        redirectAttributes.addFlashAttribute("infoMessage", infoMessage);
        return "redirect:/faculties";
    }



    //    Specialties

}
