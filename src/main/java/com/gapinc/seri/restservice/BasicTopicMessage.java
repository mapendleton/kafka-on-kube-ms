package com.gapinc.seri.restservice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.hateoas.RepresentationModel;

public class BasicTopicMessage extends RepresentationModel<BasicTopicMessage> {
    private final Integer id;
	private String content;
    
    @JsonCreator
	public BasicTopicMessage(@JsonProperty("id") Integer id, @JsonProperty("content") String content) {
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
