package org.learning.studentManagement.service;

import org.learning.studentManagement.model.Group;

import java.util.List;
import java.util.Optional;

/**
 * Processing for Group data
 */
public interface GroupService {
    Group findById(int Id);

    Optional<Group> findByName(String name);

    List<Group> findAll();

    Group save(String name);

    void update(String id, String name);

    void delete(String id);

}
