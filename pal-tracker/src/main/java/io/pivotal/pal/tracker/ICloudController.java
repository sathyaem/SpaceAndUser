package io.pivotal.pal.tracker;

import java.util.List;

public interface ICloudController {
    List<String> getSpaces();
    boolean spaceExists(String spaceName);
    List<String> getUsers();
    boolean userExists(String userName);

    boolean createSpace(String spaceName, List<String> roles );
    boolean deleteSpace(String spaceName);

    boolean createUser(String userName, List<String> spaces);
    boolean deleteUser(String userName);

    List<String> getApps();

    List<String> getOrgUsers();

}
