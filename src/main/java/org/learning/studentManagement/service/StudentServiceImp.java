package org.learning.studentManagement.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.learning.studentManagement.dataaccess.GroupDao;
import org.learning.studentManagement.dataaccess.StudentDao;
import org.learning.studentManagement.exception.GroupNameDuplicateException;
import org.learning.studentManagement.exception.StudentCnpDuplicateException;
import org.learning.studentManagement.exception.StudentEmailDuplicateException;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class StudentServiceImp implements StudentService {
    @Autowired
    private StudentDao studentDao;

    @Autowired
    private GroupDao groupDao;

    String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads/imgs";

    private void emailDuplicateCheck(String email) throws StudentEmailDuplicateException {
        Optional<Student> testEmail = studentDao.findByEmail(email);
        if (testEmail.isPresent()) {
            throw new StudentEmailDuplicateException();
        }
    }
    private void cnpDuplicateCheck(String cnp) throws GroupNameDuplicateException {
        Optional<Student> testCnp = studentDao.findByCnp(cnp);
        if (testCnp.isPresent()) {
            throw new StudentCnpDuplicateException();
        }
    }

    private Group fetchGroup(String name) throws GroupNameDuplicateException {
        Group group = groupDao.findByName(name).orElse(null);

        if  (group == null) {
            group = new Group();
            group.setName(name);

            group = groupDao.save(group);

        }

        return group;

    }

    @Override
    public Student findById(int Id) {
        Student student = studentDao.findById(Id).orElse(null);

        if (student == null) {
            throw new IllegalArgumentException("The given ID isn't associated with a student!");
        }

        return student;
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
    @Transactional
    public Student save(String firstName, String lastName, String email, String cnp, String groupName, MultipartFile file) throws IOException {

        Student student = new Student();

        if (file.isEmpty()) {
            throw new IllegalArgumentException("No picture has been uploaded!");
        }

        Path imgPath = Paths.get(UPLOAD_DIRECTORY, cnp + "_" + firstName + "_" + lastName + ".png");
        Files.write(imgPath, file.getBytes());
        student.setImgName(cnp + "_" + firstName + "_" + lastName + ".png");

        student.setFirstName(firstName);
        student.setLastName(lastName);

        emailDuplicateCheck(email);
        student.setEmail(email);

        cnpDuplicateCheck(cnp);
        student.setCnp(cnp);

        studentDao.save(student);

        Group group = fetchGroup(groupName);

        student.setGroup(group);

        groupDao.save(group);
        studentDao.update(student);

        return student;

    }

    @Override
    @Transactional
    public void update(String id,  String firstName, String lastName, String email, String cnp, String groupid, MultipartFile file) throws IOException {
        Student student = studentDao.findById(Integer.parseInt(id)).orElse(null);

        if  (student == null) {
            throw new IllegalArgumentException("The given ID isn't associated with a student!");
        }

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
            student.setGroup(groupDao.findById(Integer.parseInt(groupid)).orElse(null));
        }

        if (!file.isEmpty()) {
            Path imgPath = Paths.get(UPLOAD_DIRECTORY, student.getCnp() + "_" +
                    student.getFirstName() + "_" + student.getLastName() + ".png");
            Files.write(imgPath, file.getBytes());
            student.setImgName(student.getCnp() + "_" +
                    student.getFirstName() + "_" + student.getLastName() + ".png");
        }

        studentDao.update(student);

    }

    @Override
    @Transactional
    public void delete(String id) {
        Student student = studentDao.findById(Integer.parseInt(id)).orElse(null);

        if  (student == null) {
            throw new IllegalArgumentException("The given ID isn't associated with any student!");
        }

        studentDao.delete(student);

    }

    @Override
    public Resource serveImg(String imgName) throws MalformedURLException {
        Path path = Paths.get(UPLOAD_DIRECTORY).resolve(imgName);

        Resource resource = new UrlResource(path.toUri());

        return  resource;
    }

}
