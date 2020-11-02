package com.pda.session.dto.v2;

public class SessionidsattestationcountDTO {
    private Long sessionId;
    private Long attestationCount;
    private Long membersCount;
    private Long participantCount;

    public SessionidsattestationcountDTO() {
    }

    public Long getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(Long participantCount) {
        this.participantCount = participantCount;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getAttestationCount() {
        return attestationCount;
    }

    public void setAttestationCount(Long attestationCount) {
        this.attestationCount = attestationCount;
    }

    public Long getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(Long membersCount) {
        this.membersCount = membersCount;
    }
}
