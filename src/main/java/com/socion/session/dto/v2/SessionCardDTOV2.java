package com.socion.session.dto.v2;

public class SessionCardDTOV2 {

    private Long sessionId;
    private String sessionName;
    private String sessionDescription;
    private int sessionProgress;
    private String sessionStartDate;
    private String sessionEndDate;

    public SessionCardDTOV2() {
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionDescription() {
        return sessionDescription;
    }

    public void setSessionDescription(String sessionDescription) {
        this.sessionDescription = sessionDescription;
    }

    public int getSessionProgress() {
        return sessionProgress;
    }

    public void setSessionProgress(int sessionProgress) {
        this.sessionProgress = sessionProgress;
    }

    public String getSessionStartDate() {
        return sessionStartDate;
    }

    public void setSessionStartDate(String sessionStartDate) {
        this.sessionStartDate = sessionStartDate;
    }

    public String getSessionEndDate() {
        return sessionEndDate;
    }

    public void setSessionEndDate(String sessionEndDate) {
        this.sessionEndDate = sessionEndDate;
    }

}
