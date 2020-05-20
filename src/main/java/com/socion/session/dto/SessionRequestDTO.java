package com.socion.session.dto;

public class SessionRequestDTO {

    private SessionDTO sessionDTO;

    public SessionDTO getSessionDTO() {
        return sessionDTO;
    }

    public SessionRequestDTO() {
    }

    public void setSessionDTO(SessionDTO sessionDTO) {
        this.sessionDTO = sessionDTO;
    }
}