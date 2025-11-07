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
        if (group.getStudents() != null) {
            dto.setStudentIds(group.getStudents().stream()
                    .map(student -> String.valueOf(student.getId())).toList());
        }

        return dto;
    }

    public Group dtoToGroup(GroupDto dto) {
        Group group = new Group();
        group.setId(Integer.parseInt(dto.getId()));
        group.setName(dto.getName());
        group.setStudents(new ArrayList<>());

        return group;
    }

    public CourseDto courseToCourseDto(Course course) {
        CourseDto dto = new CourseDto();
        dto.setId(String.valueOf(course.getId()));
        dto.setName(course.getName());
        dto.setStartDate(course.getStartDate());
        dto.setEndDate(course.getEndDate());
        if(course.getStudents() != null) {
            dto.setStudentIds(course.getStudents().stream()
                    .map(student -> String.valueOf(student.getId())).toList());
        }

        return dto;

    }

    public Course dtoToCourse(CourseDto dto) {
        Course course = new Course();
        if (dto.getId() != null) {
            course.setId(Integer.parseInt(dto.getId()));
        }
        if (dto.getName() != null) {
            course.setName(dto.getName());
        }
        if (dto.getStartDate() != null) {
            course.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            course.setEndDate(dto.getEndDate());
        }
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
        if(student.getCourses() != null) {
            dto.setCourseIds(student.getCourses().stream()
                    .map(course -> String.valueOf(course.getId())).toList());
        }

        return dto;
    }

    public Student dtoToStudent(StudentDto dto) {
        Student student = new Student();
        if(dto.getId() != null) {
            student.setId(Integer.parseInt(dto.getId()));
        }
        if(dto.getFirstName()  != null) {
            student.setFirstName(dto.getFirstName());
        }
        if(dto.getLastName()  != null) {
            student.setLastName(dto.getLastName());
        }
        if(dto.getEmail() != null) {
            student.setEmail(dto.getEmail());
        }
        if(dto.getCnp() != null) {
            student.setCnp(dto.getCnp());
        }
        if(dto.getImgName() != null) {
            student.setImgName(dto.getImgName());
        }

        return student;
    }


}
