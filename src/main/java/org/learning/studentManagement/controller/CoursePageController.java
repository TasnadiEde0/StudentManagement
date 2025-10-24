package org.learning.studentManagement.controller;

import org.springframework.ui.Model;
import lombok.extern.slf4j.Slf4j;
import org.learning.studentManagement.model.Course;
import org.learning.studentManagement.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Controller
public class CoursePageController {
    @Autowired
    private CourseService courseService;

    @GetMapping("/course")
    public String course(Model model) {

        List<Course> courses = courseService.findAll();

        model.addAttribute("courses", courses);

        return "course";
    }

    @PostMapping("/utils/course/add")
    public RedirectView addCourse(
            @RequestParam("name") String name,
            @RequestParam("startDate")LocalDate startDate,
            @RequestParam("endDate")LocalDate endDate
            ) {

        courseService.save(name, startDate, endDate);

        return new RedirectView("/course");

    }

    @PostMapping("/utils/course/delete")
    public RedirectView deleteCourse(
            @RequestParam("id") String id
    ) {

        courseService.delete(Integer.parseInt(id));

        return new RedirectView("/course");

    }

    @PostMapping("/utils/course/alter")
    public RedirectView alterCourse(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate
    ) {
        courseService.update(Integer.valueOf(id), name, startDate, endDate);

        return new RedirectView("/course");

    }

    @PostMapping("/utils/course/removeStudent")
    public RedirectView removeStudent(
            @RequestParam(value = "studentId") String studentId,
            @RequestParam(value = "courseId")  String courseId
    ) {
        courseService.removeStudent(Integer.parseInt(studentId), Integer.parseInt(courseId));

        return new RedirectView("/course");
    }

    @PostMapping("/utils/course/addStudent")
    public RedirectView addStudent(
            @RequestParam(value = "studentId") String studentId,
            @RequestParam(value = "courseId")  String courseId
    ) {
        courseService.addStudent(Integer.parseInt(studentId), Integer.parseInt(courseId));

        return new RedirectView("/course");
    }


}
