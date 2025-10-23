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

    private Group fetchGroup(Boolean groupCreation, String groupid, String groupName) throws GroupNameDuplicateException {
        Group group = new Group();

        if (groupCreation != null && groupCreation && groupName != null && !groupName.isEmpty()) {

            groupNameDuplicateCheck(groupName);

            group.setName(groupName);
            group = groupService.save(group);
        }
        else {
            group = groupService.findById(Integer.parseInt(groupid)).orElse(null);
        }
        return group;

    }

    @PostMapping("/utils/student/add")
    public RedirectView addStudent(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("cnp") String cnp,
            @RequestParam("groupid") String groupid,
            @RequestParam(value = "groupCreation", required = false) Boolean groupCreation,
            @RequestParam(value = "groupName", required = false) String groupName
    ) throws StudentCnpDuplicateException {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);

        emailDuplicateCheck(email);

        student.setEmail(email);

        cnpDuplicateCheck(cnp);

        student.setCnp(cnp);

        Group group = fetchGroup(groupCreation, groupid, groupName);

        student.setGroup(group);

        studentService.save(student);

        return new RedirectView("/student"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads
    }

    @PostMapping("/utils/student/delete")
    public RedirectView deleteStudent(
            @RequestParam("id") String id
    ) {
        Student student = studentService.findById(Integer.parseInt(id)).orElse(null);
        studentService.delete(student);

        return new RedirectView("/student"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads
    }

    @PostMapping("/utils/student/alter")
    public RedirectView alterStudent(
            @RequestParam("id") String id,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("cnp") String cnp,
            @RequestParam("groupid") String groupid
    ) {
        Student student = studentService.findById(Integer.parseInt(id)).orElse(null);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        student.setCnp(cnp);
        student.setGroup(groupService.findById(Integer.parseInt(groupid)).orElse(null));

        studentService.update(student);

        return new RedirectView("/student"); // Redirecting instead of sending back html to not risk multiple submission in the case of reloads
    }

}
