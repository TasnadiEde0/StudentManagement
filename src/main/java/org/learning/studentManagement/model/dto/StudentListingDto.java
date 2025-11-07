package org.learning.studentManagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentListingDto {
    List<StudentDto> students;
    int pageCount;
    List<GroupDto> groups;
}
