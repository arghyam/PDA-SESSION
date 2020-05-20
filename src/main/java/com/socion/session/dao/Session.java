package com.socion.session.dao;

import javax.persistence.*;

@Entity
@Table(name = "Session")
public class Session extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    @Column(length = 100000)
    private String sessionName;
    private String programName;
    @Column(length = 100000)
    private String sessionDescription;
    private String address;
    private String trainingOrganization;
    private String sessionCreator;
    private Long topicId;
    private Long programId;
    private String sessionStartDate;
    private String sessionEndDate;
    private Boolean is_deleted;
    @Column(length = 100000)
    private String startQrcode;
    @Column(length = 100000)
    private String endQrcode;
    private String sessionTimeZone;
    private String sessionEndDateUtcTime;

    public Session() {
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

    public String getSessionTimeZone() {
        return sessionTimeZone;
    }

    public void setSessionTimeZone(String sessionTimeZone) {
        this.sessionTimeZone = sessionTimeZone;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getSessionCreator() {
        return sessionCreator;
    }

    public void setSessionCreator(String sessionCreator) {
        this.sessionCreator = sessionCreator;
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

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public String getSessionEndDateUtcTime() {
        return sessionEndDateUtcTime;
    }

    public void setSessionEndDateUtcTime(String sessionEndDateUtcTime) {
        this.sessionEndDateUtcTime = sessionEndDateUtcTime;
    }


}
