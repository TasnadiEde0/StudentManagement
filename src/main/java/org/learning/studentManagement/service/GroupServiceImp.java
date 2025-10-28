package org.learning.studentManagement.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.learning.studentManagement.dataaccess.GroupDao;
import org.learning.studentManagement.model.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GroupServiceImp implements GroupService {

    @Autowired
    private GroupDao groupDao;

    /**
     * Check if the name already belongs to a Group
     *
     * @param name Name to be checked
     * @throws IllegalArgumentException If the name is taken
     */
    private void groupDuplicateCheck(String name) throws IllegalArgumentException {
        Optional<Group> testGroup = groupDao.findByName(name);
        if (testGroup.isPresent()) {
            throw new IllegalArgumentException("Group name already taken!");
        }
    }

    /**
     * Find the Group with the provided {@code id}
     *
     * @param Id Id of the queried Group
     * @return Group with the given {@code id}
     * @throws IllegalArgumentException If the {@code id} doesn't belong to a Group
     */
    @Override
    public Group findById(int Id) throws IllegalArgumentException {
        return groupDao.findById(Id).orElseThrow(() ->
                new IllegalArgumentException("The given ID isn't associated with a group!"));

    }

    /**
     * Find the Group with the provided {@code name}
     *
     * @param name Name of the queried Group
     * @return If exists Group with the given {@code name}
     */
    @Override
    public Optional<Group> findByName(String name) {
        return groupDao.findByName(name);
    }

    /**
     * Returns all Groups
     *
     * @return Every Group
     */
    @Override
    public List<Group> findAll() {
        return groupDao.findAll();
    }

    /**
     * Create and save a Group with the given properties
     *
     * @param name New Group name
     * @return Saved Group
     * @throws IllegalArgumentException If the {@code name} is not unique
     */
    @Override
    @Transactional
    public Group save(String name) throws IllegalArgumentException {
        Group group = new Group();

        groupDuplicateCheck(name);

        group.setName(name);
        return groupDao.save(group);
    }

    /**
     * Update the Group with the provided values
     *
     * @param id   Id of the Group to be modified. Must belong to a Group
     * @param name New name of the Group or {@code null}
     * @throws IllegalArgumentException If the {@code id} doesn't
     *                                  belong to a Group or if {@code name} is not unique
     */
    @Override
    @Transactional
    public void update(String id, String name) throws IllegalArgumentException {
        Group group = findById(Integer.parseInt(id));

        if (!name.isEmpty()) {
            groupDuplicateCheck(name);
            group.setName(name);
        }

        groupDao.update(group);
    }

    /**
     * Delete the Group associated with the {@code id}
     *
     * @param id Id of the Group to be deleted
     * @throws IllegalArgumentException If the {@code id} doesn't belong to a Group
     */
    @Override
    @Transactional
    public void delete(String id) throws IllegalArgumentException {
        Group group = findById(Integer.parseInt(id));

        groupDao.delete(group);
    }
}
