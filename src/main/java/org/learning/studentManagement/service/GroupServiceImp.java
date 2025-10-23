package org.learning.studentManagement.service;

import org.learning.studentManagement.dataaccess.GroupDao;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupServiceImp implements GroupService{

    @Autowired
    private GroupDao groupDao;

    @Override
    public Optional<Group> findById(int Id) {
        return groupDao.findById(Id);
    }

    @Override
    public Optional<Group> findByName(String name) {
        return groupDao.findByName(name);
    }

    @Override
    public List<Group> findAll() {
        return groupDao.findAll();
    }

    @Override
    public Group save(Group group) {
        return groupDao.save(group);
    }


    @Override
    public void update(Group group) {
        groupDao.update(group);
    }

    @Override
    public void delete(Group group) {
        groupDao.delete(group);
    }
}
