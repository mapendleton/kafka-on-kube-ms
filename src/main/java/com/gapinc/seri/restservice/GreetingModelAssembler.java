package com.gapinc.seri.restservice;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class GreetingModelAssembler implements RepresentationModelAssembler<Greeting, EntityModel<Greeting>>{
    @Override
    public EntityModel<Greeting> toModel(Greeting greeting){
        return EntityModel.of(greeting,
            linkTo(methodOn(GreetingController.class).one(greeting.getId(),null)).withSelfRel(),
            linkTo(methodOn(GreetingController.class).all(null)).withRel("greetings"));
    }

    public EntityModel<Greeting> toModel(Greeting greeting, String name){
        return EntityModel.of(greeting,
            linkTo(methodOn(GreetingController.class).one(greeting.getId(),name)).withSelfRel(),
            linkTo(methodOn(GreetingController.class).all(name)).withRel("greetings"));
    }
}
