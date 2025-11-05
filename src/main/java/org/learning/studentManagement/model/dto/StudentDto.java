package org.learning.studentManagement.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.learning.studentManagement.model.BaseObject;
import org.springframework.core.annotation.AliasFor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {
    private String id;

    private String firstName;

    private String lastName;

    private String cnp;

    private String email;

    private String imgName;

    private String groupName;

    private String groupId;

    private MultipartFile profilePic;

//    private List<String> courseIds;

}
