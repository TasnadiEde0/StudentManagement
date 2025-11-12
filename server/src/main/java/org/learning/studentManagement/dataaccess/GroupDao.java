package org.learning.studentManagement.dataaccess;

import org.learning.studentManagement.model.Group;

import java.util.List;
import java.util.Optional;

/**
 * Data access object used to interact with the database and obtain Group data
 */

public interface GroupDao {
    Optional<Group> findById(int Id);

    Optional<Group> findByName(String name);

    List<Group> findAll();

    Group save(Group group);

    void update(Group group);

    void delete(Group group);

}
