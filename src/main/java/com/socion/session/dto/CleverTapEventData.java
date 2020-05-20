package com.socion.session.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CleverTapEventData {

    private Long eventId;
    @NotNull
    @NotEmpty
    private String eventType;

    private String userId;
    private String timestamp;
    private String ipAddress;
    private Long programId;
    private String programName;
    private Long topicId;
    private String topicName;
    private Long sessionId;
    private String sessionName;
    private String sessionStartDate;
    private String sessionEndDate;
    private String sessionLat;
    private String sessionLon;
    private String memberId;
    private String contentFileName;
    private String sizeOfContent;
    private Long noOfParticipants;
    private String role;
    private Long notificationId;
    private String offlineEventTimestamp;
    private boolean offlineToOnlineSync;
    private String deviceType;
    private String osType;
    private String deviceId;
    private String appType;
    private String browserType;
    private String scanInTime;
    private String scanOutTime;

    private String pageResponseTime;
    private String pageLoadTime;
    private String internetSpeed;
    private String netWorkProvider;
    private String attestationUrl;
    private String training_organization;
    private boolean deleted;
    private Date updatedAt;
    private Date createdAt;

    public CleverTapEventData() {
    }

    public String getScanInTime() {
        return scanInTime;
    }

    public void setScanInTime(String scanInTime) {
        this.scanInTime = scanInTime;
    }

    public String getScanOutTime() {
        return scanOutTime;
    }

    public void setScanOutTime(String scanOutTime) {
        this.scanOutTime = scanOutTime;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }


    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getTraining_organization() {
        return training_organization;
    }

    public void setTraining_organization(String training_organization) {
        this.training_organization = training_organization;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
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

    public String getSessionLat() {
        return sessionLat;
    }

    public void setSessionLat(String sessionLat) {
        this.sessionLat = sessionLat;
    }

    public String getSessionLon() {
        return sessionLon;
    }

    public void setSessionLon(String sessionLon) {
        this.sessionLon = sessionLon;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getPageResponseTime() {
        return pageResponseTime;
    }

    public void setPageResponseTime(String pageResponseTime) {
        this.pageResponseTime = pageResponseTime;
    }

    public String getPageLoadTime() {
        return pageLoadTime;
    }

    public void setPageLoadTime(String pageLoadTime) {
        this.pageLoadTime = pageLoadTime;
    }

    public String getInternetSpeed() {
        return internetSpeed;
    }

    public void setInternetSpeed(String internetSpeed) {
        this.internetSpeed = internetSpeed;
    }

    public String getNetWorkProvider() {
        return netWorkProvider;
    }

    public void setNetWorkProvider(String netWorkProvider) {
        this.netWorkProvider = netWorkProvider;
    }

    public String getContentFileName() {
        return contentFileName;
    }

    public void setContentFileName(String contentFileName) {
        this.contentFileName = contentFileName;
    }

    public String getSizeOfContent() {
        return sizeOfContent;
    }

    public void setSizeOfContent(String sizeOfContent) {
        this.sizeOfContent = sizeOfContent;
    }

    public Long getNoOfParticipants() {
        return noOfParticipants;
    }

    public void setNoOfParticipants(Long noOfParticipants) {
        this.noOfParticipants = noOfParticipants;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAttestationUrl() {
        return attestationUrl;
    }

    public void setAttestationUrl(String attestationUrl) {
        this.attestationUrl = attestationUrl;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getOfflineEventTimestamp() {
        return offlineEventTimestamp;
    }

    public void setOfflineEventTimestamp(String offlineEventTimestamp) {
        this.offlineEventTimestamp = offlineEventTimestamp;
    }

    public Boolean getOfflineToOnlineSync() {
        return offlineToOnlineSync;
    }

    public void setOfflineToOnlineSync(Boolean offlineToOnlineSync) {
        this.offlineToOnlineSync = offlineToOnlineSync;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getBrowserType() {
        return browserType;
    }

    public void setBrowserType(String browserType) {
        this.browserType = browserType;
    }
}
