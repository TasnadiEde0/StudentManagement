package org.learning.studentManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_course")
public class Course extends BaseObject {
    @Size(min = 4, max = 32, message = "Course name should be between 4 and 32 characters")
    @Pattern(regexp = "^[a-zA-Z 1-9]*$", message = "Course name contains invalid characters")
    @Column(unique = true)
    private String name;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @ManyToMany(mappedBy = "courses")
//    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<Student> students;

}
