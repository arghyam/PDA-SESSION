package com.socion.session.dto;

public class TemplateDto {
    private Long sessionId;
    private String sessionName;
    private String sessionStartDate;
    private String sessionEndDate;
    private String trainingOrganization;
    private String profilePicPath;
    private String name;
    private String userRole;
    private String userId;
    private String programName;
    private int noOfParticipants;
    private String entityName;

    public TemplateDto(Long sessionId, String sessionName, String sessionStartDate, String sessionEndDate, String trainingOrganization, String profilePicPath, String name, String userRole, String userId, String programName, int noOfParticipants) {
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.sessionStartDate = sessionStartDate;
        this.sessionEndDate = sessionEndDate;
        this.trainingOrganization = trainingOrganization;
        this.profilePicPath = profilePicPath;
        this.name = name;
        this.userRole = userRole;
        this.userId = userId;
        this.programName = programName;
        this.noOfParticipants = noOfParticipants;
    }

    public TemplateDto(Long sessionId, String sessionName, String sessionStartDate, String sessionEndDate, String trainingOrganization, String profilePicPath, String name, String userRole, String userId, String programName, int noOfParticipants, String entityName) {
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.sessionStartDate = sessionStartDate;
        this.sessionEndDate = sessionEndDate;
        this.trainingOrganization = trainingOrganization;
        this.profilePicPath = profilePicPath;
        this.name = name;
        this.userRole = userRole;
        this.userId = userId;
        this.programName = programName;
        this.noOfParticipants = noOfParticipants;
        this.entityName = entityName;
    }

    public TemplateDto() {
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public int getNoOfParticipants() {
        return noOfParticipants;
    }

    public void setNoOfParticipants(int noOfParticipants) {
        this.noOfParticipants = noOfParticipants;
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

    public String getTrainingOrganization() {
        return trainingOrganization;
    }

    public void setTrainingOrganization(String trainingOrganization) {
        this.trainingOrganization = trainingOrganization;
    }

    public String getProfilePicPath() {
        return profilePicPath;
    }

    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }
}
