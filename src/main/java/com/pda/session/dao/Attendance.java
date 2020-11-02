package com.pda.session.dao;

import javax.persistence.*;


@Entity
@Table(name = "Attendance")
public class Attendance extends BaseEntity {

    @Id
    @SequenceGenerator(name = "attendance_id_seq", sequenceName = "attendance_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendance_id_seq")
    private Long id;
    private Long sessionId;
    private String userId;
    private String role;
    private Boolean isScanOut;
    private Boolean isScanIn;
    private String scanInDateTime;
    private String scanOutDateTime;
    private String attestationUrl;
    private boolean deleted;

    public Attendance() {
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getScanInDateTime() {
        return scanInDateTime;
    }

    public void setScanInDateTime(String scanInDateTime) {
        this.scanInDateTime = scanInDateTime;
    }

    public String getScanOutDateTime() {
        return scanOutDateTime;
    }

    public void setScanOutDateTime(String scanOutDateTime) {
        this.scanOutDateTime = scanOutDateTime;
    }

    public Boolean getScanOut() {
        return isScanOut;
    }

    public void setScanOut(Boolean scanOut) {
        isScanOut = scanOut;
    }

    public Boolean getScanIn() {
        return isScanIn;
    }

    public void setScanIn(Boolean scanIn) {
        isScanIn = scanIn;
    }


}
