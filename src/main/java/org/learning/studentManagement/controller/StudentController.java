package org.learning.studentManagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.learning.studentManagement.model.Student;
import org.learning.studentManagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Api controller for testing purposes
 */

@Slf4j
@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public String findAll() throws JsonProcessingException {
        List<Student> students = studentService.findAll();
        return objectMapper.writeValueAsString(students);
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Integer id) throws JsonProcessingException {
        Student student = studentService.findById(id).orElse(null);
        return objectMapper.writeValueAsString(student);
    }

    @PostMapping
    public String save(@RequestBody Student student) throws JsonProcessingException {
        Student savedStudent = studentService.save(student);
        return objectMapper.writeValueAsString(savedStudent);
    }

    @PutMapping
    public void update(@RequestBody Student student) throws JsonProcessingException {
        studentService.update(student);
    }

    @DeleteMapping
    public void delete(@RequestBody Student student) throws JsonProcessingException {
        studentService.delete(student);
    }
}
