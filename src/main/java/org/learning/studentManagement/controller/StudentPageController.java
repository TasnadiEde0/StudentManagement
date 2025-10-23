package org.learning.studentManagement.controller;

import lombok.extern.slf4j.Slf4j;
import org.learning.studentManagement.exception.StudentCnpDuplicateException;
import org.learning.studentManagement.exception.StudentEmailDuplicateException;
import org.learning.studentManagement.exception.GroupNameDuplicateException;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.learning.studentManagement.service.GroupService;
import org.learning.studentManagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class StudentPageController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private GroupService groupService;

    @GetMapping("/student")
    public String student(Model model) {
        List<Student> students = studentService.findAll();
        List<Group> groups = groupService.findAll();
        model.addAttribute("students", students);
        model.addAttribute("groups", groups);
        return "student";
    }

    private void groupNameDuplicateCheck(String name) throws GroupNameDuplicateException {
        Optional<Group> testGroup = groupService.findByName(name);
        if (testGroup.isPresent()) {
            throw new GroupNameDuplicateException();
        }
    }

    private void emailDuplicateCheck(String email) throws StudentEmailDuplicateException {
        Optional<Student> testEmail = studentService.findByEmail(email);
        if (testEmail.isPresent()) {
            throw new StudentEmailDuplicateException();
        }
    }
    private void cnpDuplicateCheck(String cnp) throws GroupNameDuplicateException {
        Optional<Student> testCnp = studentService.findByCnp(cnp);
        if (testCnp.isPresent()) {
            throw new StudentCnpDuplicateException();
        }
    }

    private Group fetchGroup(String name) throws GroupNameDuplicateException {
        Group group = groupService.findByName(name).orElse(null);

        if  (group == null) {
            group = new Group();
            group.setName(name);

            groupService.save(group);

        }

        return group;

    }

    @PostMapping("/utils/student/add")
    public RedirectView addStudent(
            @RequestParam(value = "firstName") String firstName,
            @RequestParam(value = "lastName") String lastName,
            @RequestParam(value = "email") String email,
            @RequestParam(value = "cnp") String cnp,
            @RequestParam(value = "groupName") String groupName
    ) throws StudentCnpDuplicateException {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);

        emailDuplicateCheck(email);
        student.setEmail(email);

        cnpDuplicateCheck(cnp);
        student.setCnp(cnp);

        studentService.save(student);

        Group group = fetchGroup(groupName);
        student.setGroup(group);

        studentService.update(student);

        return new RedirectView("/student"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads
    }

    @PostMapping("/utils/student/delete")
    public RedirectView deleteStudent(
            @RequestParam(value = "id") String id
    ) {
        Student student = studentService.findById(Integer.parseInt(id)).orElse(null);
        studentService.delete(student);

        return new RedirectView("/student"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads
    }

    @PostMapping("/utils/student/alter")
    public RedirectView alterStudent(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "firstName", required = false, defaultValue = "") String firstName,
            @RequestParam(value = "lastName", required = false, defaultValue = "") String lastName,
            @RequestParam(value = "email", required = false, defaultValue = "") String email,
            @RequestParam(value = "cnp", required = false, defaultValue = "") String cnp,
            @RequestParam(value = "groupid", required = false, defaultValue = "") String groupid
    ) {
        Student student = studentService.findById(Integer.parseInt(id)).orElse(null);

        if (!firstName.isEmpty()) {
            student.setFirstName(firstName);
        }
        if (!lastName.isEmpty()) {
            student.setLastName(lastName);
        }
        if (!email.isEmpty()) {
            student.setEmail(email);
        }
        if (!cnp.isEmpty()) {
            student.setCnp(cnp);
        }
        if (!groupid.isEmpty()) {
            student.setGroup(groupService.findById(Integer.parseInt(groupid)).orElse(null));
        }

        studentService.update(student);

        return new RedirectView("/student"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads
    }

}
