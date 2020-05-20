package com.socion.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


public class AttendanceDTO implements Serializable {
    private Long sessionId;
    @JsonProperty("isScanIn")
    private boolean ScanIn;
    private String date;
    private String time;
    private String latitude;
    private String longitude;
    private String ipAddress;
    @JsonProperty("QRtype")
    private String qrType;
    @JsonProperty("offline")
    private Boolean isOffline;

    public AttendanceDTO() {
    }

    public boolean isScanIn() {
        return ScanIn;
    }

    public void setScanIn(boolean scanIn) {
        ScanIn = scanIn;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Boolean getOffline() {
        return isOffline;
    }

    public void setOffline(Boolean offline) {
        isOffline = offline;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getQrType() {
        return qrType;
    }

    public void setQrType(String qrType) {
        this.qrType = qrType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}
