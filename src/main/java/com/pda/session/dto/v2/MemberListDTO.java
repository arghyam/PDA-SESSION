package com.pda.session.dto.v2;

import java.util.List;

public class MemberListDTO {
    private String name;
    private String phoneNumber;
    private String countryCode;
    private String emailId;
    private String scanInTime;
    private String scanOutTime;
    private Long sessionid;
    private List<String> role;
    private String userId;
    private String photo;
    private boolean attestationGenerated;

    public MemberListDTO() {
    }

    public MemberListDTO(String name, String phoneNumber, String countryCode, String emailId, String scanInTime, String scanOutTime, Long sessionid, List<String> role, String userId, String photo, boolean attestationGenerated) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.countryCode = countryCode;
        this.emailId = emailId;
        this.scanInTime = scanInTime;
        this.scanOutTime = scanOutTime;
        this.sessionid = sessionid;
        this.role = role;
        this.userId = userId;
        this.photo = photo;
        this.attestationGenerated = attestationGenerated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
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

    public Long getSessionid() {
        return sessionid;
    }

    public void setSessionid(Long sessionid) {
        this.sessionid = sessionid;
    }

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isAttestationGenerated() {
        return attestationGenerated;
    }

    public void setAttestationGenerated(boolean attestationGenerated) {
        this.attestationGenerated = attestationGenerated;
    }
}
