package com.socion.session.dto;

public class SessionResponseDTO {

    private Long sessionId;

    public SessionResponseDTO() {
    }

    public SessionResponseDTO(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}
