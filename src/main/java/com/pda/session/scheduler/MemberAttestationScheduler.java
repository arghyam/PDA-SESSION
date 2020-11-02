package com.pda.session.scheduler;

import com.pda.session.dao.*;
import com.pda.session.dto.*;
import com.pda.session.exceptions.UserDataNotFound;
import com.pda.session.repository.*;
import com.pda.session.service.AttendanceService;
import com.pda.session.service.NotificationService;
import com.pda.session.service.v2.UserService;
import com.pda.session.service.CleverTapService;
import com.pda.session.config.AppContext;
import com.pda.session.dto.v2.RegistryUserWithOsId;
import com.pda.session.dto.v2.TopicInfo;
import com.pda.session.facade.AttestationDao;
import com.pda.session.facade.EntityDao;
import com.pda.session.facade.IamDao;
import com.pda.session.utils.Constants;
import com.pda.session.utils.EmailUtils;
import com.pda.session.utils.HttpUtils;
import com.pda.session.utils.NotificationEvents;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import retrofit2.Call;

import javax.mail.MessagingException;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MemberAttestationScheduler {

    private Long totalrecords = Long.valueOf(0);
    private Long succcessrecords = Long.valueOf(0);
    private Long failedrecords = Long.valueOf(0);

    @Autowired
    SessionRoleRepository sessionRoleRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    FailedattestationRepository failedattestationRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private AppContext appContext;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    IamDao iamDao;

    @Autowired
    EntityDao entityDao;

    @Autowired
    private CleverTapService cleverTapService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    UserService userService;

    @Autowired
    AttestationDao attestationDao;

    @Autowired
    CronlogsRepository cronlogsRepository;

    @Autowired
    AttendanceService attendanceService;


    private static final Logger LOGGER = LoggerFactory.getLogger(MemberAttestationScheduler.class);

    //@Scheduled(cron = "0 0/30 * * * ?", zone = "UTC")
   public void reportCurrentTime() throws IOException, MessagingException {

        Cronlogsdao cronlogsdao = new Cronlogsdao();
        cronlogsdao.setStartofcron(LocalDateTime.now().toString());
        cronlogsdao.setNameofcron("Member Attestation Cron");
        LOGGER.info("Generating Attestation for members");

        List<Long> infos = sessionRepository.findCompletedSessionOnThisDate(cronlogsRepository.lastentry().getEndofcron().replace("T"," "), LocalDateTime.now().toString().replace("T"," "));
        LOGGER.info(infos.toString());
        if (infos.size() == 0) {
            LOGGER.info("NO Session for today!");
        }
        List<Long> sessionIds = new ArrayList<Long>();
        List<String> timeZones = null;
        if (infos.size() != 0) {
            List<BigInteger> validSessions = sessionRoleRepository.findSessionswithAtleastOneParticipant(infos);
            for (BigInteger session : validSessions) {
                if (!attendanceRepository.isMemberAttestationGenerated(session.longValue())) {
                    sessionIds.add(session.longValue());
                    LOGGER.info("=================================sessions {}============================", session.longValue());
                }
            }
            LOGGER.info("No of sessions for which attestations needs to be generated:{}", sessionIds.size());
            LOGGER.info("Ids of sessions for which attestations needs to be generated:{}", sessionIds);
             timeZones=sessionIds.isEmpty()?null:sessionRepository.findBySessionTimeZoneList(sessionIds);
            generateAttestation(sessionIds);
        }

        totalrecords=failedrecords+succcessrecords;
        cronlogsdao.setTotalrecords(totalrecords);
        cronlogsdao.setSuccessrecords(succcessrecords);
        cronlogsdao.setFailedrecords(failedrecords);
        cronlogsdao.setEndofcron(LocalDateTime.now().toString());
        cronlogsRepository.save(cronlogsdao);
        
        EmailUtils.sendEmail(appContext, appContext.getMemberAttestationSchedulerEmail(), totalrecords, succcessrecords, failedrecords,timeZones==null?"NO Timezones":timeZones.toString().replace("[","").replace("]",""));
        totalrecords = 0L;
        succcessrecords = 0L;
        failedrecords = 0L;
        LOGGER.info("Attestation generated for members");
        LOGGER.info("The date is  {}", LocalDate.now().toString());
    }

    private List<Map<String, String>> generateAttestation(List<Long> validSessions){

        List<TemplateDto> templateDtos = new ArrayList<>();
        if (null != validSessions && !validSessions.isEmpty()) {
            List<SessionRole> sessionroles = sessionRoleRepository.findMembersRelatedToSession(validSessions);
            for (SessionRole role : sessionroles) {
                Session session = sessionRepository.findByIds(role.getSession().getId());
                try {
                    Call<RegistryUserWithOsId> userRequest = iamDao.getUser("null",role.getUserId());
                    retrofit2.Response<RegistryUserWithOsId> userResponse = userRequest.execute();
                    if (!userResponse.isSuccessful()) {
                        LOGGER.error("unable to fetch user details {}", userResponse.errorBody().string());
                    }
                    RegistryUserWithOsId userDetails = userResponse.body();
                    if (null != userDetails) {
                        TopicInfo details = null;
                        Call<TopicInfo> topicRequest = entityDao.topicDetailWithProgramContentDTO(session.getTopicId(), true);
                        retrofit2.Response<TopicInfo> topicResponse = topicRequest.execute();
                        LOGGER.info("Message from entity", topicResponse.message());
                        if (!topicResponse.isSuccessful()) {
                            LOGGER.error("unable to fetch Content And Program details {}", topicResponse.errorBody().string());
                        } else {
                            details = topicResponse.body();
                        }
                        templateDtos.add(new TemplateDto(session.getId(), session.getSessionName(), session.getSessionStartDate(), session.getSessionEndDate(), session.getTrainingOrganization(), userDetails.getPhoto(), null != userDetails.getName() ? userDetails.getName() : "", role.getRole().equalsIgnoreCase(Constants.OTHER_ROLE_LABEL)?role.getOtherRoleName():role.getRole(), role.getUserId(), session.getProgramName(), attendanceRepository.numberOfParticipantsAttendedSession(session.getId()),details.getProgram().getEntityName()!=null?details.getProgram().getEntityName():""));
                    } else {

                        throw new UserDataNotFound("the user data is not available");
                    }
                } catch (Exception e) {
                    Failedattestation failedattestation = new Failedattestation();
                    failedattestation.setEvent_type("Failed Attestation");
                    failedattestation.setProgram_name(session.getProgramName());
                    failedattestation.setRole(role.getRole());
                    failedattestation.setSession_start_date(session.getSessionStartDate());
                    failedattestation.setSession_end_date(session.getSessionEndDate());
                    failedattestation.setNumber_of_participants(attendanceRepository.numberOfParticipantsAttendedSession(session.getId()));
                    failedattestation.setSession_id(session.getId());
                    failedattestation.setTopic_name(session.getSessionName());
                    failedattestation.setUser_id(role.getUserId());
                    failedattestation.setReason("Error logged while fetching user data");
                    failedrecords = failedrecords + 1;
                    failedattestationRepository.save(failedattestation);
                }
                sendNotification(role.getRole(), role.getUserId(), session, role.getOtherRoleName());
            }

        }
        try {
            Call<ResponseDTO> userRequest = entityDao.getMultiAttestationDetails(templateDtos);
            retrofit2.Response<ResponseDTO> userResponse = userRequest.execute();
            if (!userResponse.isSuccessful()) {
                LOGGER.error("unable to fetch user details {}", userResponse.errorBody().string());
            }
            ResponseDTO responseDTO = userResponse.body();
            if (responseDTO.getResponseCode() == HttpStatus.SC_OK) {
                saveAttestationUrl((List<Map<String, String>>) responseDTO.getResponse());
              cleverTapEvent((List<Map<String, String>>) responseDTO.getResponse(), validSessions);
                return (List<Map<String, String>>) responseDTO.getResponse();
            }
        } catch (Exception e) {
            failedrecords=failedrecords+1;
            Failedattestation failedattestation1 = new Failedattestation();
            failedattestation1.setEvent_type("Failed Attestation");
            failedattestation1.setReason("Error logged while fetching user data");
            failedrecords=failedrecords+1;
            failedattestationRepository.save(failedattestation1);
        }
        LOGGER.info("Generate Attestation");
        return new ArrayList<>();
    }

    private ResponseDTO saveAttestationUrl(List<Map<String, String>> attestations) {
        ResponseDTO responseDTO = new ResponseDTO();
        if (attestations != null && !attestations.isEmpty()) {
            for (Map<String, String> att : attestations) {
                Attendance attendance = new Attendance();
                attendance.setUserId(att.get("userId"));
                attendance.setSessionId(Long.parseLong(att.get("sessionId")));
                attendance.setAttestationUrl(att.get("attestationUrl"));
                attendance.setScanInDateTime(sessionRepository.findScanInBySessionId(Long.parseLong(att.get("sessionId"))));
                attendance.setScanOutDateTime(sessionRepository.findScanOutBySessionId(Long.parseLong(att.get("sessionId"))));
                attendance.setRole(att.get("role"));
                attendance.setScanOut(true);
                attendance.setScanIn(true);
                try {
                    attendanceRepository.save(attendance);
                    attendanceService.sendAttestationsToRegistry(att.get("userId"),attendance,null,Constants.MEMBER);
                    succcessrecords=succcessrecords+1;
                } catch (Exception e) {
                    Failedattestation failedattestation = new Failedattestation();
                    failedattestation.setReason("Error occured while saving the attendance ");
                    failedattestation.setUser_id(att.get("userId"));
                    failedattestation.setSession_id(Long.parseLong(att.get("sessionId")));
                    failedattestation.setRole(att.get("role"));
                    failedrecords=failedrecords+1;
                    failedattestationRepository.save(failedattestation);
                }
                LOGGER.info("Saving attestation in attendace for member: {} " , att.get("userId"));
            }
        }
        return responseDTO;
    }


    private void sendNotification(String role, String userId, Session session, String otherRole) {
        LocalDateTime now = LocalDateTime.now();
        if (null != role && !role.isEmpty() && null != userId && !userId.isEmpty()) {
            if (role.equalsIgnoreCase(Constants.TRAINER_LABEL)) {
                NotificationDTO notificationDTO = new NotificationDTO(null, Constants.ATTESTATION_RECEIVED, session.getId(), "Description", NotificationEvents.ATTESTATION.toString(), now.toLocalDate().toString() + " " + now.toLocalTime().toString(), false, role);
                notificationDTO.setRole(Constants.TRAINER_LABEL);
                notificationService.saveNotification(userId, session, notificationDTO);
            } else if (role.equalsIgnoreCase(Constants.ADMIN_LABEL)) {
                NotificationDTO notificationDTO = new NotificationDTO(null, Constants.ATTESTATION_RECEIVED, session.getId(), "Description", NotificationEvents.ATTESTATION.toString(), now.toLocalDate().toString() + " " + now.toLocalTime().toString(), false, role);
                notificationDTO.setRole(Constants.ADMIN_LABEL);
                notificationService.saveNotification(userId, session, notificationDTO);
            } else if (role.equalsIgnoreCase(Constants.OTHER_ROLE_LABEL)) {
                NotificationDTO notificationDTO = new NotificationDTO(null, Constants.ATTESTATION_RECEIVED, session.getId(), "Description", NotificationEvents.ATTESTATION.toString(), now.toLocalDate().toString() + " " + now.toLocalTime().toString(), false, role);
                notificationDTO.setRole(otherRole);
                notificationService.saveNotification(userId, session, notificationDTO);
            }

        }
    }
    private ResponseDTO cleverTapEvent(List<Map<String, String>> attestations, List<Long> sessionIdsWithAtleastOneParticipant) throws IOException {
        if (attestations != null && !attestations.isEmpty() && sessionIdsWithAtleastOneParticipant != null && !sessionIdsWithAtleastOneParticipant.isEmpty()) {
            List<CleverTapEventData> collection = new ArrayList<>();


            for (Long session : sessionIdsWithAtleastOneParticipant) {
                Session session1 = sessionRepository.findByIds(session);
                long count1 = attendanceRepository.numberOfParticipantsAttendedSession(session);
                for (Map<String, String> attestation : attestations) {
                    if (attestation.get("sessionId").equalsIgnoreCase(session.toString())) {
                        Session dbsession = sessionRepository.findByIds(session);
                        Attendance attendance = attendanceRepository.findByUserIdAndSessionId(attestation.get("userId"), dbsession.getId(), attestation.get("role"));
                        long count = attendanceRepository.numberOfParticipantsAttendedSession(session);
                        TopicInfo details;
                        Call<TopicInfo> topicRequest = entityDao.topicDetailWithProgramContentDTO(dbsession.getTopicId(), true);

                        retrofit2.Response<TopicInfo> topicResponse = topicRequest.execute();
                        if (!topicResponse.isSuccessful()) {
                            LOGGER.error("unable to fetch Content And Program details {}", topicResponse.errorBody().string());
                            return HttpUtils.onFailure(org.springframework.http.HttpStatus.NOT_FOUND.value(), "Content And Program details Not Available");
                        } else {
                            details = topicResponse.body();
                        }
                        try {
                            CleverTapEventData generateAttestationData = new CleverTapEventData();
                            generateAttestationData.setEventType("Generate Attestation");
                            generateAttestationData.setUserId(attestation.get("userId"));
                            generateAttestationData.setSessionId(session);
                            generateAttestationData.setSessionName(dbsession.getSessionName());
                            generateAttestationData.setTopicId(dbsession.getTopicId());
                            generateAttestationData.setSessionStartDate(dbsession.getSessionStartDate());
                            generateAttestationData.setSessionEndDate(dbsession.getSessionEndDate());
                            generateAttestationData.setProgramId(details.getTopic().getProgramId());
                            generateAttestationData.setTopicName(details.getTopic().getName());
                            generateAttestationData.setProgramName(dbsession.getProgramName());
                            generateAttestationData.setRole(attestation.get("role"));
                            generateAttestationData.setScanInTime(attendance.getScanInDateTime());
                            generateAttestationData.setScanOutTime(attendance.getScanOutDateTime());
                            generateAttestationData.setNoOfParticipants(count);
                            generateAttestationData.setAttestationUrl(attestation.get("attestationUrl"));
                            generateAttestationData.setTimestamp(LocalDateTime.now().toString());
                            collection.add(generateAttestationData);
                        } catch (Exception e) {
                            LOGGER.info("Error logged while saving data to telemetry");
                        }

                        LOGGER.info("Generating clever tap event for attestation for user: {} ",attestation.get("userId"));

                    }
                }try {
                    TopicInfo details;
                    Call<TopicInfo> topicRequest = entityDao.topicDetailWithProgramContentDTO(session1.getTopicId(), true);

                    retrofit2.Response<TopicInfo> topicResponse = topicRequest.execute();
                    if (!topicResponse.isSuccessful()) {
                        LOGGER.error("unable to fetch Content And Program details {}", topicResponse.errorBody().string());
                        return HttpUtils.onFailure(org.springframework.http.HttpStatus.NOT_FOUND.value(), "Content And Program details Not Available");
                    } else {
                        details = topicResponse.body();
                    }
                    CleverTapEventData sessionCompletedData = new CleverTapEventData();
                    sessionCompletedData.setEventType("Session Completed");
                    sessionCompletedData.setSessionId(session);
                    sessionCompletedData.setSessionName(session1.getSessionName());
                    sessionCompletedData.setTopicId(session1.getTopicId());
                    sessionCompletedData.setTopicName(details.getTopic().getName());
                    sessionCompletedData.setSessionStartDate(session1.getSessionStartDate());
                    sessionCompletedData.setSessionEndDate(session1.getSessionEndDate());
                    sessionCompletedData.setProgramId(details.getTopic().getProgramId());
                    sessionCompletedData.setProgramName(session1.getProgramName());
                    sessionCompletedData.setNoOfParticipants(count1);
                    collection.add(sessionCompletedData);
                } catch (Exception e) {
                    LOGGER.info("Error while saving session completed data");
                }
                LOGGER.info("Generating clever tap event for attestation for sessionname {} and session id {} ", session1.getSessionName(), session1.getId());
            }
            LOGGER.info("COllection Size:{}",collection.size());
            ObjectMapper objectMapper=new ObjectMapper();
            LOGGER.info(objectMapper.writeValueAsString(collection));


            CleverTapRequestWrapper cleverTapData = new CleverTapRequestWrapper();
            cleverTapData.setData(collection);

            Call<List<CleverTapEventData>> userRequest = entityDao.addMultiple(collection);
            retrofit2.Response<List<CleverTapEventData>> userResponse = userRequest.execute();
            if (!userResponse.isSuccessful()) {
                LOGGER.error("unable to fetch user details {}", userResponse.errorBody().string());
                return HttpUtils.onFailure(org.springframework.http.HttpStatus.NOT_FOUND.value(), "No Data Posted to Telemetry for EVENT:Generate Attestation and Session Completed");
            }
            return HttpUtils.success(cleverTapService.uploadMultipleEvent(collection), "Generated Event");
        }
        LOGGER.info("Clever Tap");
        return HttpUtils.onSuccess(HttpStatus.SC_OK, "No Attestations Generated");
    }

}

