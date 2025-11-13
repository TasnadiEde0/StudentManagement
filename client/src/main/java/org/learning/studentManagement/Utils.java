package org.learning.studentManagement;

import org.learning.openApiTest.rest.model.StudentObj;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Utils {
    private static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads/imgs";

    public static StudentObj createStudentObj(
            String id,
            String firstName,
            String lastName,
            String cnp,
            String email,
            String imgName,
            String groupId,
            List<String> courseIds
    ){
        StudentObj studentObj = new StudentObj();
        studentObj.setId(id);
        studentObj.setFirstName(firstName);
        studentObj.setLastName(lastName);
        studentObj.setEmail(email);
        studentObj.setCnp(cnp);
        studentObj.setCourseIds(courseIds);
        studentObj.setGroupId(groupId);
        studentObj.setImgName(imgName);

        return studentObj;
    }
}
