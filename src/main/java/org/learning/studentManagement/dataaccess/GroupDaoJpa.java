package org.learning.studentManagement.dataaccess;

import jakarta.transaction.Transactional;
import org.learning.studentManagement.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupDaoJpa extends GroupDao, JpaRepository<Group, Integer> {
    @Modifying
    @Transactional
    @Query("""
            UPDATE Group g
            SET g.name = :#{#group.name}
            WHERE g.id = :#{#group.id}
            """)
    @Override
    void update(@Param("group") Group group); // JpaRepository doesn't generate default update method
}
