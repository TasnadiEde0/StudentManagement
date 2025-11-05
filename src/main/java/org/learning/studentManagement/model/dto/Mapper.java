package org.learning.studentManagement.model.dto;

import org.learning.studentManagement.model.Course;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class Mapper {

    public GroupDto grouptoGroupDto(Group group) {
        GroupDto dto = new GroupDto();
        dto.setId(String.valueOf(group.getId()));
        dto.setName(group.getName());

        return dto;
    }

    public Group groupDtoToGroup(GroupDto groupDto) {
        Group group = new Group();
        group.setId(Integer.parseInt(groupDto.getId()));
        group.setName(groupDto.getName());
        group.setStudents(new ArrayList<>());

        return group;
    }

    public CourseDto courseToCourseDto(Course course) {
        CourseDto dto = new CourseDto();
        dto.setId(String.valueOf(course.getId()));
        dto.setName(course.getName());
        dto.setStartDate(course.getStartDate());
        dto.setEndDate(course.getEndDate());

        return dto;

    }

    public Course courseDtoToCourse(CourseDto courseDto) {
        Course course = new Course();
        course.setId(Integer.parseInt(courseDto.getId()));
        course.setName(courseDto.getName());
        course.setStartDate(courseDto.getStartDate());
        course.setEndDate(courseDto.getEndDate());
        course.setStudents(new ArrayList<>());

        return course;
    }

    public StudentDto studentToStudentDto(Student student) {
        StudentDto dto = new StudentDto();
        dto.setId(String.valueOf(student.getId()));
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setCnp(student.getCnp());
        dto.setImgName(student.getImgName());
        dto.setGroupName(student.getGroup().getName());
        dto.setGroupId(String.valueOf(student.getGroup().getId()));

        return dto;
    }

    public Student studentDtoToStudent(StudentDto studentDto) {
        Student student = new Student();
        student.setId(Integer.parseInt(studentDto.getId()));
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        student.setEmail(studentDto.getEmail());
        student.setCnp(studentDto.getCnp());
        student.setImgName(studentDto.getImgName());

        return student;
    }


}
