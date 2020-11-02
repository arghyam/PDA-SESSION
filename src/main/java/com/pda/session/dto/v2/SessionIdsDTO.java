package com.pda.session.dto.v2;

import java.util.List;

public class SessionIdsDTO {
    private List<Long> sessionIds;

    public SessionIdsDTO() {
    }

    public List<Long> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(List<Long> sessionIds) {
        this.sessionIds = sessionIds;
    }
}
