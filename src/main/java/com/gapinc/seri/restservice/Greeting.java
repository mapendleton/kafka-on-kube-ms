package com.gapinc.seri.restservice;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Greeting extends RepresentationModel<Greeting> {
    private final Integer id;
	private String content;

    @JsonCreator
	public Greeting(@JsonProperty("id") Integer id, @JsonProperty("content") String content) {
		this.id = id;
		this.content = content;
	}

	public Integer getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

    public void setContent(String content){
        this.content = content;
    }
}
