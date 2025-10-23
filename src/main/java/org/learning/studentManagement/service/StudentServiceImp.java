package org.learning.studentManagement.service;

import org.learning.studentManagement.dataaccess.StudentDao;
import org.learning.studentManagement.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImp implements StudentService {
    @Autowired
    private StudentDao studentDao;

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
    public void update(Student student) {
        studentDao.update(student);
    }

    @Override
    public void delete(Student student) {
        studentDao.delete(student);
    }

}
