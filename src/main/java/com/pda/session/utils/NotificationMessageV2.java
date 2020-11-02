package com.pda.session.utils;

import com.pda.session.repository.SessionRepository;
import com.pda.session.repository.SessionRoleRepository;
import com.pda.session.dao.Session;
import com.pda.session.dto.IAMNotificationDto;
import com.pda.session.dto.NotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class NotificationMessageV2 {


    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    SessionRoleRepository sessionRoleRepository;

    private static final Logger log = LoggerFactory.getLogger(NotificationMessageV2.class);

    public String getNotificationDescription(String userId, NotificationDTO notification, Session session) {

        List<String> existingRoles = sessionRoleRepository.collectRolesForUserforSession(userId, session.getId());
        String description = "";
        switch (notification.getTitle()) {
            case Constants.ADD_MEMBER:
                List<String> roles = new ArrayList<>();
                if (null != existingRoles && !existingRoles.isEmpty()) {
                    existingRoles.forEach((String roleMap) -> {
                        if (roleMap.equalsIgnoreCase("TRAINER")) {
                            roles.add("TRAINER");
                        }
                        if (roleMap.equalsIgnoreCase("ADMIN")) {
                            roles.add("ADMIN");
                        }
                        if (roleMap.equalsIgnoreCase("OTHER")) {
                            roles.add(sessionRoleRepository.getOtherRoleNameOfUserForASession(session.getId(), userId));
                        }

                    });
                }
                log.info(session.getSessionStartDate());
                description = "You have been enrolled to session " + session.getSessionName() + " as " + roles.toString() + " scheduled for " + DateUtil.fetchTheLocalTimeForNotification(session.getSessionStartDate(), -330L);
                break;
            case Constants.DELETE_MEMBER:
                description = "You have been withdrawn from session " + session.getSessionName() + " scheduled for " + DateUtil.fetchTheLocalTimeForNotification(session.getSessionStartDate(), -330L);
                break;
            case Constants.EDIT_SESSION:
                description = "You have made changes to the session " + session.getSessionName();
                break;
            case Constants.EDIT_SESSION_END_DATE:
                description = "End time of the session " + session.getSessionName() + " has been changed";
                break;
            case Constants.EDIT_SESSION_ADDRESS_CHANGE:
                description = "Venue of the session " + session.getSessionName() + " has been changed";
                break;
            case Constants.EDIT_SESSION_DATE:
                description = "Start time of the session " + session.getSessionName() + " has been changed";
                break;
            case Constants.DELETE_SESSION:
                String[] startDatetime = session.getSessionStartDate().split(" ");
                description = "The Session " + session.getSessionName() + " scheduled on " + startDatetime[0] + " has been deleted";
                break;
            case Constants.SESSION_CREATED:
                description = "You have successfully created Session " + session.getSessionName() + " scheduled for " + DateUtil.fetchTheLocalTimeForNotification(session.getSessionStartDate(), -330L);
                break;
            case Constants.SESSION_START:
                if (!notification.getRole().equalsIgnoreCase(Constants.TRAINEE)) {
                    description = "Session " + session.getSessionName() + " has started.";
                } else {
                    description = "You have scanned into Session " + session.getSessionName();
                }
                break;
            case Constants.SESSION_PLEASE:
                if (!notification.getRole().equalsIgnoreCase(Constants.TRAINEE)) {
                    description = "Please scan in first";
                }
                break;
            case Constants.SESSION_END:
                if (!notification.getRole().equalsIgnoreCase(Constants.TRAINEE)) {
                    description = "Session " + session.getSessionName() + " has ended.";
                } else {
                    description = "You have scanned out of Session " + session.getSessionName();
                }
                break;
            case Constants.ATTESTATION_RECEIVED:
                if (Constants.TRAINEE.equalsIgnoreCase(notification.getRole())) {
                    description = "You have received an attestation for session " + session.getSessionName() + " Congrats!!!";
                } else if (!Constants.SESSION_CREATOR_LABEL.equalsIgnoreCase(notification.getRole())) {
                    String role;
                    if (notification.getRole().equalsIgnoreCase("OTHER")) {
                        role = sessionRoleRepository.getOtherRoleNameOfUserForASession(session.getId(), userId);
                    } else {
                        role = notification.getRole();
                    }
                    description = "You have received  [" + role + "] attestation for session " + session.getSessionName() + " Congrats!!!";

                }
                break;
            case Constants.ADD_ADDITIONAL_LINKS_TO_SESSION:
                    description = "An additional content URL link has been posted for the session "+ session.getSessionName() +" you attended on "+session.getSessionStartDate().split(" ")[0];
                break;
        }

        return description;
    }

    public String getDescriptionMessageForProfile(IAMNotificationDto notification) {

        String description = "";

        switch (notification.getTitle()) {
            case Constants.ADD_EMAIL:
                description = "Email ID has been added";
                break;
            case Constants.FORGOT_PASWORD:
                description = "Your password has been reset.";
                break;
            case Constants.REMOVE_PHOTO:
                description = "Your photo has been removed";
                break;
            case Constants.CHANGE_NAME:
                description = "Your name has been updated";
                break;
            case Constants.CHANGE_PHONE_NUM:
                description = "Your phone no. has been updated";
                break;
            case Constants.CHANGE_OLD_EMAIL:
                description = "Your email id has been updated";
                break;
            case Constants.UPDATE_PHOTO:
                description = "Your photo has been updated";
                break;
            case Constants.UPDATE_LOCATION:
                description = "Your location has been updated";
                break;
            case Constants.REMOVE_LOCATION:
                description = "Your photo has been updated";
                break;
            case Constants.RESET_PASWORD:
                description = "Password has been reset";
                break;
            case Constants.DEACTIVATE_ACCOUNT:
                description = "Account has been deactivated. Sorry to see you go. You will not be visible for others to access you and you will not be able to participate in any sessions until you reactivate your account.";
                break;
            case Constants.REACTIVATE_ACCOUNT:
                description = "Account has been reactivated Good to have you back ";
                break;
        }
        return description;
    }
}