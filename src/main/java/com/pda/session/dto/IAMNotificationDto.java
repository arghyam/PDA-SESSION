package com.pda.session.dto;

public class IAMNotificationDto {

    private Long notificationId;
    private String userId;
    private String title;
    private String notificationType;
    private String dateTime;
    private Boolean isRead;
    private  String description;

    public IAMNotificationDto(Long notificationId, String userId, String title, String notificationType, String dateTime, Boolean isRead) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.title = title;
        this.notificationType = notificationType;
        this.dateTime = dateTime;
        this.isRead = isRead;
    }

    public IAMNotificationDto() {
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}