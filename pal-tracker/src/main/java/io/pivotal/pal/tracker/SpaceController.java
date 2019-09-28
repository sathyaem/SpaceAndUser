package io.pivotal.pal.tracker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spaces")
public class SpaceController {
    private final ISpaceController controller;
    public SpaceController(ISpaceController spaceController){
        controller = spaceController;
    }

    @GetMapping
    public ResponseEntity<List<String>> list(){
        List<String> spaceList=controller.listSpaces();
        if(spaceList!=null)
        return new ResponseEntity<>(spaceList, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping
    public ResponseEntity createSpace(@RequestBody String space)
    {
        try {
            if (controller.createSpace(space)) {
                return new ResponseEntity<>(HttpStatus.CREATED);
            } else return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity deleteSpace(@RequestBody String space)
    {
        try {
            controller.deleteSpace(space);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        catch(Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("{oldSpaceName}")
    public ResponseEntity updateSpace(@PathVariable String oldSpaceName, @RequestBody String newSpaceName)
    {
        try {
            controller.renameSpace(newSpaceName,oldSpaceName);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        catch(Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
