package com.pda.session.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Location")
public class Location extends BaseEntity{
    @Id
    @GeneratedValue
    private Long id;
    private String scanInLatitude;
    private String scanInLongitude;
    private String scanOutLatitude;
    private String scanOutLongitude;
    private Long sessionId;
    private String userId;
    private String role;

    public Location(String scanInLatitude, String scanInLongitude, Long sessionId, String userId, String role) {
        this.scanInLatitude = scanInLatitude;
        this.scanInLongitude = scanInLongitude;
        this.sessionId = sessionId;
        this.userId = userId;
        this.role = role;
    }

    public Location() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScanInLatitude() {
        return scanInLatitude;
    }

    public void setScanInLatitude(String scanInLatitude) {
        this.scanInLatitude = scanInLatitude;
    }

    public String getScanInLongitude() {
        return scanInLongitude;
    }

    public void setScanInLongitude(String scanInLongitude) {
        this.scanInLongitude = scanInLongitude;
    }

    public String getScanOutLatitude() {
        return scanOutLatitude;
    }

    public void setScanOutLatitude(String scanOutLatitude) {
        this.scanOutLatitude = scanOutLatitude;
    }

    public String getScanOutLongitude() {
        return scanOutLongitude;
    }

    public void setScanOutLongitude(String scanOutLongitude) {
        this.scanOutLongitude = scanOutLongitude;
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
}
