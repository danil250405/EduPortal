package org.glazweq.eduportal.education.specialty;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.education.faculty.Faculty;
import org.glazweq.eduportal.education.faculty.FacultyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
@Controller
@AllArgsConstructor
public class SpecialtyController {
    SpecialtyService specialtyService;
    FacultyService facultyService;
    @GetMapping("/faculties/{facultyAbbreviation}")
    public String redirectToFacultyPage(@PathVariable("facultyAbbreviation") String facultyAbbreviation,
                                        Model model) {

        Faculty faculty = facultyService.getFacultyByAbbreviation(facultyAbbreviation);
        model.addAttribute("faculty", faculty);
        List<Specialty> specialties = specialtyService.getAllSpecialtiesByFaculty(faculty);
        model.addAttribute("specialties", specialties);

        return "specialties-page";

    }
    @PostMapping("/specialties/add")
    public String addSpecialty(@ModelAttribute("specialty-name") String name,
                               @ModelAttribute("specialty-abbreviation") String abbreviation,
                               @ModelAttribute("specialty-faculty-id") Long specialtyFacultyId,
                               RedirectAttributes redirectAttributes) {
        Faculty faculty = facultyService.getFacultyById(specialtyFacultyId);
        if (specialtyService.specialtyNameExistsOnSameFaculty(name, specialtyFacultyId)) {
            redirectAttributes.addFlashAttribute("infoMessage", "Specialty with the same name already exists on this faculty.");
            return "redirect:/faculties/" + faculty.getAbbreviation();
        }
        if (specialtyService.specialtyAbbreviationExistsOnSameFaculty(abbreviation, specialtyFacultyId)) {
            redirectAttributes.addFlashAttribute("infoMessage", "Specialty with the same abbreviation already exists on this faculty.");
            return "redirect:/faculties/" + faculty.getAbbreviation();
        }

        Specialty specialty = new Specialty();
        specialty.setName(name);
        specialty.setAbbreviation(abbreviation);
        specialty.setFaculty(faculty);
        specialtyService.addSpecialty(specialty);

        return "redirect:/faculties/" + faculty.getAbbreviation();
    }

    @PostMapping("/specialties/delete")
    public String deleteSpecialties(@RequestParam("specialty-id") Long id,
                                    @RequestParam("del-specialty-name") String specialtyName,
                                    @RequestParam("specialty-faculty-abbr") String specialtyFacultyAbbr,
                                    RedirectAttributes redirectAttributes) {
        System.out.println("11111");
        boolean isDeleted = specialtyService.deleteSpecialty(id);
        System.out.println("spec name " + specialtyName);
        String infoMessage = isDeleted ? "Specialty '" + specialtyName + "' deleted successfully" : "You can't delete " + specialtyName;
        redirectAttributes.addFlashAttribute("infoMessage", infoMessage);
        return "redirect:/faculties/" + specialtyFacultyAbbr;
    }
}
