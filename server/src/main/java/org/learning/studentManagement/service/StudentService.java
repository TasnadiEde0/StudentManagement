package org.learning.studentManagement.service;

import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.learning.studentManagement.model.dto.StudentDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

/**
 * Processing for Student data
 */
public interface StudentService {
    Student findById(int Id);

    Optional<Student> findByCnp(String cnp);

    Optional<Student> findByEmail(String email);

    List<Student> findAll();

    int count();

    int countByGroup(Group group);

    Student save(StudentDto studentDto) throws IOException;

    void update(StudentDto studentDto) throws IOException;

    void delete(String id);

    Resource serveImg(String imgName) throws MalformedURLException, FileNotFoundException;

    void enterCourse(Integer studentId, Integer courseId);

    void leaveCourse(Integer studentId, Integer courseId);

    List<Student> findAllFiltered(Group group, String orderProperty, Integer page);

}
