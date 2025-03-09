package org.glazweq.eduportal.education.specialty;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.glazweq.eduportal.education.faculty.Faculty;
import org.glazweq.eduportal.education.faculty.FacultyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
@Controller
@AllArgsConstructor
public class SpecialtyController {
//    SpecialtyService specialtyService;
//    FacultyService facultyService;
//    @GetMapping("/specialtiesAll")
//    public String showSpecialtyPage(Model model) {
//        List<Specialty> specialties = specialtyService.getAllSpecialties();
//        System.out.println(specialties.get(0).getName());
//
//        model.addAttribute("specialties", specialties);
//        return "specialties-all";
//    }
//    @GetMapping("/faculties/{facultyAbbreviation}")
//    public String redirectToFacultyPage(@PathVariable("facultyAbbreviation") String facultyAbbreviation,
//                                        Model model
//                                        ) {
//
//        Faculty faculty = facultyService.getFacultyByAbbreviation(facultyAbbreviation);
//        model.addAttribute("faculty", faculty);
//        int itemsPerPage = 10;
//        List<Specialty> specialties = specialtyService.getAllSpecialtiesByFaculty(facultyService.getFacultyByAbbreviation(facultyAbbreviation));
//        model.addAttribute("specialties", specialties);
//        // Получаем общее количество для пагинации
//        long totalItems = specialtyService.countByFaculty(faculty);
//        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
//
//        // Добавление атрибутов в модель
//        model.addAttribute("specialties", specialties);
////        model.addAttribute("currentUrl", request.getRequestURI());
////        model.addAttribute("currentPage", page);
////        model.addAttribute("totalPages", totalPages);
////        model.addAttribute("itemsPerPage", itemsPerPage);
//        return "specialties-page";
//
//    }
//    @PostMapping("/specialties/add")
//    public String addSpecialty(@ModelAttribute("specialty-name") String name,
//                               @ModelAttribute("specialty-abbreviation") String abbreviation,
//                               @ModelAttribute("specialty-faculty-id") Long specialtyFacultyId,
//                               RedirectAttributes redirectAttributes) {
//        Faculty faculty = facultyService.getFacultyById(specialtyFacultyId);
//        if (specialtyService.specialtyNameExistsOnSameFaculty(name, specialtyFacultyId)) {
//            redirectAttributes.addFlashAttribute("infoMessage", "Specialty with the same name already exists on this faculty.");
//            return "redirect:/faculties/" + faculty.getAbbreviation();
//        }
//        if (specialtyService.specialtyAbbreviationExistsOnSameFaculty(abbreviation, specialtyFacultyId)) {
//            redirectAttributes.addFlashAttribute("infoMessage", "Specialty with the same abbreviation already exists on this faculty.");
//            return "redirect:/faculties/" + faculty.getAbbreviation();
//        }
//
//        Specialty specialty = new Specialty();
//        specialty.setName(name);
//        specialty.setAbbreviation(abbreviation);
//        specialty.setFaculty(faculty);
//        specialtyService.addSpecialty(specialty);
//
//        return "redirect:/faculties/" + faculty.getAbbreviation();
//    }
//
//    @PostMapping("/specialties/delete")
//    public String deleteSpecialties(@RequestParam("specialty-id") Long id,
//                                    @RequestParam("del-specialty-name") String specialtyName,
//                                    @RequestParam("specialty-faculty-abbr") String specialtyFacultyAbbr,
//                                    RedirectAttributes redirectAttributes) {
//        System.out.println("11111");
//        boolean isDeleted = specialtyService.deleteSpecialty(id);
//        System.out.println("spec name " + specialtyName);
//        String infoMessage = isDeleted ? "Specialty '" + specialtyName + "' deleted successfully" : "You can't delete " + specialtyName;
//        redirectAttributes.addFlashAttribute("infoMessage", infoMessage);
//        return "redirect:/faculties/" + specialtyFacultyAbbr;
//    }
//    @PostMapping("/specialtiesAll/delete")
//    public String deleteSpecialtiesFromAllPage(@RequestParam("specialty-id") Long id,
//                                    @RequestParam("del-specialty-name") String specialtyName,
//                                    RedirectAttributes redirectAttributes) {
//        System.out.println("11111");
//        boolean isDeleted = specialtyService.deleteSpecialty(id);
//        System.out.println("spec name " + specialtyName);
//        String infoMessage = isDeleted ? "Specialty '" + specialtyName + "' deleted successfully" : "You can't delete " + specialtyName;
//        redirectAttributes.addFlashAttribute("infoMessage", infoMessage);
//        return "redirect:/specialtiesAll";
//    }
}
