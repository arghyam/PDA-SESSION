package com.socion.session.dto.v2;

import java.math.BigInteger;
import java.util.List;

public class TopicIdsDTO {
    List<BigInteger> topicIds;

    public List<BigInteger> getTopicIds() {
        return topicIds;
    }

    public void setTopicIds(List<BigInteger> topicIds) {
        this.topicIds = topicIds;
    }
}
