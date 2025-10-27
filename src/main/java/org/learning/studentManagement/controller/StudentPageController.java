package org.learning.studentManagement.controller;

import lombok.extern.slf4j.Slf4j;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.learning.studentManagement.service.GroupService;
import org.learning.studentManagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Controller
public class StudentPageController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private GroupService groupService;

    private Pair<List<Student>, Integer> formatList(List<Student> list, String sortBy, Integer pageNum, String selectedGroupId) {
        switch (sortBy) {
            case "firstName" -> Collections.sort(list, Comparator.comparing(Student::getFirstName));
            case "lastName" -> Collections.sort(list, Comparator.comparing(Student::getLastName));
            case "email" -> Collections.sort(list, Comparator.comparing(Student::getEmail));
        }

        if (selectedGroupId != null && !selectedGroupId.isEmpty()) {
            list = list.stream().filter(student ->
                    student.getGroup().getId() == Integer.parseInt(selectedGroupId)).toList();
        }

        int pageCount = (int) Math.ceil(list.size() / 10.0);

        if (pageCount < pageNum) {
            pageNum = pageCount;
        }

        list = list.subList((pageNum - 1) * 10, Math.min(pageNum * 10, list.size()));

        return Pair.of(list, pageCount);

    }

    @GetMapping("/student")
    public String student(
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") String pageNum,
            @RequestParam(value = "selectedGroup", required = false, defaultValue = "") String selectedGroupId,
            Model model
    ) {
        List<Student> students = studentService.findAll();
        List<Group> groups = groupService.findAll();

        int totalStudentCount = students.size();
        Pair<List<Student>, Integer> studentsAndCount = formatList(students, sortBy, Integer.valueOf(pageNum), selectedGroupId);
        students = studentsAndCount.getFirst();
        int pageCount = studentsAndCount.getSecond();

        model.addAttribute("students", students);
        model.addAttribute("groups", groups);
        model.addAttribute("totalStudentCount", totalStudentCount);
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentPageNum", pageNum);
        model.addAttribute("currentSelectedGroup", selectedGroupId);
        return "student";

    }

    @GetMapping("/student/{id}")
    public String student(@PathVariable Integer id, Model model) {
        Student student = studentService.findById(id);

        model.addAttribute("student", student);

        return "oneStudent";
    }

    @PostMapping("/utils/student/add")
    public RedirectView addStudent(
            @RequestParam(value = "profilePic") MultipartFile file,
            @RequestParam(value = "firstName") String firstName,
            @RequestParam(value = "lastName") String lastName,
            @RequestParam(value = "email") String email,
            @RequestParam(value = "cnp") String cnp,
            @RequestParam(value = "groupName") String groupName
    ) throws IOException {

        studentService.save(firstName, lastName, email, cnp, groupName, file);

        return new RedirectView("/student"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads

    }

    @PostMapping("/utils/student/delete")
    public RedirectView deleteStudent(
            @RequestParam(value = "id") String id
    ) {

        studentService.delete(id);

        return new RedirectView("/student"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads

    }

    @PostMapping("/utils/student/alter")
    public RedirectView alterStudent(
            @RequestParam(value = "profilePic") MultipartFile file,
            @RequestParam(value = "id") String id,
            @RequestParam(value = "firstName", required = false, defaultValue = "") String firstName,
            @RequestParam(value = "lastName", required = false, defaultValue = "") String lastName,
            @RequestParam(value = "email", required = false, defaultValue = "") String email,
            @RequestParam(value = "cnp", required = false, defaultValue = "") String cnp,
            @RequestParam(value = "groupid", required = false, defaultValue = "") String groupid
    ) throws IOException {

        studentService.update(id, firstName, lastName, email, cnp, groupid, file);

        return new RedirectView("/student"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads

    }

    @GetMapping("/imgs/{imgName}")
    public ResponseEntity<Resource> imgServing(@PathVariable String imgName) throws MalformedURLException, FileNotFoundException {
        Resource resource = studentService.serveImg(imgName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imgName + "\"")
                .body(resource);
    }

    @PostMapping("/utils/student/enterCourse")
    public RedirectView enterCourse(
            @RequestParam(value = "studentId") String studentId,
            @RequestParam(value = "courseId") String courseId
    ) {
        studentService.enterCourse(Integer.parseInt(studentId), Integer.parseInt(courseId));

        return new RedirectView("/student/" + studentId);

    }

    @PostMapping("/utils/student/leaveCourse")
    public RedirectView leaveCourse(
            @RequestParam(value = "studentId") String studentId,
            @RequestParam(value = "courseId") String courseId
    ) {
        studentService.leaveCourse(Integer.parseInt(studentId), Integer.parseInt(courseId));

        return new RedirectView("/student/" + studentId);
    }

}
