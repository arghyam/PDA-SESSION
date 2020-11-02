package com.pda.session.service.impl;

import com.pda.session.dto.*;
import com.pda.session.dto.v2.*;
import com.pda.session.dto.v2.User;
import com.pda.session.repository.AttendanceRepository;
import com.pda.session.repository.LocationRepository;
import com.pda.session.repository.SessionRepository;
import com.pda.session.repository.SessionRoleRepository;
import com.pda.session.config.AppContext;
import com.pda.session.dao.Attendance;
import com.pda.session.dao.Location;
import com.pda.session.dao.Session;
import com.pda.session.dao.SessionRole;
import com.pda.session.utils.*;
import com.pda.session.facade.EntityDao;
import com.pda.session.facade.KeycloakService;
import com.pda.session.facade.RegistryDao;
import com.pda.session.service.AttendanceService;
import com.pda.session.service.CleverTapService;
import com.pda.session.service.NotificationService;
import com.pda.session.service.v2.SessionServiceV2;
import com.pda.session.service.v2.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.common.VerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
public class AttendanceServiceImpl implements AttendanceService {


    @Autowired
    AppContext appContext;

    @Autowired
    SessionServiceV2 sessionService;

    @Autowired
    SessionRoleRepository sessionRoleRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    UserService userService;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    EntityDao entityDao;

    @Autowired
    RegistryDao registryDao;

    @Autowired
    KeycloakService keycloakService;

    @Autowired
    CleverTapService cleverTapService;

