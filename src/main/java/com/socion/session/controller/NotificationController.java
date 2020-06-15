package com.socion.session.controller;

import com.socion.session.config.AppContext;
import com.socion.session.dto.IAMNotificationDto;
import com.socion.session.dto.NotificationStatusDTO;
import com.socion.session.dto.ResponseDTO;
import com.socion.session.service.NotificationService;
import com.socion.session.utils.HttpUtils;
import com.socion.session.utils.KeycloakUtil;
import org.keycloak.common.VerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(value = "/api/v2/session/notifications", produces = {"application/json"})
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @Autowired
    AppContext appContext;

    @GetMapping("/unReadCount")
    public ResponseDTO getUnreadCount(@RequestHeader("access-token") String accessToken) {
        try {
            return notificationService.getUnreadCount(KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm(),appContext.getKeycloakPublickey()));
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);
        }
    }

    @PostMapping("/save-iam-notification")
    public ResponseDTO saveIAMNotification(@RequestBody IAMNotificationDto notificationDTO) {
        return notificationService.saveIAMNotification(notificationDTO);
    }

    @PutMapping("/status")
    public ResponseDTO updateStatus(@RequestHeader("access-token") String accessToken, @RequestBody NotificationStatusDTO notificationStatus) {
        try {
            return notificationService.updateNotificationStatus(KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm(),appContext.getKeycloakPublickey()), notificationStatus);
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO getNotifications(@RequestHeader("access-token") String accessToken, @RequestHeader("offset") Long offset, @RequestParam("pageSize") Integer pageSize, @RequestParam("pageNumber") Integer pageNumber) {
        try {
            return notificationService.getNotifications(KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm(),appContext.getKeycloakPublickey()), pageSize, pageNumber, offset);
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);
        }
    }
}
