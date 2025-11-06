package org.learning.studentManagement.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.learning.studentManagement.model.dto.Mapper;
import org.learning.studentManagement.model.dto.StudentDto;
import org.learning.studentManagement.model.dto.StudentListingDto;
import org.learning.studentManagement.service.GroupService;
import org.learning.studentManagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import static org.learning.studentManagement.utils.SecurityUtils.addAuthsAndNameToModel;

@Slf4j
@Controller
public class StudentPageController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private Mapper mapper;

    @GetMapping("/student")
    public String student(
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") String pageNum,
            @RequestParam(value = "selectedGroup", required = false, defaultValue = "") String selectedGroupId,
            Model model,
            HttpServletRequest request
    ) {
        List<Group> groups = groupService.findAll();
        int studentCount = studentService.count();
        int pageCount = (int) Math.ceil(studentCount / 10.0);

        Group selectedGroup = null;
        if(!selectedGroupId.isEmpty()) {
            selectedGroup = groupService.findById(Integer.parseInt(selectedGroupId));
            int studentCountByGroup = studentService.countByGroup(selectedGroup);
            pageCount = (int) Math.ceil(studentCountByGroup / 10.0);
            if (Integer.parseInt(pageNum) > pageCount) {
                pageNum = String.valueOf(pageCount);
            }
        }

        List<Student> students = studentService.findAllFiltered(selectedGroup, sortBy, Integer.parseInt(pageNum));

        addAuthsAndNameToModel(model);
        model.addAttribute("students", students);
        model.addAttribute("groups", groups);
        model.addAttribute("totalStudentCount", studentCount);
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentPageNum", pageNum);
        model.addAttribute("currentSelectedGroup", selectedGroupId);
        return "student";

    }

    @GetMapping("/fetchedStudent")
    @ResponseBody
    public StudentListingDto fetchedStudent(
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") String pageNum,
            @RequestParam(value = "selectedGroup", required = false, defaultValue = "") String selectedGroupId
    ) {
        int studentCount = studentService.count();
        int pageCount = (int) Math.ceil(studentCount / 10.0);

        Group selectedGroup = null;
        if(!selectedGroupId.isEmpty()) {
            selectedGroup = groupService.findById(Integer.parseInt(selectedGroupId));
            int studentCountByGroup = studentService.countByGroup(selectedGroup);
            pageCount = (int) Math.ceil(studentCountByGroup / 10.0);
            if (Integer.parseInt(pageNum) > pageCount) {
                pageNum = String.valueOf(Math.max(1, pageCount));
            }
        }

        List<Student> students = studentService.findAllFiltered(selectedGroup, sortBy, Integer.parseInt(pageNum));
        List<StudentDto> studentDtos =
                students.stream().map(student -> mapper.studentToStudentDto(student)).toList();

        return new StudentListingDto(studentDtos, pageCount);
    }

    @GetMapping("/student/{id}")
    public String student(@PathVariable Integer id, Model model, HttpServletRequest request) {
        Student student = studentService.findById(id);


        addAuthsAndNameToModel(model);
        model.addAttribute("student", student);
        return "oneStudent";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/student/add")
    public RedirectView addStudent(
            StudentDto studentDto
    ) throws IOException {

        studentService.save(studentDto);

        return new RedirectView("/student"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/student/delete")
    public RedirectView deleteStudent(
            @RequestParam(value = "id") String id
    ) {

        studentService.delete(id);

        return new RedirectView("/student");

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/student/alter")
    public RedirectView alterStudent(
            StudentDto studentDto
    ) throws IOException {

        studentService.update(studentDto);

        return new RedirectView("/student");

    }

    //Dynamically serve the student profile pictures
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/imgs/{imgName}")
    public ResponseEntity<Resource> imgServing(@PathVariable String imgName) throws MalformedURLException, FileNotFoundException {
        Resource resource = studentService.serveImg(imgName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imgName + "\"")
                .body(resource);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/student/enterCourse")
    public RedirectView enterCourse(
            @RequestParam(value = "studentId") String studentId,
            @RequestParam(value = "courseId") String courseId
    ) {
        studentService.enterCourse(Integer.parseInt(studentId), Integer.parseInt(courseId));

        return new RedirectView("/student/" + studentId);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/student/leaveCourse")
    public RedirectView leaveCourse(
            @RequestParam(value = "studentId") String studentId,
            @RequestParam(value = "courseId") String courseId
    ) {
        studentService.leaveCourse(Integer.parseInt(studentId), Integer.parseInt(courseId));

        return new RedirectView("/student/" + studentId);
    }

}
