package io.pivotal.pal.tracker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@RestController("/users")
public class FirstWorkingMembersController {
    private final ICloudController controller;
    public FirstWorkingMembersController(ICloudController cloudController){
        controller = cloudController;
    }
    //@GetMapping
    public ResponseEntity<List<String>> list(){
        //controller.createUser(null,null);

        return new ResponseEntity<>(controller.getOrgUsers(), HttpStatus.OK);
    }
}
