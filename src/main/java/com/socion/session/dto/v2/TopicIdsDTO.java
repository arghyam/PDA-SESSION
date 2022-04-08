package com.socion.session.dto.v2;

import java.math.BigInteger;
import java.util.List;

public class TopicIdsDTO {
    // This change had to be done since the API using the DTO was broken and this is part of the fix.
    List<Long> topicIds;

    public List<Long> getTopicIds() {
        return topicIds;
    }

    public void setTopicIds(List<Long> topicIds) {
        this.topicIds = topicIds;
    }
}
