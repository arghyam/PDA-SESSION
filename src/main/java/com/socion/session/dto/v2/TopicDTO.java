package com.socion.session.dto.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TopicDTO implements Serializable {

    private Long id;
    private String name;
    private String description;
    private Long programId;
    private Boolean sessionLinked;

    public TopicDTO() {
    }

    public Boolean getSessionLinked() {
        return sessionLinked;
    }

    public void setSessionLinked(Boolean sessionLinked) {
        this.sessionLinked = sessionLinked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }
}
