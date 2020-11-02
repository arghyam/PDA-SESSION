package com.pda.session.dto.v2;

public class Attestations {

    private String userId;
    private String role;
    private String scanInDateTime;
    private String scanOutDateTime;
    private String attestationUrl;
    private boolean deleted;
    private int entityId;
    private String entityName;
    private String programName;
    private String sessionName;
    private String scanInSessionLat;
    private String scanInSessionLon;
    private String scanOutSessionLat;
    private String scanOutSessionLon;
    private String contentS3Url;
    private String typeOfAttestation;
    private int noOfParticipants;

    public Attestations(String userId, String role, String scanInDateTime, String scanOutDateTime, String attestationUrl, boolean deleted) {
        this.userId = userId;
        this.role = role;
        this.scanInDateTime = scanInDateTime;
        this.scanOutDateTime = scanOutDateTime;
        this.attestationUrl = attestationUrl;
        this.deleted = deleted;
    }

    public int getNoOfParticipants() {
        return noOfParticipants;
    }

    public void setNoOfParticipants(int noOfParticipants) {
        this.noOfParticipants = noOfParticipants;
    }

    public String getTypeOfAttestation() {
        return typeOfAttestation;
    }

    public void setTypeOfAttestation(String typeOfAttestation) {
        this.typeOfAttestation = typeOfAttestation;
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

    public String getAttestationUrl() {
        return attestationUrl;
    }

    public void setAttestationUrl(String attestationUrl) {
        this.attestationUrl = attestationUrl;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getScanInSessionLat() {
        return scanInSessionLat;
    }

    public void setScanInSessionLat(String scanInSessionLat) {
        this.scanInSessionLat = scanInSessionLat;
    }

    public String getScanInSessionLon() {
        return scanInSessionLon;
    }

    public void setScanInSessionLon(String scanInSessionLon) {
        this.scanInSessionLon = scanInSessionLon;
    }

    public String getScanOutSessionLat() {
        return scanOutSessionLat;
    }

    public void setScanOutSessionLat(String scanOutSessionLat) {
        this.scanOutSessionLat = scanOutSessionLat;
    }

    public String getScanOutSessionLon() {
        return scanOutSessionLon;
    }

    public void setScanOutSessionLon(String scanOutSessionLon) {
        this.scanOutSessionLon = scanOutSessionLon;
    }

    public String getContentS3Url() {
        return contentS3Url;
    }

    public void setContentS3Url(String contentS3Url) {
        this.contentS3Url = contentS3Url;
    }
}
