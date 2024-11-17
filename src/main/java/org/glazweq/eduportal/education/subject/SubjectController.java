package org.glazweq.eduportal.education.subject;

import lombok.AllArgsConstructor;
import org.glazweq.eduportal.education.specialty.Specialty;
import org.glazweq.eduportal.education.specialty.SpecialtyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class SubjectController {
    SubjectService subjectService;
    SpecialtyService specialtyService;


    //    Subjects
    @GetMapping("/subjectsAll")
    public String showSpecialtyPage(Model model) {
        List<Subject> subjects = subjectService.getAllSubjects();

        model.addAttribute("subjects", subjects);
        return "subjects-all";
    }
    @GetMapping("/faculties/{facultyAbbr}/{specialtyAbbr}")
    public String redirectToSpecialtyPage(@PathVariable("specialtyAbbr") String specialtyAbbreviation,
                                          Model model, @PathVariable String facultyAbbr) {
        Specialty specialty =specialtyService.getSpecialtyByAbbreviation(specialtyAbbreviation);
//        Faculty faculty = educationService.getFacultyByAbbreviation(facultyAbbreviation);
        model.addAttribute("specialty", specialty);


        List<Subject> subjects = subjectService.getAllSubjectsBySpecialty(specialty);
//        sort semesters(sort by numbers + some things)
        List<String> sortedSemesters = subjectService.getSortedSemesters(subjects);
        //        TODO: put in the service

        List<Subject> compulsorySubjects = new ArrayList<>();
        List<Subject> compulsoryElectiveSubjects = new ArrayList<>();
        List<Subject> electiveSubjects = new ArrayList<>();
        for (Subject subject : subjects) {
            String type = subject.getType();

            switch (type) {
                case "Compulsory":

                    compulsorySubjects.add(subject);
                    break;
                case "Compulsory elective":
                    compulsoryElectiveSubjects.add(subject);
                    break;
                case "Elective":
                    electiveSubjects.add(subject);
                    break;
            }
        }

        model.addAttribute("compulsorySubjects", compulsorySubjects);
        model.addAttribute("compulsoryElectiveSubjects", compulsoryElectiveSubjects);
        model.addAttribute("electiveSubjects", electiveSubjects);
        model.addAttribute("semesters", sortedSemesters);


        return "subjects-page";
    }

    @PostMapping("/subject/add")
    public String addSubject(@ModelAttribute("subject-name") String name,
                             @ModelAttribute("subject-abbreviation") String abbreviation,
                             @ModelAttribute("subject-specialty-id") Long subjectSpecialtyId,
                             @ModelAttribute("subject-semester") String subjectSemester,
                             @ModelAttribute("subject-type") String subjectType) {
        Specialty specialty = specialtyService.getSpecialtyById(subjectSpecialtyId);
        Subject subject = new Subject();
        subject.setName(name);
        subject.setAbbreviation(abbreviation);
        subject.setSpecialty(specialty);
        subject.setSemester(subjectSemester);
        subject.setType(subjectType);
        subjectService.addSubject(subject);

        return "redirect:/faculties/" + specialty.getFaculty().getAbbreviation() + "/" + specialty.getAbbreviation();
    }

    @PostMapping("/subject/delete")
    public String deleteSubject(@RequestParam("subject-id") Long id,
                                @RequestParam("del-subject-name") String subjectName,
                                @RequestParam("del-subject-specialty-id") Long subjectSpecialtyId,
                                RedirectAttributes redirectAttributes) {
        System.out.println("2222");
        Specialty specialty = specialtyService.getSpecialtyById(subjectSpecialtyId);
        boolean isDeleted = subjectService.deleteSubject(id);

        String infoMessage = isDeleted ? "Subject '" + subjectName + "' deleted successfully" : "You can't delete " + subjectName;
        redirectAttributes.addFlashAttribute("infoMessage", infoMessage);
        return "redirect:/faculties/" + specialty.getFaculty().getAbbreviation() + "/" + specialty.getAbbreviation();
    }
    @PostMapping("/subjectsAll/delete")
    public String deleteSubjectFromAllPage(@RequestParam("subject-id") Long id,
                                               @RequestParam("del-subject-name") String subjectName,
                                               RedirectAttributes redirectAttributes) {
        System.out.println("11111");
        boolean isDeleted = subjectService.deleteSubject(id);
        System.out.println("ssubject name " + subjectName);
        String infoMessage = isDeleted ? "subject '" + subjectName + "' deleted successfully" : "You can't delete " + subjectName;
        redirectAttributes.addFlashAttribute("infoMessage", infoMessage);
        return "redirect:/subjectsAll";
    }
}
