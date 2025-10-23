package org.learning.studentManagement.service;

import jakarta.transaction.Transactional;
import org.learning.studentManagement.dataaccess.GroupDao;
import org.learning.studentManagement.dataaccess.StudentDao;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImp implements StudentService {
    @Autowired
    private StudentDao studentDao;

    @Autowired
    private GroupDao groupDao;

    @Override
    public Optional<Student> findById(int Id) {
        return studentDao.findById(Id);
    }

    @Override
    public Optional<Student> findByCnp(String cnp) {
        return studentDao.findByCnp(cnp);
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        return studentDao.findByEmail(email);
    }

    @Override
    public List<Student> findAll() {
        return studentDao.findAll();
    }

    @Override
    public Student save(Student student) {
        return studentDao.save(student);
    }

    @Override
    @Transactional
    public void createNewStudent(String firstName, String lastName, String email, String cnp, String groupName) {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);

//        emailDuplicateCheck(email);
        student.setEmail(email);

//        cnpDuplicateCheck(cnp);
        student.setCnp(cnp);

        studentDao.save(student);

//        Group group = fetchGroup(groupName);
        Group group = new Group();
        group.setName(groupName);

        student.setGroup(group);

        groupDao.save(group);
        studentDao.update(student);

    }

    @Override
    public void update(Student student) {
        studentDao.update(student);
    }

    @Override
    public void delete(Student student) {
        studentDao.delete(student);
    }

}
