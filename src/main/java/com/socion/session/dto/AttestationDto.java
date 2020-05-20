package com.socion.session.dto;

public class AttestationDto {

    private Long sessionId;
    private String sessionName;
    private String organizationName;
    private String address;
    private String attestationDate;
    private String qrCodeURL;
    private String attestationUrl;
    private String role;

    public AttestationDto() {
    }

    public AttestationDto(Long sessionId, String sessionName, String organizationName, String address, String attestationDate, String qrCodeURL, String attestationUrl, String role) {
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.organizationName = organizationName;
        this.address = address;
        this.attestationDate = attestationDate;
        this.qrCodeURL = qrCodeURL;
        this.attestationUrl = attestationUrl;
        this.role = role;
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

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAttestationDate() {
        return attestationDate;
    }

    public void setAttestationDate(String attestationDate) {
        this.attestationDate = attestationDate;
    }

    public String getQrCodeURL() {
        return qrCodeURL;
    }

    public void setQrCodeURL(String qrCodeURL) {
        this.qrCodeURL = qrCodeURL;
    }

    public String getAttestationUrl() {
        return attestationUrl;
    }

    public void setAttestationUrl(String attestationUrl) {
        this.attestationUrl = attestationUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
