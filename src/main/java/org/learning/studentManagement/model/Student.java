package org.learning.studentManagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "tb_student")
public class Student extends BaseObject {
    @Size(min = 1, max = 16, message = "Invalid first name")
    private String firstName;

    @Size(min = 1, max = 16, message = "Invalid last name")
    private String lastName;

    @Size(min = 13, max = 13, message = "Invalid CNP")
    @Column(nullable = false, unique = true)
    private String cnp;

    @Email(message = "Email should be valid")
    private String email;

    private String imgName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = true)
    private Group group;

    @ManyToMany(cascade = { CascadeType.PERSIST,  CascadeType.MERGE })
    @JoinTable(
        name = "tb_student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses;

}
