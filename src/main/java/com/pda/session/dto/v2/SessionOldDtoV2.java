package com.pda.session.dto.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionOldDtoV2 {
    private Long sessionId;
    @NotNull(message = "Session Name is Mandatory")
    @NotEmpty
    private String sessionName;
    private String sessionDescription;
    private String address;
    private List<MemberOldDtoV2> members;
    private List<SessionLinksDTO> sessionLinks;
    private String sessionStartDate;
    private String sessionEndDate;
    private String sessionCreator;
    private Long topicId;
    private TopicInfo topicInfo;
    private int sessionProgress;
    private Boolean isSessionCreator;
    private String attestationUrl;
    private String attestationDate;
    private String memberAttestationUrl;
    private String traineeAttestationUrl;
    private String qrCodeUrl;
    private String role;
    public int numberOfParticipants;
    private User user;
    private String programName;
    private boolean isMember;
    private User sessionCreatorProfile;
    private String startQrcode;
    private String endQrcode;
    private String sessionTimeZone;

    public String getSessionTimeZone() {
        return sessionTimeZone;
    }

    public void setSessionTimeZone(String sessionTimeZone) {
        this.sessionTimeZone = sessionTimeZone;
    }

    public String getStartQrcode() {
        return startQrcode;
    }

    public void setStartQrcode(String startQrcode) {
        this.startQrcode = startQrcode;
    }

    public String getEndQrcode() {
        return endQrcode;
    }

    public void setEndQrcode(String endQrcode) {
        this.endQrcode = endQrcode;
    }

    public TopicInfo getTopicInfo() {
        return topicInfo;
    }

    public void setTopicInfo(TopicInfo topicInfo) {
        this.topicInfo = topicInfo;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean member) {
        isMember = member;
    }

    public User getSessionCreatorProfile() {
        return sessionCreatorProfile;
    }

    public void setSessionCreatorProfile(User sessionCreatorProfile) {
        this.sessionCreatorProfile = sessionCreatorProfile;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public SessionOldDtoV2() {
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

    public void setIsSessionCreator(boolean isSessionCreator) {
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


    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public void setSessionCreator(boolean sessionCreator) {
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAttestationDate() {
        return attestationDate;
    }


    public void setAttestationDate(String attestationDate) {
        this.attestationDate = attestationDate;
    }

    public Boolean isSessionCreator() {
        return isSessionCreator;
    }

    public List<MemberOldDtoV2> getMembers() {
        return members;
    }

    public void setMembers(List<MemberOldDtoV2> members) {
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


}