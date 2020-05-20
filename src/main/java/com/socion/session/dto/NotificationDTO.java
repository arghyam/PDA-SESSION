package com.socion.session.dto;

public class NotificationDTO {

    private Long notificationId;
    private String title;
    private Long sessionId;
    private String description;
    private String notificationType;
    private String dateTime;
    private Boolean isRead;
    private String role;

    public NotificationDTO() {
    }

    public NotificationDTO(Long notificationId, String title, Long sessionId, String description, String notificationType, String dateTime, Boolean isRead, String role) {
        this.notificationId = notificationId;
        this.title = title;
        this.sessionId = sessionId;
        this.description = description;
        this.notificationType = notificationType;
        this.dateTime = dateTime;
        this.isRead = isRead;
        this.role = role;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


}