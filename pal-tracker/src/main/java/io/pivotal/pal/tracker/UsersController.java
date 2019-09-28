package io.pivotal.pal.tracker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final IUsersController usersContext;
    private final ISpaceController spacesContext;
    public UsersController(ISpaceController spaceController, IUsersController usersController){
        usersContext = usersController;
        spacesContext = spaceController;
    }

    @GetMapping("{spaceName}")
    public ResponseEntity<List<String>> list(@PathVariable String spaceName){

        return new ResponseEntity<>(usersContext.listUsers(spaceName), HttpStatus.OK);
    }

/*    @PostMapping("{userName}")
    public ResponseEntity createUser(@PathVariable String userName){
        return new ResponseEntity<>(usersContext.createUser(userName), HttpStatus.OK);
    }*/

    @DeleteMapping("{userName}")
    public ResponseEntity deleteUser(@PathVariable String userName){
        return new ResponseEntity<>(usersContext.deleteUser(userName), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createSpaceAndUser(@RequestBody SpaceInfo spaceInfo)
    {
        //if(spacesContext.createSpace(spaceInfo.SpaceName)) {
        if(spacesContext.createSpace(spaceInfo.SpaceName)) {
            if(usersContext.assignSpaceToUser(spaceInfo.SpaceName, spaceInfo.UserName))
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
