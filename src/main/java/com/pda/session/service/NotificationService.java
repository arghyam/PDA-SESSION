package com.pda.session.service;

import com.pda.session.dao.Session;
import com.pda.session.dto.IAMNotificationDto;
import com.pda.session.dto.NotificationDTO;
import com.pda.session.dto.NotificationStatusDTO;
import com.pda.session.dto.ResponseDTO;

public interface NotificationService {

    public ResponseDTO saveNotification(String userId, Session session, NotificationDTO notificationDTO);

    public ResponseDTO saveIAMNotification(IAMNotificationDto notificationDTO);

    public ResponseDTO getUnreadCount(String userId);

    public ResponseDTO getNotifications(String fetchUserIdFromToken, Integer pageSize, Integer pageNumber, Long offset);

    public ResponseDTO updateNotificationStatus(String fetchUserIdFromToken, NotificationStatusDTO notificationStatus);


}
