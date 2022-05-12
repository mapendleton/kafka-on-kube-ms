package com.gapinc.seri.restservice;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class GreetingController {

    //private final GreetingModelAssembler assembler;
    private final GreetingModelAssembler assembler;

    //in place of db as ex
    private Map<Integer,String> greeting_dict = Map.of(
        1,"Hello, %s!",
        2,"Hola, %s!",
        3,"Howdy, %s!"
    );

    GreetingController(GreetingModelAssembler assembler){
        this.assembler = assembler;
    }

    @GetMapping("/greetings")
    public HttpEntity<CollectionModel<EntityModel<Greeting>>> all(@RequestParam(value = "name", defaultValue = "World") String name) {

        List<EntityModel<Greeting>> greetings = new ArrayList<>();

        for (Entry<Integer, String> entry : greeting_dict.entrySet()) {
            greetings.add(
                assembler.toModel(
                    new Greeting(entry.getKey(),String.format(entry.getValue(),name)),
                    name
                )
            );
        }

        return new ResponseEntity<>(CollectionModel.of(greetings,linkTo(methodOn(GreetingController.class).all(name)).withSelfRel()),HttpStatus.OK);
    }

    @GetMapping("/greetings/{id}")
    public HttpEntity<EntityModel<Greeting>> one(@PathVariable Integer id,@RequestParam(value = "name", defaultValue = "World") String name)
    throws ResponseStatusException {
        if (!greeting_dict.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("greeting with id: %s not found.",id));
        }

        Greeting greeting = new Greeting(id,String.format(greeting_dict.get(id),name));

        return new ResponseEntity<>(assembler.toModel(greeting,name),HttpStatus.OK);
    }

    @PostMapping("/greetings")
    public ResponseEntity<?> newGreeting(@RequestBody Greeting newGreeting){
        Integer id = newGreeting.getId();
        if (greeting_dict.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("greeting with id: %s already exists.",id));
        }

        //Doesn't actually save anything right now, but you'd save it here
        EntityModel<Greeting> entityModel = assembler.toModel(newGreeting);

        return ResponseEntity
            .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(entityModel);
    }

    @PatchMapping("/greetings/{id}/{content}")
    public ResponseEntity<?> updateGreeting(@PathVariable Integer id,@PathVariable String content) {
        //get Greeting greeting from a db somewhere
        Greeting greeting = new Greeting(id,greeting_dict.get(id));
        //update greeting
        greeting.setContent(content);
        //save greeting in db
        return new ResponseEntity<>(assembler.toModel(greeting),HttpStatus.OK);
    }

    @DeleteMapping("/greetings/{id}")
    public ResponseEntity<?> deleteGreeting(@PathVariable Integer id){
        //delete from db or wherever...here
        return ResponseEntity.noContent().build();
    }

}