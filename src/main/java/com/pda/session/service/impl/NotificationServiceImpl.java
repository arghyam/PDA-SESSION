package com.pda.session.service.impl;

import com.pda.session.dto.*;
import com.pda.session.repository.NotificationRepository;
import com.pda.session.dao.Notification;
import com.pda.session.dao.Session;
import com.pda.session.service.NotificationService;
import com.pda.session.utils.HttpUtils;
import com.pda.session.utils.NotificationMessageV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {


    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    NotificationRepository notificationReposiotry;

    @Autowired
    NotificationMessageV2 notificationMessageV2;

    @Override
    public ResponseDTO saveNotification(String userId, Session session, NotificationDTO notificationDTO) {
        Notification notification = new Notification(null, notificationDTO.getTitle(), session, notificationDTO.getDateTime(), userId, notificationDTO.getNotificationType(), false, "", false, notificationDTO.getRole());
        notification.setDescription(notificationMessageV2.getNotificationDescription(userId, notificationDTO, session));

        LOGGER.debug("Role type is {}", notification.getRole());
        notificationReposiotry.save(notification);
        LOGGER.info("Notification Saved for session: {} with userId: {}",session.getId(), userId);
        return HttpUtils.success("Notification Saved for session: " + session.getId() + " user: ", userId);
    }

    @Override
    public ResponseDTO saveIAMNotification(IAMNotificationDto notificationDTO) {
        LOGGER.info("IAM CALL for:{}", notificationDTO.getTitle());
        Notification notification = new Notification(null, notificationDTO.getTitle(), null, notificationDTO.getDateTime(), notificationDTO.getUserId(), notificationDTO.getNotificationType(), false, "", false, null);
        notification.setDescription(notificationMessageV2.getDescriptionMessageForProfile(notificationDTO));
       notificationReposiotry.save(notification);
        LOGGER.info("Notification Saved for user {}" , notificationDTO.getUserId());
        return HttpUtils.onSuccess(null, "Notification saved for user " + notificationDTO.getUserId());

    }

    @Override
    public ResponseDTO getUnreadCount(String memberId) {
        Long count = notificationReposiotry.countByUserIdAndIsReadAndIsDeleted(memberId, Boolean.FALSE, Boolean.FALSE);
        return HttpUtils.success(count, "Unread Count");
    }

    @Override
    public ResponseDTO updateNotificationStatus(String userId, NotificationStatusDTO notificationStatus) {
        if (null != notificationStatus && notificationStatus.getNotificationId() != null) {
            Notification notification = notificationReposiotry.getOne(notificationStatus.getNotificationId());
            if (notification.getDeleted()) {
                return HttpUtils.onFailure(400, "Notification Already Deleted");
            }
            notification.setDeleted(notificationStatus.getIsDeleted());
            notification.setRead(notificationStatus.getIsRead());
            notificationReposiotry.save(notification);
            return HttpUtils.success(200, "Updated Status");
        }
        return HttpUtils.onFailure(400, "Notification Request Status is Empty");
    }

    @Override
    public ResponseDTO getNotifications(String userId, Integer pageSize, Integer pageNumber, Long offset) {
        if (null == pageNumber || null == pageSize || pageNumber == 0 || pageSize == 0) {
            return HttpUtils.onFailure(400, "Check Pagination Details (should not be null or 0)");
        }
        Page<Notification> notifications = notificationReposiotry.findAllByUserIdAndIsDeleted(userId, Boolean.FALSE, PageRequest.of(pageNumber - 1, pageSize, Sort.by("date_time").descending()));
        LOGGER.info("No. of Notifications:{}", notifications.getContent().size());
        List<NotificationDTO> notificationDtos = null;
        try {
            notificationDtos = convertToDto(notifications.getContent());
        } catch (Exception e) {
            LOGGER.error("Error:{}", e.getMessage());
            return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
        NotificationWrapperDto notificationWrapperDto = new NotificationWrapperDto(notificationDtos, pageSize, pageNumber, notificationReposiotry.countByUserIdAndIsDeleted(userId, Boolean.FALSE));
        LOGGER.info("Fetching notifications for {}", userId);

        return HttpUtils.success(notificationWrapperDto, "Fetched Notifications");
    }

    private List<NotificationDTO> convertToDto(List<Notification> notifications) {
        List<NotificationDTO> notificationDTOs = new ArrayList<>();
        for (Notification notification : notifications) {
            notificationDTOs.add(new NotificationDTO(notification.getId(), notification.getTitle(), null != notification.getSession() ? notification.getSession().getId() : null, notification.getDescription(), notification.getNotificationType(), notification.getDateTime(), notification.getRead(), notification.getRole()));
        }
        return notificationDTOs;
    }
}
