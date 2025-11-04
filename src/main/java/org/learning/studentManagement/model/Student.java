package org.learning.studentManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
@Table(name = "tb_student", uniqueConstraints = {@UniqueConstraint(columnNames = {"firstName", "lastName"})})
public class Student extends BaseObject {
    @Size(min = 1, max = 16, message = "First name should be between 1 and 16 characters")
    @Pattern(regexp = "^[a-zA-Z 1-9]*$", message = "First name contains invalid characters")
    private String firstName;

    @Size(min = 1, max = 16, message = "Last name should be between 1 and 16 characters")
    @Pattern(regexp = "^[a-zA-Z 1-9]*$", message = "Last name contains invalid characters")
    private String lastName;

    @Size(min = 13, max = 13, message = "CNP should be 13 characters long")
    @Pattern(regexp = "^[0-9]*$", message = "CNP contains invalid characters")
    @Column(nullable = false, unique = true)
    private String cnp;

    @Email(message = "Invalid email")
    private String email;

    private String imgName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = true)
    private Group group;

    @ManyToMany(cascade = {/*CascadeType.PERSIST, CascadeType.MERGE*/})
    @JoinTable(
            name = "tb_student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
//    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<Course> courses;

}
