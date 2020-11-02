package com.pda.session.dto;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


public class SessionDTO {

    private Long sessionId;
    @NotNull(message = "Session Name is Mandatory")
    @NotEmpty
    private String sessionName;
    private String sessionDescription;
    private String address;
    private String trainingOrganization;
    private List<Member> members;
    private String sessionStartDate;
    private String sessionEndDate;
    private String memberAttestation;
    private String traineeAttestation;
    private String sessionCreator;
    private String sessionStatus;
    private int sessionProgress;

    public SessionDTO() {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTrainingOrganization() {
        return trainingOrganization;
    }

    public void setTrainingOrganization(String trainingOrganization) {
        this.trainingOrganization = trainingOrganization;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
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

    public String getMemberAttestation() {
        return memberAttestation;
    }

    public void setMemberAttestation(String memberAttestation) {
        this.memberAttestation = memberAttestation;
    }

    public String getTraineeAttestation() {
        return traineeAttestation;
    }

    public void setTraineeAttestation(String traineeAttestation) {
        this.traineeAttestation = traineeAttestation;
    }

    public String getSessionCreator() {
        return sessionCreator;
    }

    public void setSessionCreator(String sessionCreator) {
        this.sessionCreator = sessionCreator;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public int getSessionProgress() {
        return sessionProgress;
    }

    public void setSessionProgress(int sessionProgress) {
        this.sessionProgress = sessionProgress;
    }
}
