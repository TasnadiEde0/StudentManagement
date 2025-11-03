package org.learning.studentManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_group")
public class Group extends BaseObject {
    @Size(min = 4, max = 32, message = "Group name should be between 4 and 32 characters")
    @Pattern(regexp = "^[a-zA-Z 1-9]*$", message = "Group name contains invalid characters")
    @Column(unique = true)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, mappedBy = "group")
    private List<Student> students;

    public Group(String name) {
        this.name = name;
    }
}
