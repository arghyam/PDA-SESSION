package com.socion.session.dto.v2;

public class ParticipantListDTO {
    private String name;
    private String phoneNumber;
    private String countryCode;
    private String emailId;
    private String scanInTime;
    private String scanOutTime;
    private Long sessionid;
    private String role;


    public Long getSessionid() {
        return sessionid;
    }

    public void setSessionid(Long sessionid) {
        this.sessionid = sessionid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }



    public ParticipantListDTO() {
    }

    private boolean attestationGenerated;


    public ParticipantListDTO(String name, String phoneNumber, String countryCode, String emailId, String scanInTime, String scanOutTime, Long sessionid, String role,boolean attestationGenerated) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.countryCode = countryCode;
        this.emailId = emailId;
        this.scanInTime = scanInTime;
        this.scanOutTime = scanOutTime;
        this.sessionid = sessionid;
        this.role = role;
        this.attestationGenerated = attestationGenerated;
    }


    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
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

    public boolean isAttestationGenerated() {
        return attestationGenerated;
    }

    public void setAttestationGenerated(boolean attestationGenerated) {
        this.attestationGenerated = attestationGenerated;
    }

}
