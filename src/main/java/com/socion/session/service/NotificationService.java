package com.socion.session.service;

import com.socion.session.dao.Session;
import com.socion.session.dto.IAMNotificationDto;
import com.socion.session.dto.NotificationDTO;
import com.socion.session.dto.NotificationStatusDTO;
import com.socion.session.dto.ResponseDTO;

public interface NotificationService {

    public ResponseDTO saveNotification(String userId, Session session, NotificationDTO notificationDTO);

    public ResponseDTO saveIAMNotification(IAMNotificationDto notificationDTO);

    public ResponseDTO getUnreadCount(String userId);

    public ResponseDTO getNotifications(String fetchUserIdFromToken, Integer pageSize, Integer pageNumber, Long offset);

    public ResponseDTO updateNotificationStatus(String fetchUserIdFromToken, NotificationStatusDTO notificationStatus);


}
