package org.learning.studentManagement.service;

import org.learning.studentManagement.model.Group;

import java.util.List;
import java.util.Optional;

/**
 * Processing for Group data
 */
public interface GroupService {
    Optional<Group> findById(int Id);

    Optional<Group> findByName(String name);

    List<Group> findAll();

    Group save(Group group);

    void update(Group group);

    void delete(Group group);

}
