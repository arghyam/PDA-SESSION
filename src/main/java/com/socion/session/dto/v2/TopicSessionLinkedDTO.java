package com.socion.session.dto.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TopicSessionLinkedDTO implements Serializable {

    @NotEmpty
    @NotNull
    private Long topicId;

    @NotEmpty
    @NotNull
    private boolean SessionLinked;

    public TopicSessionLinkedDTO() {
    }

    public TopicSessionLinkedDTO(@NotEmpty @NotNull Long topicId, @NotEmpty @NotNull boolean sessionLinked) {
        this.topicId = topicId;
        SessionLinked = sessionLinked;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public boolean isSessionLinked() {
        return SessionLinked;
    }

    public void setSessionLinked(boolean sessionLinked) {
        SessionLinked = sessionLinked;
    }
}
