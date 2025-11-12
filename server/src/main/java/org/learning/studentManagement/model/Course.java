package org.learning.studentManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    @JsonIgnore
    @ManyToMany(mappedBy = "courses")
//    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<Student> students;

    @Override
    public String toString() {
        String studentList = "";
        if (students != null) {
            studentList = students.stream().map(student -> student.getFirstName() + " " +
                    student.getLastName()).collect(Collectors.joining(", "));
        }
        return "Course(name=" + name + ", startDate=" + startDate + ", endDate=" +
                endDate + ", students=" + studentList + ")";
    }

}
