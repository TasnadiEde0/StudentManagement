package org.learning.studentManagement.controller.rest;

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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private Mapper mapper;

    @GetMapping
    @ResponseBody
    public List<StudentDto> getStudents(
            @RequestParam(value = "sortBy", required = false, defaultValue = "") String sortBy,
            @RequestParam(value = "pageNum", required = false, defaultValue = "") String pageNum,
            @RequestParam(value = "selectedGroup", required = false, defaultValue = "") String selectedGroupId
    ) {
        if(sortBy.isEmpty() && pageNum.isEmpty() && selectedGroupId.isEmpty()) {
            return studentService.findAll().stream()
                    .map(student -> mapper.studentToStudentDto(student)).toList();
        }

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

        return students.stream().map(student -> mapper.studentToStudentDto(student)).toList();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public StudentDto getStudentById(@PathVariable("id") Integer id) {
        return mapper.studentToStudentDto(studentService.findById(id));
    }

    @PostMapping
    @ResponseBody
    public StudentDto postStudent(@RequestBody StudentDto studentDto) throws IOException {
        return mapper.studentToStudentDto(studentService.save(studentDto));
    }

    @PutMapping("/{id}")
    @ResponseBody
    public StudentDto putStudent(
            @RequestBody StudentDto studentDto,
            @PathVariable("id") Integer id
    ) throws IOException {
        studentDto.setId(String.valueOf(id));
        studentService.update(studentDto);
        return mapper.studentToStudentDto(studentService.findById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void putStudent(@PathVariable("id") Integer id) throws IOException {
        studentService.delete(String.valueOf(id));
    }

    @PostMapping("/{studentId}/course/{courseId}")
    public void enterCourse(
            @PathVariable("studentId") Integer studentId,
            @PathVariable("courseId") Integer courseId
    ) {
        studentService.enterCourse(studentId, courseId);
    }

    @DeleteMapping("/{studentId}/course/{courseId}")
    public void leaveCourse(
            @PathVariable("studentId") Integer studentId,
            @PathVariable("courseId") Integer courseId
    ) {
        studentService.leaveCourse(studentId, courseId);
    }

}
