package org.learning.StudentManagement.model;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class Student extends BaseObject {
    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private String cnp;

    private String email;

}
