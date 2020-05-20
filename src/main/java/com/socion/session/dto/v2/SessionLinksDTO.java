package com.socion.session.dto.v2;

public class SessionLinksDTO {

    private Long id;
    private Long sessionId;
    private String sessionUrl;

    public SessionLinksDTO(Long id, Long sessionId, String sessionUrl) {
        this.id = id;
        this.sessionId = sessionId;
        this.sessionUrl = sessionUrl;
    }

    public SessionLinksDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionUrl() {
        return sessionUrl;
    }

    public void setSessionUrl(String sessionUrl) {
        this.sessionUrl = sessionUrl;
    }
}
