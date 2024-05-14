package org.glazweq.eduportal.education;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.glazweq.eduportal.education.entity.Faculty;
import org.glazweq.eduportal.education.entity.Specialty;
import org.glazweq.eduportal.education.entity.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@AllArgsConstructor
public class EducationController {
    EducationService educationService;

    // faculty
    @GetMapping("/faculties")
    public String showFacultyPage(Model model) {
        List<Faculty> faculties = educationService.getAllFaculties();
        model.addAttribute("faculties", faculties);
        return "faculties-page";
    }


    @PostMapping("/faculties/add")
    public String addFaculty(@ModelAttribute("faculty-name") String name,
                             @ModelAttribute("faculty-abbreviation") String abbreviation) {
        Faculty faculty = new Faculty();
        faculty.setName(name);
        faculty.setAbbreviation(abbreviation);
        educationService.addFaculty(faculty);

        return "redirect:/faculties";
    }

    @PostMapping("/faculties/delete")
    public String deleteFaculty(@ModelAttribute("faculty-id") Long id,
                                @ModelAttribute("del-faculty-name") String facultyName,
                                RedirectAttributes redirectAttributes) {
        boolean isDeleted = educationService.deleteFaculty(id);
        System.out.println("fac name " + facultyName);
        String infoMessage = isDeleted ? "Faculty is " + facultyName + " deleted successfully" : "You can't delete " + facultyName;
        redirectAttributes.addFlashAttribute("infoMessage", infoMessage);
        return "redirect:/faculties";
    }



    //    Specialties
    @GetMapping("/faculties/{facultyAbbreviation}")
    public String redirectToFacultyPage(@PathVariable("facultyAbbreviation") String facultyAbbreviation,
                                        Model model) {

        Faculty faculty = educationService.getFacultyByAbbreviation(facultyAbbreviation);
        model.addAttribute("faculty", faculty);
        List<Specialty> specialties = educationService.getAllSpecialtiesByFaculty(faculty);
        model.addAttribute("specialties", specialties);

        return "specialty-page";

    }
    @PostMapping("/specialties/add")
    public String addSpecialty(@ModelAttribute("specialty-name") String name,
                               @ModelAttribute("specialty-abbreviation") String abbreviation,
                               @ModelAttribute("specialty-faculty-id") Long specialtyFacultyId) {
        Faculty faculty = educationService.getFacultyById(specialtyFacultyId);
        Specialty specialty = new Specialty();
        specialty.setName(name);
        specialty.setAbbreviation(abbreviation);
        specialty.setFaculty(faculty);
        educationService.addSpecialty(specialty);

        return "redirect:/faculties/" + faculty.getAbbreviation();
    }

    @PostMapping("/specialties/delete")
    public String deleteSpecialties(@RequestParam("specialty-id") Long id,
                                    @RequestParam("del-specialty-name") String specialtyName,
                                    @RequestParam("specialty-faculty-abbr") String specialtyFacultyAbbr,
                                    RedirectAttributes redirectAttributes) {
        System.out.println("11111");
        boolean isDeleted = educationService.deleteSpecialty(id);
        System.out.println("spec name " + specialtyName);
        String infoMessage = isDeleted ? "Specialty '" + specialtyName + "' deleted successfully" : "You can't delete " + specialtyName;
        redirectAttributes.addFlashAttribute("infoMessage", infoMessage);
        return "redirect:/faculties/" + specialtyFacultyAbbr;
    }

    //    Subjects
    @GetMapping("/faculties/{facultyAbbreviation}/{specialtyAbbreviation}")
    public String redirectToSpecialtyPage(@PathVariable("facultyAbbreviation") String facultyAbbreviation,
                                          @PathVariable("specialtyAbbreviation") String specialtyAbbreviation,
                                          Model model) {
        Specialty specialty =educationService.getSpecialtyByAbbreviation(specialtyAbbreviation);
//        Faculty faculty = educationService.getFacultyByAbbreviation(facultyAbbreviation);
        model.addAttribute("specialty", specialty);
        List<Subject> subjects = educationService.getAllSubjectsBySpecialty(specialty);
        model.addAttribute("subjects", subjects);

        return "subject-page";
    }
    @PostMapping("/subject/add")
    public String addSubject(@ModelAttribute("subject-name") String name,
                               @ModelAttribute("subject-abbreviation") String abbreviation,
                               @ModelAttribute("subject-specialty-id") Long subjectSpecialtyId,
                             @ModelAttribute("subject-semester") String subjectSemester,
                             @ModelAttribute("subject-type") String subjectType) {
        Specialty specialty = educationService.getSpecialtyById(subjectSpecialtyId);
        Subject subject = new Subject();
        subject.setName(name);
        subject.setAbbreviation(abbreviation);
        subject.setSpecialty(specialty);
        subject.setSemester(subjectSemester);
        subject.setType(subjectType);
        educationService.addSubject(subject);

        return "redirect:/faculties/" + specialty.getFaculty().getAbbreviation() + "/" + specialty.getAbbreviation();
    }

    @PostMapping("/subject/delete")
    public String deleteSubject(@RequestParam("subject-id") Long id,
                                    @RequestParam("del-subject-name") String subjectName,
                                    @RequestParam("del-subject-specialty-id") Long subjectSpecialtyId,
                                    RedirectAttributes redirectAttributes) {
        System.out.println("2222");
        Specialty specialty = educationService.getSpecialtyById(subjectSpecialtyId);
        boolean isDeleted = educationService.deleteSubject(id);

        String infoMessage = isDeleted ? "Subject '" + subjectName + "' deleted successfully" : "You can't delete " + subjectName;
        redirectAttributes.addFlashAttribute("infoMessage", infoMessage);
        return "redirect:/faculties/" + specialty.getFaculty().getAbbreviation() + "/" + specialty.getAbbreviation();
    }

}
