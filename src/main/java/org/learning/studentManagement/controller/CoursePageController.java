package org.learning.studentManagement.controller;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.learning.studentManagement.model.Course;
import org.learning.studentManagement.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.List;

import static org.learning.studentManagement.utils.SecurityUtils.addAuthsAndNameToModel;

@Slf4j
@Controller
public class CoursePageController {
    @Autowired
    private CourseService courseService;

    @GetMapping("/course")
    public String course(Model model, HttpServletRequest request) {
        List<Course> courses = courseService.findAll();

        addAuthsAndNameToModel(model);
        model.addAttribute("courses", courses);
        return "course";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/course/add")
    public RedirectView addCourse(
            @RequestParam("name") String name,
            @Nullable @RequestParam("startDate") LocalDate startDate,
            @Nullable @RequestParam("endDate") LocalDate endDate
    ) {

        courseService.save(name, startDate, endDate);

        return new RedirectView("/course"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/course/delete")
    public RedirectView deleteCourse(
            @RequestParam("id") String id
    ) {

        courseService.delete(Integer.parseInt(id));

        return new RedirectView("/course"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/course/alter")
    public RedirectView alterCourse(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate
    ) {
        courseService.update(Integer.valueOf(id), name, startDate, endDate);

        return new RedirectView("/course"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/course/removeStudent")
    public RedirectView removeStudent(
            @RequestParam(value = "studentId") String studentId,
            @RequestParam(value = "courseId") String courseId
    ) {
        courseService.removeStudent(Integer.parseInt(studentId), Integer.parseInt(courseId));

        return new RedirectView("/course"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/course/addStudent")
    public RedirectView addStudent(
            @RequestParam(value = "studentId") String studentId,
            @RequestParam(value = "courseId") String courseId
    ) {
        courseService.addStudent(Integer.parseInt(studentId), Integer.parseInt(courseId));

        return new RedirectView("/course"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads
    }

}
