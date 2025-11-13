package org.learning.studentManagement;

import lombok.extern.slf4j.Slf4j;
import org.learning.openApiTest.rest.ApiClient;
import org.learning.openApiTest.rest.ApiException;
import org.learning.openApiTest.rest.api.DefaultApi;
import org.learning.openApiTest.rest.model.StudentObj;

import java.util.List;

@Slf4j
public class ClientMain {
    public static void main(String[] args) throws ApiException {
        ApiClient client = new ApiClient();
        client.setConnectTimeout(600000);
        client.setReadTimeout(600000);
        client.setWriteTimeout(600000);
        DefaultApi api = new DefaultApi(client);

        StudentObj studentObj = Utils.createStudentObj(
                "0",
                "firstName",
                "lastName",
                "1231231231231",
                "email@email.email",
                "pic.png",
                "2",
                List.of()
        );

        try {
            StudentObj savedStudentObj = api.studentPost(studentObj);

            log.info("{}", savedStudentObj);

        }
        catch (ApiException e) {
            log.info("{}", e.getResponseBody());
            log.info("{}", e.getCode());
            log.info("{}", e.getResponseHeaders());
            throw e;
        }
    }
}
