package com.pda.session.dto.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TopicInfo implements Serializable {

    private TopicDTO topic;
    private ProgramDTO program;
    private List<ContentDTO> content;
    private boolean deleted;

    public TopicInfo() {
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public TopicDTO getTopic() {
        return topic;
    }

    public void setTopic(TopicDTO topic) {
        this.topic = topic;
    }

    public ProgramDTO getProgram() {
        return program;
    }

    public void setProgram(ProgramDTO program) {
        this.program = program;
    }

    public List<ContentDTO> getContent() {
        return content;
    }

    public void setContent(List<ContentDTO> content) {
        this.content = content;
    }


}
