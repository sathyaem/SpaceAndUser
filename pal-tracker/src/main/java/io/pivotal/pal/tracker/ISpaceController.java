package io.pivotal.pal.tracker;

import java.util.List;

public interface ISpaceController {
    List<String> listSpaces();

    boolean createSpace(String space);

    boolean deleteSpace(String space);

    boolean renameSpace(String newSpaceName, String oldSpaceName);
}