    @Autowired
    LocationRepository locationRepository;


    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceServiceImpl.class);

    @Transactional
    @Override
    public ResponseDTO attendance(String accessToken, AttendanceDTO attendanceDTO) throws IOException {
        LOGGER.info("QRType:{}", attendanceDTO.getQrType());
        LOGGER.info("isScanIn:{}", attendanceDTO.isScanIn());
        LOGGER.info("scan-Date {}", attendanceDTO.getDate());
        LOGGER.info("scan-time {}", attendanceDTO.getTime());
        boolean isTokenActive = false;
        try {
            isTokenActive = KeycloakUtil.verifyToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm(),appContext.getKeycloakPublickey());
            String loggedInUserId = KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm(),appContext.getKeycloakPublickey());
            if (isTokenActive) {
                return attendanceDTO.isScanIn() ? scanIn(loggedInUserId, attendanceDTO) : scanOut(loggedInUserId, attendanceDTO);
            }
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);
        }
        return HttpUtils.onFailure(HttpStatus.UNAUTHORIZED.value(), "Invalid access token");
    }

    private ResponseDTO scanIn(String userId, AttendanceDTO attendanceDTO) {
        LOGGER.info("=====================================SCAN IN========================================");
        LOGGER.info("QRType:{}", attendanceDTO.getQrType());
        LOGGER.info("isScanIn:{}", attendanceDTO.isScanIn());
        Session session = sessionRepository.findByIds(attendanceDTO.getSessionId());
        if (session == null) {
            return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "No Session Found");
        }
        if (null != userId && null != attendanceDTO.getSessionId()) {
            String role = Constants.TRAINEE;

            Attendance attendance = attendanceRepository.findByUserIdAndSessionIdAndRole(userId, attendanceDTO.getSessionId(), role);
            if (attendance != null) {
                if (Boolean.TRUE.equals(attendance.getScanOut())) {
                    LOGGER.info("Session Already Completed");
                    return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Session Already Completed");
                } else {
                    LOGGER.info("Already Joined the Session");
                    return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Already Joined the Session");
                }
            }
            String sessionStartDateTime = session.getSessionStartDate();
            String endOfSessionStartDateTime;
            try {
                endOfSessionStartDateTime = TimeUtils.convertToUTCTImeZoneAddMinutes(session.getSessionEndDate(), appContext.getSessionEndMinutes());
            } catch (Exception e) {
                return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Invalid Date and/or Time Format");
            }

            String dateTime = attendanceDTO.getDate() + " " + attendanceDTO.getTime() + ".000";
            if (Boolean.FALSE.equals(DateUtil.validateDateTimeFormat(dateTime))) {
                LOGGER.info("Invalid Date and/or Time Format");
                return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Invalid Date and/or Time Format");
            }


            if (isScanInBeforeSessionStart(sessionStartDateTime, dateTime)) {
                LOGGER.info("Please Scan In after Session Starts");
                return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Please scan-in after the session starts");
            }

            if (isScanInAfterSessionStartDayEnd(endOfSessionStartDateTime, dateTime)) {
                LOGGER.info("Scan-in is not allowed as the session is closed");
                return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Scan-in is not allowed as the session is closed");
            }

            SessionRole userData = new SessionRole(role, userId, session, Constants.TRAINEE, null, false);
            try {
                sessionRoleRepository.save(userData);
            } catch (Exception e) {
                LOGGER.debug("===================================================================");
                LOGGER.debug(e.getMessage());
                return HttpUtils.onFailure(500, "Database Transaction Failed:SESSION_ROLE TABLE" + e.getMessage());
            }

            Attendance attendace = transformToEntity(null, dateTime, userId, attendanceDTO, role, null);
            try {
                attendanceRepository.save(attendace);
            } catch (Exception e) {
                LOGGER.debug("===================================================================");
                LOGGER.debug(e.getMessage());
                return HttpUtils.onFailure(500, "Database Transaction Failed:ATTENDANCE TABLE" + e.getMessage());
            }

            int attendanceCount = attendanceRepository.findCountOfAttendanceBySessionId(attendanceDTO.getSessionId());
            LocalDateTime now = LocalDateTime.now();
            if (attendanceCount == 1) {
                List<SessionRole> userIds = sessionRoleRepository.getSessionMembersBySessionId(attendanceDTO.getSessionId());
                for (SessionRole user : userIds) {
                    if (null != user.getRole() && !user.getRole().isEmpty() && !user.getRole().equals(Constants.TRAINEE)) {
                        notificationService.saveNotification(user.getUserId(), session, new NotificationDTO(null, Constants.SESSION_START, attendanceDTO.getSessionId(), null, NotificationEvents.ATTENDANCE.toString(), now.toLocalDate().toString() + " " + now.toLocalTime().toString(), false, user.getRole().equalsIgnoreCase(Constants.OTHER_ROLE_LABEL) ? user.getOtherRoleName() : user.getRole()));

                    }
                }
                notificationService.saveNotification(session.getSessionCreator(), session, new NotificationDTO(null, Constants.SESSION_START, session.getId(), null, NotificationEvents.ATTENDANCE.toString(), now.toLocalDate().toString() + " " + now.toLocalTime().toString(), false, Constants.SESSION_CREATOR_LABEL));
            }


            LOGGER.info("Scanned in sucessfully");
            Location location = new Location(attendanceDTO.getLatitude(), attendanceDTO.getLongitude(), attendanceDTO.getSessionId(), userId, Constants.TRAINEE);
            locationRepository.save(location);
            notificationService.saveNotification(userId, session, new NotificationDTO(null, Constants.SESSION_START, session.getId(), null, NotificationEvents.ATTENDANCE.toString(), now.toLocalDate().toString() + " " + now.toLocalTime().toString(), false, Constants.TRAINEE));
            return HttpUtils.success(session.getSessionName(), "Scanned in Successfully");
        }
        return HttpUtils.onFailure(500, "User and/or Session is not Available");
    }

    private ResponseDTO scanOut(String userId, AttendanceDTO attendanceDTO) throws IOException {
        LOGGER.info("=====================================SCAN OUT========================================");
        if (Boolean.TRUE.equals(attendanceDTO.getOffline())) {
            LOGGER.error("Before offline timeout");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                LOGGER.info("EXCEPTION:{}", e.getMessage());
            }
            LOGGER.error("After offline timeout");
        }
        if (null != userId && null != attendanceDTO.getSessionId()) {
            Session session = sessionRepository.findByIds(attendanceDTO.getSessionId());
            Attendance attendance = attendanceRepository.findByUserIdAndSessionIdAndRole(userId, attendanceDTO.getSessionId(), Constants.TRAINEE);
            if (null == attendance || attendance.getScanInDateTime() == null) {
                LOGGER.info("Please scan-in first");
                return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Please Scan In First");
            }
            if (attendance.getScanOutDateTime() != null) {
                LOGGER.info("Already Scanned Out from Session");
                return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Already Scanned Out from Session");
            }

            String startOfSessionEndDateTime = session.getSessionStartDate();
            String endOdSessionEndDateTime;
            try {
                endOdSessionEndDateTime = TimeUtils.convertToUTCTImeZoneAddMinutes(session.getSessionEndDate(), appContext.getSessionEndMinutes());
            } catch (Exception e) {
                return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Invalid Date and/or Time Format");
            }

            String dateTime = attendanceDTO.getDate() + " " + attendanceDTO.getTime() + ".000";

            if (Boolean.FALSE.equals(DateUtil.validateDateTimeFormat(dateTime))) {
                LOGGER.info("Invalid Date and/or Time Format");
                return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Invalid Date and/or Time Format");
            }


            if (isScanOutBeforeSessionEndDayStart(startOfSessionEndDateTime, dateTime)) {
                LOGGER.info("Scan-out is allowed only after session starts");
                return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Scan-out is allowed only after session starts");
            }

            if (isScanOutAfterSessionEndDayEnd(endOdSessionEndDateTime, dateTime)) {
                LOGGER.info("Scan-out is not allowed as the session is closed");
                return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Scan-out is not allowed as the session is closed");
            }

            ResponseDTO responseDTO = userService.getUserDetails(userId, userId, false);
            User user = (User) responseDTO.getResponse();
            if (user == null) {
                return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Unable to fetch user");
            }
            String attestationUrl = generateAttestation(session, user, Constants.TRAINEE, attendanceDTO);
            //TODO move s3 url to props
            if (attestationUrl == null) {
                attestationUrl = appContext.getAwsS3UrlPrivate()+"attestation/" + session.getId() + Constants.TRAINEE + user.getUserId();
            }
            LOGGER.info("attestation URL: {} for session id: {}", attestationUrl, attendanceDTO.getSessionId());
            Attendance attendanceOutput = transformToEntity(attendance, dateTime, userId, attendanceDTO, null, attestationUrl);
            LOGGER.info("saving user data into attendance table");

            attendanceRepository.save(attendanceOutput);
            sendAttestationsToRegistry(userId, attendanceOutput, attendanceDTO, Constants.TRAINEE);

            int attendanceTotalCount = attendanceRepository.findCountOfAttendanceBySessionId(attendanceDTO.getSessionId());
            LocalDateTime now = LocalDateTime.now();

            if (attendanceTotalCount == 1) {
                List<SessionRole> userIds = sessionRoleRepository.getSessionMembersBySessionId(attendanceDTO.getSessionId());
                for (SessionRole user1 : userIds) {
                    if (null != user1.getRole() && !user1.getRole().isEmpty() && !user1.getRole().equals(Constants.TRAINEE)) {
                        notificationService.saveNotification(user1.getUserId(), session, new NotificationDTO(null, Constants.SESSION_END, attendanceDTO.getSessionId(), null, NotificationEvents.ATTENDANCE.toString(), now.toLocalDate().toString() + " " + now.toLocalTime().toString(), false, user1.getRole().equalsIgnoreCase(Constants.OTHER_ROLE_LABEL) ? user1.getOtherRoleName() : user1.getRole()));
                    }
                }
                notificationService.saveNotification(session.getSessionCreator(), session, new NotificationDTO(null, Constants.SESSION_END, session.getId(), null, NotificationEvents.ATTENDANCE.toString(), now.toLocalDate().toString() + " " + now.toLocalTime().toString(), false, Constants.SESSION_CREATOR_LABEL));
            }
            LOGGER.info("scanned out sucessfully");
            Location location = locationRepository.findByUserIdAndRoleAndSessionId(userId, attendanceDTO.getSessionId());

            assert location != null;
            location.setScanOutLatitude(attendanceDTO.getLatitude());
            location.setScanOutLongitude(attendanceDTO.getLongitude());
            locationRepository.save(location);
            notificationService.saveNotification(userId, session, new NotificationDTO(null, Constants.SESSION_END, session.getId(), null, NotificationEvents.ATTENDANCE.toString(), now.toLocalDate().toString() + " " + now.toLocalTime().toString(), false, Constants.TRAINEE));
            LocalDateTime attestationTime = LocalDateTime.now();
            NotificationDTO notificationDTO = new NotificationDTO(null, Constants.ATTESTATION_RECEIVED, session.getId(), null, NotificationEvents.ATTESTATION.toString(), attestationTime.toLocalDate().toString() + " " + attestationTime.toLocalTime().toString(), false, Constants.TRAINEE);

            notificationService.saveNotification(userId, session, notificationDTO);
            return HttpUtils.success(session.getSessionName(), "Scanned Out Successfully");

        }
        return HttpUtils.onFailure(500, "User and/or Session is not Available");
    }

    private Attendance transformToEntity(Attendance attendance, String dateTime, String userId, AttendanceDTO attendanceDTO, String role, String attestationUrl) {
        if (attendance == null) {
            attendance = new Attendance();
        }
        attendance.setUserId(userId);
        attendance.setSessionId(attendanceDTO.getSessionId());
        attendance.setDeleted(false);
        attendance.setAttestationUrl(attestationUrl);
        if (attendanceDTO.isScanIn()) {
            attendance.setScanInDateTime(dateTime);
            attendance.setScanIn(true);
            attendance.setScanOut(false);
            if (null != role) attendance.setRole(role);
        } else {
            attendance.setScanOutDateTime(dateTime);
            attendance.setScanOut(true);
        }
        return attendance;
    }

    private boolean isScaninOnSameDayAsSessionStart(String sessionDateTime, String scaninDate) {
        if (null != sessionDateTime && !sessionDateTime.isEmpty()) {
            String[] dateTime = sessionDateTime.split(" ");
            return !LocalDate.parse(dateTime[0]).isBefore(LocalDate.parse(scaninDate));
        }
        return false;
    }

    private boolean isScanInAfterOrEqualToSessionStartAndBeforrEndDate(String sessionDateTime, String scanInDate, String scanInTime) {
        if (null != sessionDateTime && !sessionDateTime.isEmpty()) {
            String[] startDatetime = sessionDateTime.split(" ");
            return (LocalDate.parse(startDatetime[0]).isEqual(LocalDate.parse(scanInDate)) && !LocalTime.parse(scanInTime).isBefore(LocalTime.parse(startDatetime[1])));
        }
        return false;
    }

    private boolean isScanInBeforeSessionStart(String sessionStartTime, String requestTime) {
        if (null != requestTime && !requestTime.isEmpty()) {
            String actual = sessionStartTime.replace(" ", "T");
            String requested = requestTime.replace(" ", "T");
            return LocalDateTime.parse(requested).isBefore(LocalDateTime.parse(actual));
        }
        return false;
    }

    private boolean isScanInAfterSessionStartDayEnd(String endOfSessionStartDateTime, String requestTime) {
        if (null != requestTime && !requestTime.isEmpty()) {
            String actual = endOfSessionStartDateTime.replace(" ", "T");
            String requested = requestTime.replace(" ", "T");
            return LocalDateTime.parse(requested).isAfter(LocalDateTime.parse(actual));
        }
        return false;
    }

    private boolean isScanOutBeforeSessionEndDayStart(String startOfSessionEndDateTime, String requestTime) {
        if (null != requestTime && !requestTime.isEmpty()) {
            String actual = startOfSessionEndDateTime.replace(" ", "T");
            String requested = requestTime.replace(" ", "T");
            return LocalDateTime.parse(requested).isBefore(LocalDateTime.parse(actual));
        }
        return false;
    }

    private boolean isScanOutAfterSessionEndDayEnd(String endOfSessionEndDateTime, String requestTime) {
        if (null != requestTime && !requestTime.isEmpty()) {
            String actual = endOfSessionEndDateTime.replace(" ", "T");
            String requested = requestTime.replace(" ", "T");
            return LocalDateTime.parse(requested).isAfter(LocalDateTime.parse(actual));
        }
        return false;
    }


    private boolean isScanOutOnSameDayAsSessionEnd(String sessionDateTime, String scanOutDate) {
        if (null != sessionDateTime && !sessionDateTime.isEmpty()) {
            String[] dateTime = sessionDateTime.split(" ");
            return LocalDate.parse(dateTime[0]).isEqual(LocalDate.parse(scanOutDate));
        }
        return false;
    }


    private String generateAttestation(Session session, User user, String role, AttendanceDTO attendanceDTO) throws IOException {
        TopicInfo details = null;
        Call<TopicInfo> topicRequest = entityDao.topicDetailWithProgramContentDTO(session.getTopicId(), true);
        retrofit2.Response topicResponse = topicRequest.execute();
        LOGGER.info("Message from entity {}", topicResponse.message());
        if (!topicResponse.isSuccessful()) {
            LOGGER.error("unable to fetch Content And Program details {}", topicResponse.errorBody().string());
        } else {
            details = (TopicInfo) topicResponse.body();
        }

        TemplateDto dto = new TemplateDto(session.getId(), session.getSessionName(), session.getSessionStartDate(), session.getSessionEndDate(), session.getTrainingOrganization(), user.getPhoto(), user.getName(), role, user.getUserId(), session.getProgramName(), 0);

        dto.setEntityName(details.getProgram().getEntityName() != null ? details.getProgram().getEntityName() : "");
        ObjectMapper objectMapper = new ObjectMapper();
        LOGGER.info(objectMapper.writeValueAsString(dto));
        Call<ResponseDTO> userRequest = entityDao.getAttestationDetails(dto);
        retrofit2.Response userResponse = userRequest.execute();

        LOGGER.error("Response from entity {}", userResponse.message());
        if (!userResponse.isSuccessful()) {
            LOGGER.error("unable to generate attestation {}", userResponse.errorBody().string());
        }
        ResponseDTO responseDTO = (ResponseDTO) userResponse.body();
        cleverTapEvent(user, null != responseDTO ? responseDTO.getResponse().toString() : null, session, attendanceDTO);
        return null != responseDTO ? responseDTO.getResponse().toString() : null;
    }


    private ResponseDTO cleverTapEvent(User user, String attestationUrl, Session session, AttendanceDTO attendanceDTO) throws IOException {
        Attendance attendance = attendanceRepository.findByUserIdAndSessionId(user.getUserId(), session.getId(), Constants.TRAINEE);
        TopicInfo details;
        Call<TopicInfo> topicRequest = entityDao.topicDetailWithProgramContentDTO(session.getTopicId(), true);
        retrofit2.Response topicResponse = topicRequest.execute();
        LOGGER.info("Message from entity {}", topicResponse.message());
        if (!topicResponse.isSuccessful()) {
            LOGGER.error("unable to fetch Content And Program details {}", topicResponse.errorBody().string());
            return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "Content And Program details Not Available");
        } else {
            details = (TopicInfo) topicResponse.body();
        }
        CleverTapEventData data = new CleverTapEventData();
        data.setEventType("Generate Attestation");
        data.setUserId(user.getUserId());
        data.setSessionId(session.getId());
        data.setSessionName(session.getSessionName());
        data.setTopicId(session.getTopicId());
        data.setTopicName(details.getTopic().getName());
        data.setSessionStartDate(session.getSessionStartDate());
        data.setSessionEndDate(session.getSessionEndDate());
        data.setProgramId(details.getTopic().getProgramId());
        data.setProgramName(session.getProgramName());
        data.setIpAddress(attendanceDTO.getIpAddress());
        data.setSessionLat(attendanceDTO.getLatitude());
        data.setSessionLon(attendanceDTO.getLongitude());
        data.setNoOfParticipants((long) attendanceRepository.numberOfParticipantsAttendedSession(session.getId()));
        if (null != attendanceDTO.getOffline()) {
            LOGGER.info("Offline Status:{}", attendanceDTO.getOffline());
            data.setOfflineToOnlineSync(attendanceDTO.getOffline());
        }
        data.setRole(Constants.TRAINEE);
        data.setTimestamp(LocalDateTime.now().toString());
        data.setScanInTime(attendance.getScanInDateTime());
        data.setScanOutTime(attendanceDTO.getDate() + " " + attendanceDTO.getTime());
        data.setAttestationUrl(attestationUrl);
        data.setTraining_organization(session.getTrainingOrganization());
        Call<CleverTapEventData> userRequest = entityDao.add(data);
        retrofit2.Response userResponse = userRequest.execute();
        if (!userResponse.isSuccessful()) {
            assert userResponse.errorBody() != null;
            LOGGER.error("unable to fetch user details {}", userResponse.errorBody().string());
            return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "No Data Posted to Telemetry");

        }
        return HttpUtils.success(cleverTapService.uploadSingleEvent(data), "Generated Event");
    }

    public ResponseDTO sendAttestationsToRegistry(String userId, Attendance attendance, AttendanceDTO attendanceDTO, String role) throws IOException {
        ResponseDTO response = new ResponseDTO();
        Session session = sessionRepository.findByIds(attendance.getSessionId());
        TopicInfo details;
        assert session != null;
        Call<TopicInfo> topicRequest = entityDao.topicDetailWithProgramContentDTO(session.getTopicId(), true);
        retrofit2.Response topicResponse = topicRequest.execute();
        LOGGER.info("Message from entity {}", topicResponse.message());
        if (!topicResponse.isSuccessful()) {
            LOGGER.error("unable to fetch Content And Program details {}", topicResponse.errorBody().string());
            return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "Content And Program details Not Available");
        } else {
            details = (TopicInfo) topicResponse.body();
        }

        Request request = new Request();
        Attestations attestations = new Attestations(attendance.getUserId(), attendance.getRole(), attendance.getScanInDateTime(), attendance.getScanOutDateTime(), attendance.getAttestationUrl(), attendance.isDeleted());
        List<String> urls = new ArrayList<>();
        for (ContentDTO content : details.getContent()) {
            urls.add(content.getUrl());
        }
        attestations.setContentS3Url(urls.toString());
        attestations.setSessionName(sessionRepository.findByIds(session.getId()).getSessionName());
        attestations.setEntityId(details.getProgram().getEntityId().intValue());
        attestations.setEntityName(details.getProgram().getEntityName());
        attestations.setProgramName(details.getProgram().getName());
        if (!role.equalsIgnoreCase(Constants.TRAINEE)) {
            attestations.setScanInSessionLat("");
            attestations.setScanInSessionLon("");
            attestations.setScanOutSessionLat("");
            attestations.setScanOutSessionLon("");
            attestations.setNoOfParticipants(attendanceRepository.numberOfParticipantsAttendedSession(session.getId()));
        } else {
            Location location = locationRepository.findByUserIdAndRoleAndSessionId(userId, attendanceDTO.getSessionId());
            attestations.setScanInSessionLat(location.getScanInLatitude() == null ? "" : location.getScanInLatitude());
            attestations.setScanInSessionLon(location.getScanInLongitude() == null ? "" : location.getScanInLongitude());
            attestations.setScanOutSessionLat(attendanceDTO.getLatitude() == null ? "" : attendanceDTO.getLatitude());
            attestations.setScanOutSessionLon(attendanceDTO.getLongitude() == null ? "" : attendanceDTO.getLongitude());
            attestations.setNoOfParticipants(0);
        }
        attestations.setTypeOfAttestation(Constants.SESSION);

        request.setAttestations(attestations);

        RegistryRequest registryRequest = new RegistryRequest(null, request, RegistryResponse.API_ID.CREATE.getId());
        ObjectMapper objectMapper = new ObjectMapper();
        String data = objectMapper.writeValueAsString(registryRequest);
        LOGGER.info("API Request:==={}", data);
        String adminAccessToken = keycloakService.generateAccessToken(appContext.getAdminUserName());
        Call<RegistryResponse> createRegistryEntryCall = registryDao.addAttestations(adminAccessToken, registryRequest);
        retrofit2.Response registryUserCreationResponse = createRegistryEntryCall.execute();
        RegistryResponse registryResponse = (RegistryResponse) registryUserCreationResponse.body();
        if (!registryResponse.getResponseParams().getStatus().name().equals(Constants.SUCCESSFUL)) {
            LOGGER.error("Error Creating registry entry {} ", registryResponse.getResponseParams().getErrmsg());
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Error Creating registry entry {}" + registryResponse.getResponseParams().getErrmsg());
            return response;
        } else {
            LOGGER.info("Attestation successfully pushed to Registry");
        }
        response.setResponseCode(HttpStatus.CREATED.value());
        response.setMessage("Registry entry created successfully.");
        return response;
    }

    public ResponseDTO getLinkedPrograms(String accessToken) {
        ResponseDTO response = new ResponseDTO();
        List<LinkedProgramsDTO> linkedProgramsList = new ArrayList<>();
        try {
            String callingUserId = KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm(),appContext.getKeycloakPublickey());
            List<Long> sessionIds = attendanceRepository.findDistinctSessionIdByUserIdAndScannedOut(callingUserId);
            List<BigInteger> topicIds = sessionRepository.findByTopicIdList(sessionIds);
            Call<ResponseDTO> topicRequest = entityDao.getPrograms(topicIds);
            retrofit2.Response topicResponse = topicRequest.execute();
            LOGGER.info("Message from entity  {}", topicResponse.message());
            if (!topicResponse.isSuccessful()) {
                LOGGER.error("unable to fetch Content And Program details {}", topicResponse.errorBody().string());
                throw new IOException();
            } else {
                topicResponse.body();
                linkedProgramsList = (List<LinkedProgramsDTO>) ((ResponseDTO) topicResponse.body()).getResponse();
            }

        } catch (Exception e) {
            LOGGER.error("Error while fetching programs");
            response.setMessage("Error while fetching programs");
            response.setResponse(null);
            response.setResponseCode(HttpStatus.NOT_FOUND.value());
        }
        response.setMessage("Sucessfully fetched Programs");
        response.setResponse(linkedProgramsList);
        response.setResponseCode(HttpStatus.OK.value());
        return response;
    }

    @Override
    public Set<String> getUserIdsAttendedByProgramId(long programId) {
        Set<Long> sessionIds = sessionService.getSessionIdsByProgramId(programId);
        Set<String> userIds = new HashSet<>();
        if (!sessionIds.isEmpty()) {
            userIds.addAll(attendanceRepository.getUserIdsBySessionId(sessionIds));
        }
        return userIds;
    }


}

