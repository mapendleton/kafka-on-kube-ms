package com.gapinc.seri.restservice;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class GreetingControllerTest {
    
    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void get_all_success() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/greetings").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links.self.href").value("http://localhost/greetings?name=World"))
            .andExpect(jsonPath("$._embedded.greetingList", hasSize(3)))
            .andExpect(jsonPath("$._embedded.greetingList[?(@.id==1)].content").value("Hello, World!"))
            .andExpect(jsonPath("$._embedded.greetingList[?(@.id==2)].content").value("Hola, World!"))
            .andExpect(jsonPath("$._embedded.greetingList[?(@.id==3)].content").value("Howdy, World!"));
            //.andExpect(content().json("{\"id\":1,\"content\":\"Hello, World!\",\"_links\":{\"self\":{\"href\":\"http://localhost/greeting?name=World\"}}}"));
    }

    @Test
    public void get_one_greeting_name() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/greetings/1?name=Bob").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("Hello, Bob!"))
            .andExpect(jsonPath("$._links.self.href").value("http://localhost/greetings/1?name=Bob"));
    }

    @Test
    public void post_greeting() throws Exception {

        Greeting greeting = new Greeting(4, "Greetings, %s");
        mvc.perform(MockMvcRequestBuilders.post("/greetings").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(greeting)))
                .andExpect(status().isCreated());
            
    }

    @Test
    public void patch_greeting() throws Exception {

        mvc.perform(MockMvcRequestBuilders.patch("/greetings/1/boop%20bop").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.content").value("boop%20bop"));

    }

    @Test
    public void delete_greeting() throws Exception {

        mvc.perform(MockMvcRequestBuilders.delete("/greetings/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    public void value_not_found() throws Exception {
        
        mvc.perform(MockMvcRequestBuilders.get("/greetings/5").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void id_exists_already() throws Exception {
        Greeting greeting = new Greeting(1, "Greetings, %s");
        
        mvc.perform(MockMvcRequestBuilders.post("/greetings").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(greeting)))
                .andExpect(status().isConflict());
    }
}
