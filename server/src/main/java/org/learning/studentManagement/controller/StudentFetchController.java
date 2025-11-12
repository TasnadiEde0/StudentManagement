package org.learning.studentManagement.controller;

import lombok.extern.slf4j.Slf4j;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.learning.studentManagement.model.dto.GroupDto;
import org.learning.studentManagement.model.dto.Mapper;
import org.learning.studentManagement.model.dto.StudentDto;
import org.learning.studentManagement.model.dto.StudentListingDto;
import org.learning.studentManagement.service.CourseService;
import org.learning.studentManagement.service.GroupService;
import org.learning.studentManagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
public class StudentFetchController {
    @Autowired
    private StudentService  studentService;
    
    @Autowired
    private GroupService groupService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private Mapper mapper;

    @GetMapping("/oldApi/student")
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
        }
        if (Integer.parseInt(pageNum) > pageCount) {
            pageNum = String.valueOf(Math.max(1, pageCount));
        }

        List<Student> students = studentService.findAllFiltered(selectedGroup, sortBy, Integer.parseInt(pageNum));
        List<StudentDto> studentDtos =
                students.stream().map(student -> mapper.studentToStudentDto(student)).toList();
        List<GroupDto> groupDtos = groupService.findAll()
                .stream().map(group -> mapper.grouptoGroupDto(group)).toList();

        return new StudentListingDto(studentDtos, groupDtos, pageCount, studentCount);
    }

    @PostMapping("/oldApi/student")
    public ResponseEntity<String> addStudent(
            StudentDto studentDto
    ) throws IOException {

        studentService.save(studentDto);

        return ResponseEntity.ok("Student added!");
    }

    @DeleteMapping("/oldApi/student")
    public ResponseEntity<String> deleteStudent(
            @RequestParam(value = "id") String id
    ) {

        studentService.delete(id);

        return ResponseEntity.ok("Student deleted!");

    }

    @PutMapping("/oldApi/student")
    public ResponseEntity<String> alterStudent(
            StudentDto studentDto
    ) throws IOException {

        studentService.update(studentDto);

        return ResponseEntity.ok("Student updated!");

    }

}
