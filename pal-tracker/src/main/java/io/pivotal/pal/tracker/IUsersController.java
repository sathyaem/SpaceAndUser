package io.pivotal.pal.tracker;

import java.util.List;
import java.util.Map;

public interface IUsersController {
    List<String> listUsers(String space);

    boolean createUser(String spaceName);
    boolean deleteUser(String spaceName);
    boolean assignSpaceToUser(String spaceName, String userName);
    //Map createUserUsingUAA(String userName);
}
