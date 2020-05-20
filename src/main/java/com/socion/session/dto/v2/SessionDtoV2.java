package com.socion.session.dto.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionDtoV2 {

    private Long sessionId;
    @NotNull
    @NotEmpty
    private String sessionName;
    private String sessionDescription;
    @NotNull
    @NotEmpty
    private String address;
    private List<MemberDtoV2> members;
    @NotNull
    @NotEmpty
    private String sessionStartDateTime;
    @NotNull
    @NotEmpty
    private String sessionEndDateTime;
    private String sessionCreator;
    @NotNull
    private Long topicId;
    private int sessionProgress;
    private Boolean isSessionCreator;
    private int SessionStatus;
    private String attestationDate;
    private String attestationUrl;
    private String memberAttestationUrl;
    private String traineeAttestationUrl;
    private TopicInfo topicInfo;
    private String role;
    private String qrCodeUrl;
    private String trainingOrganization;
    private String programName;
    public int numberOfParticipants;
    private TopicInfo detailWithProgramContentDTO;
    private User user;
    private String sessionTimeZone;
    private List<SessionLinksDTO> sessionLinks;


    public String getSessionTimeZone() {
        return sessionTimeZone;
    }

    public void setSessionTimeZone(String sessionTimeZone) {
        this.sessionTimeZone = sessionTimeZone;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }


    public List<MemberDtoV2> getMembers() {
        return members;
    }

    public void setMembers(List<MemberDtoV2> members) {
        this.members = members;
    }

    public String getAttestationDate() {
        return attestationDate;
    }

    public void setAttestationDate(String attestationDate) {
        this.attestationDate = attestationDate;
    }

    public int getSessionStatus() {
        return SessionStatus;
    }

    public void setSessionStatus(int sessionStatus) {
        SessionStatus = sessionStatus;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTrainingOrganization() {
        return trainingOrganization;
    }

    public void setTrainingOrganization(String trainingOrganization) {
        this.trainingOrganization = trainingOrganization;
    }


    public TopicInfo getDetailWithProgramContentDTO() {
        return detailWithProgramContentDTO;
    }

    public void setDetailWithProgramContentDTO(TopicInfo detailWithProgramContentDTO) {
        this.detailWithProgramContentDTO = detailWithProgramContentDTO;
    }

    public String getSessionStartDateTime() {
        return sessionStartDateTime;
    }

    public void setSessionStartDateTime(String sessionStartDateTime) {
        this.sessionStartDateTime = sessionStartDateTime;
    }

    public String getSessionEndDateTime() {
        return sessionEndDateTime;
    }

    public void setSessionEndDateTime(String sessionEndDateTime) {
        this.sessionEndDateTime = sessionEndDateTime;
    }

    public SessionDtoV2() {
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


    public String getSessionCreator() {
        return sessionCreator;
    }


    public void setSessionCreator(String sessionCreator) {
        this.sessionCreator = sessionCreator;
    }


    public int getSessionProgress() {
        return sessionProgress;
    }


    public void setSessionProgress(int sessionProgress) {
        this.sessionProgress = sessionProgress;
    }


    public Boolean getIsSessionCreator() {
        return isSessionCreator;
    }


    public void setIsSessionCreator(Boolean isSessionCreator) {
        this.isSessionCreator = isSessionCreator;
    }


    public String getAttestationUrl() {
        return attestationUrl;
    }

    public void setAttestationUrl(String attestationUrl) {
        this.attestationUrl = attestationUrl;
    }

    public String getMemberAttestationUrl() {
        return memberAttestationUrl;
    }

    public void setMemberAttestationUrl(String memberAttestationUrl) {
        this.memberAttestationUrl = memberAttestationUrl;
    }

    public String getTraineeAttestationUrl() {
        return traineeAttestationUrl;
    }

    public void setTraineeAttestationUrl(String traineeAttestationUrl) {
        this.traineeAttestationUrl = traineeAttestationUrl;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public TopicInfo getTopicInfo() {
        return topicInfo;
    }

    public void setTopicInfo(TopicInfo topicInfo) {
        this.topicInfo = topicInfo;
    }


    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }


    public SessionDtoV2(Long sessionId, @NotNull @NotEmpty String sessionName, String sessionDescription, @NotNull @NotEmpty String address, List<MemberDtoV2> members, @NotNull @NotEmpty String sessionStartDateTime, @NotNull @NotEmpty String sessionEndDateTime, String sessionCreator, @NotNull Long topicId, int sessionProgress, Boolean isSessionCreator, int sessionStatus, String attestationDate, String attestationUrl, String memberAttestationUrl, String traineeAttestationUrl, TopicInfo topicInfo, String role, String qrCodeUrl, String trainingOrganization, String programName, int numberOfParticipants, TopicInfo detailWithProgramContentDTO, User user) {
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.sessionDescription = sessionDescription;
        this.address = address;
        this.members = members;
        this.sessionStartDateTime = sessionStartDateTime;
        this.sessionEndDateTime = sessionEndDateTime;
        this.sessionCreator = sessionCreator;
        this.topicId = topicId;
        this.sessionProgress = sessionProgress;
        this.isSessionCreator = isSessionCreator;
        SessionStatus = sessionStatus;
        this.attestationDate = attestationDate;
        this.attestationUrl = attestationUrl;
        this.memberAttestationUrl = memberAttestationUrl;
        this.traineeAttestationUrl = traineeAttestationUrl;
        this.topicInfo = topicInfo;
        this.role = role;
        this.qrCodeUrl = qrCodeUrl;
        this.trainingOrganization = trainingOrganization;
        this.programName = programName;
        this.numberOfParticipants = numberOfParticipants;
        this.detailWithProgramContentDTO = detailWithProgramContentDTO;
        this.user = user;
    }


    public List<SessionLinksDTO> getSessionLinks() {
        return sessionLinks;
    }

    public void setSessionLinks(List<SessionLinksDTO> sessionLinks) {
        this.sessionLinks = sessionLinks;
    }

    public void setSessionCreator(Boolean sessionCreator) {
        isSessionCreator = sessionCreator;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(int numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }
}