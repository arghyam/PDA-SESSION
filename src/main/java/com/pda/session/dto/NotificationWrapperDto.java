package com.pda.session.dto;

import java.util.List;


public class NotificationWrapperDto {

    private List<NotificationDTO> notifications;
    private Integer pageSize;
    private Integer pageNumber;
    private Long total;

    public NotificationWrapperDto(List<NotificationDTO> notifications, Integer pageSize, Integer pageNumber, Long total) {
        this.notifications = notifications;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.total = total;
    }

    public NotificationWrapperDto() {
    }

    public List<NotificationDTO> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationDTO> notifications) {
        this.notifications = notifications;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }


}
