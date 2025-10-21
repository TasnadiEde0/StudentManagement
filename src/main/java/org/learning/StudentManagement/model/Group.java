package org.learning.StudentManagement.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Group extends BaseObject {
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Student> students;
}
