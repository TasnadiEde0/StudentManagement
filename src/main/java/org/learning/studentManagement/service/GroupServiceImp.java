package org.learning.studentManagement.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.learning.studentManagement.dataaccess.GroupDao;
import org.learning.studentManagement.model.Group;
import org.learning.studentManagement.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GroupServiceImp implements GroupService{

    @Autowired
    private GroupDao groupDao;

    @Override
    public Group findById(int Id) {

        Group group = groupDao.findById(Id).orElse(null);

        if (group == null) {
            throw new IllegalArgumentException("The given ID isn't associated with a group!");
        }

        return group;
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
    @Transactional
    public Group save(String name) {
        Group group = new Group();

//        groupDuplicateCheck(name);

        group.setName(name);
        return groupDao.save(group);
    }


    @Override
    @Transactional
    public void update(String id, String name) {
        Group group = groupDao.findById(Integer.parseInt(id)).orElse(null);

        if (group == null) {
            throw new IllegalArgumentException("The given ID isn't associated with a group!");
        }

        if (!name.isEmpty()) {
            group.setName(name);
        }

        groupDao.update(group);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Group group = groupDao.findById(Integer.parseInt(id)).orElse(null);

        groupDao.delete(group);
    }
}
