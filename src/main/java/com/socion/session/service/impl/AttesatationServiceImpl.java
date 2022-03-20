package com.socion.session.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;


import com.socion.session.config.AppContext;
import com.socion.session.dao.Attendance;
import com.socion.session.dao.Session;
import com.socion.session.dao.SessionLinks;
import com.socion.session.dao.SessionRole;
import com.socion.session.dto.AttestationDto;
import com.socion.session.dto.ResponseDTO;
import com.socion.session.dto.v2.*;
import com.socion.session.dto.v2.*;
import com.socion.session.facade.EntityDao;
import com.socion.session.repository.AttendanceRepository;
import com.socion.session.repository.SessionLinksRepository;
import com.socion.session.repository.SessionRepository;
import com.socion.session.repository.SessionRoleRepository;
import com.socion.session.service.AttestationService;
import com.socion.session.service.v2.UserService;
import com.socion.session.utils.Constants;
import com.socion.session.utils.DateUtil;
import com.socion.session.utils.HttpUtils;
import com.socion.session.utils.KeycloakUtil;
import org.keycloak.common.VerificationException;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class AttesatationServiceImpl implements AttestationService {

    @Autowired
    AppContext appContext;

    @Autowired
    UserService userService;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    SessionRoleRepository sessionRoleRepository;

    @Autowired
    EntityDao entityDao;

    @Autowired
    SessionLinksRepository sessionLinksRepository;


    private static final Logger LOGGER = LoggerFactory.getLogger(AttesatationServiceImpl.class);

    @Override
    public ResponseDTO getAttestationsForLoggedInUser(String accessToken) {
        boolean isTokenActive = false;
        try {
            isTokenActive = KeycloakUtil.verifyToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            String userId = KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());

            if (isTokenActive) {
                List<Attendance> attendanceList = attendanceRepository.findByUserIdAndScannedOut(userId);
                if (null != attendanceList && !attendanceList.isEmpty()) {
                    List<Long> sessionIds = new ArrayList<>();
                    for (Attendance attendance : attendanceList) {
                        sessionIds.add(attendance.getSessionId());
                    }
                    List<Session> sessions = sessionRepository.findByIdIn(userId);
                    List<AttestationDto> attestationDtos = transformAttestationList(attendanceList, sessions);
                    return HttpUtils.success(attestationDtos, "Returning list of Attestations");
                }
                return HttpUtils.success(new ArrayList<>(), "No Attestations");
            }
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);
        }
        return HttpUtils.onFailure(HttpStatus.UNAUTHORIZED.value(), "Invalid access token");

    }

    @Override
    public List<SessionidsattestationcountDTO> getnumberofattestations(SessionIdsDTO sessionIdsDTO) {
        List<SessionidsattestationcountDTO> sessionidsattestationcountDTOS = new ArrayList<>();
        for (Long sessionid : sessionIdsDTO.getSessionIds()) {
            {
                SessionidsattestationcountDTO sessionidsattestationcountDTO = new SessionidsattestationcountDTO();
                Long countattestation = attendanceRepository.numberofattestationspersession(sessionid);
                Long membersCount = sessionRoleRepository.getMembersCount(sessionid);
                Long participantCount = attendanceRepository.getAllParticipantCount(sessionid);
                sessionidsattestationcountDTO.setParticipantCount(participantCount);
                sessionidsattestationcountDTO.setAttestationCount(countattestation);
                sessionidsattestationcountDTO.setSessionId(sessionid);
                sessionidsattestationcountDTO.setMembersCount(membersCount);
                sessionidsattestationcountDTOS.add(sessionidsattestationcountDTO);
            }
        }
        return sessionidsattestationcountDTOS;
    }

    @Override
    public ResponseDTO getAttestationsWithSessionInfoForLoggedInUser(String accessToken) throws IOException {
        LOGGER.info("+++++++++++++++++++++++++++API CALL:/my-attestations-session-info++++++++++++++++++++++++++++++++++");
        boolean isTokenActive = false;
        try {
            isTokenActive = KeycloakUtil.verifyToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            String loggedInUserId = KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            if (isTokenActive) {
                List<Attendance> attendanceList = attendanceRepository.findByUserIdAndScannedOut(loggedInUserId);
                if (null != attendanceList && !attendanceList.isEmpty()) {
                    List<Long> sessionIds = new ArrayList<>();
                    for (Attendance attendance : attendanceList) {
                        sessionIds.add(attendance.getSessionId());
                    }
                    List<Session> sessions = sessionRepository.findByIdList(sessionIds);
                    Map<Long, Session> sessionMap = prepareMap(sessions);
                    List<SessionOldDtoV2> sessionOldDtos = new ArrayList<>();
                    for (Attendance attendance : attendanceList) {
                        if(sessionMap.get(attendance.getSessionId()) != null) {
                            sessionOldDtos.add(getCompleteSessionInfoForAttestation(sessionMap.get(attendance.getSessionId()), loggedInUserId, attendance));
                        }
                    }
                    return HttpUtils.success(sessionOldDtos, "Returning list of Attestations");
                }
                return HttpUtils.success(new ArrayList<>(), "No Attestations");
            }
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);
        }
        return HttpUtils.onFailure(HttpStatus.UNAUTHORIZED.value(), "Invalid access token");

    }

    @Override
    public ResponseDTO getAttestationsWithSessionInfoForLoggedInUserOptimized(String accessToken) throws IOException {
        LOGGER.info("+++++++++++++++++++++++++++API CALL:/my-attestations-session-info++++++++++++++++++++++++++++++++++");
        boolean isTokenActive = false;
        try {
            isTokenActive = KeycloakUtil.verifyToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            String loggedInUserId = KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            if (isTokenActive) {
                List<Attendance> attendanceList = attendanceRepository.findByUserIdAndScannedOut(loggedInUserId);
                List<Long> sessionIds = attendanceRepository.findSessionIdByUserIdAndScannedOut(loggedInUserId);
                List<BigInteger> allDistinctTopicIds = sessionRepository.findByTopicIdList(sessionIds);
                 List<Long> topicIds = new ArrayList<>();
                allDistinctTopicIds.forEach(topicId -> topicIds.add(topicId.longValue()));
                TopicIdsDTO topicIdsDTO = new TopicIdsDTO();
                topicIdsDTO.setTopicIds(topicIds);
                List<Map<String,Object>> topicIdsData = null;
                Call<ResponseDTO> userRequest = entityDao.multipleTopicDetailWithProgramContentDTO(topicIdsDTO);
                retrofit2.Response userResponse = userRequest.execute();
                if (!userResponse.isSuccessful()) {
                    LOGGER.error("unable to fetch Content And Program details {}", userResponse.errorBody().string());
                } else {
                     ResponseDTO responseDTO = (ResponseDTO) userResponse.body();
            topicIdsData = (List<Map<String,Object>>) responseDTO.getResponse();
                }

                if (null != attendanceList && !attendanceList.isEmpty()) {
                    List<Session> sessions = sessionRepository.findByIdList(sessionIds);
                    Map<Long, Session> sessionMap = prepareMap(sessions);
                    List<SessionOldDtoV2> sessionOldDtos = new ArrayList<>();
                    List<Long> allSessionIds = new ArrayList<>();
                    for (Session session : sessions) {
                        allSessionIds.add(session.getId());
                    }
                    List<String> allUserIdsRelatedToSession = sessionRoleRepository.findUserIdOfMembersRelatedToSession(allSessionIds);
                    ResponseDTO responseDTO = userService.getAllUserDetails(allUserIdsRelatedToSession, loggedInUserId, false);
                    if (responseDTO.getResponseCode() != HttpStatus.OK.value()) {
                        return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "Error while fetching user Details");
                    }
                    List<RegistryUserWithOsId> userDetails = (List<RegistryUserWithOsId>) responseDTO.getResponse();

		    List<TopicInfo> topicInfoList = new ArrayList<>();
		    ObjectMapper objectMapper=new ObjectMapper();
                    topicIdsData.forEach(element -> {
                        element.forEach((k,v) -> {
                            LinkedHashMap linkedHashMap = (LinkedHashMap) v;
                            topicInfoList.add(objectMapper.convertValue(linkedHashMap, TopicInfo.class));
                        });
                    });
                    for (Attendance attendance : attendanceList) {
                        if(sessionMap.get(attendance.getSessionId()) != null) {
                            sessionOldDtos.add(getCompleteSessionInfoForAttestationOptimized(sessionMap.get(attendance.getSessionId()), loggedInUserId, attendance, topicInfoList, userDetails));
                        }
                    }
                    return HttpUtils.success(sessionOldDtos, "Returning list of Attestations");
                }
                return HttpUtils.success(new ArrayList<>(), "No Attestations");
            }
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);
        }
        return HttpUtils.onFailure(HttpStatus.UNAUTHORIZED.value(), "Invalid access token");

    }

    public SessionOldDtoV2 getCompleteSessionInfoForAttestationOptimized(Session session, String loogedInUserID, Attendance attendance, List<TopicInfo> topicIdsData, List<RegistryUserWithOsId> userDetails) throws IOException {
        SessionOldDtoV2 sessionOldDtoV2;
        sessionOldDtoV2 = convertSessionEntityToDto(session);
        sessionOldDtoV2.setIsSessionCreator(session.getSessionCreator().equalsIgnoreCase(loogedInUserID));
        sessionOldDtoV2.setProgramName(session.getProgramName());
        try {
            List<SessionRole> sessionRoles = sessionRoleRepository.findMembersRelatedToSession(Arrays.asList(session.getId()));
            List<MemberOldDtoV2> membersOldList = new ArrayList<>();
            List<MemberDtoV2> memberDtos = new ArrayList<>();
            for (SessionRole sessionRole : sessionRoles) {
                MemberOldDtoV2 sameMember = memberExistInList(sessionRole.getUserId(), membersOldList);
                if (sameMember != null) {
                    String role = sessionRole.getRole();
                    MemberRoleDto memberRoleDto = sameMember.getRoles();
                    if (null != role && !role.isEmpty() && role.equalsIgnoreCase(Constants.TRAINER)) {
                        memberRoleDto.setTrainer(true);
                    }
                    if (null != role && !role.isEmpty() && role.equalsIgnoreCase(Constants.ADMIN_LABEL)) {
                        memberRoleDto.setAdmin(true);
                    }
                    if (null != role && !role.isEmpty() && role.equalsIgnoreCase(Constants.OTHER)) {
                        memberRoleDto.setOther(true);
                        memberRoleDto.setOtherRoleNames(sessionRole.getOtherRoleName());
                    }
                    continue;
                }
                MemberDtoV2 memberDtoV2 = new MemberDtoV2();
                memberDtoV2.setRole(Arrays.asList(sessionRole.getRole()));
                memberDtoV2.setUserId(sessionRole.getUserId());
                memberDtoV2.setSessionId(session.getId());
                memberDtoV2.setTopicId(session.getTopicId());
                ResponseDTO responseDTO = userService.getUserDetailsFromList(userDetails, sessionRole.getUserId());
                User user = (User) responseDTO.getResponse();
                sessionOldDtoV2.setUser(user);
                memberDtoV2.setName(user.getName());
                memberDtoV2.setPhoto(user.getPhoto());
                if (sessionRole.getRoleDescription() != null && !sessionRole.getRoleDescription().isEmpty()) {
                    memberDtoV2.setRoleDescription(sessionRole.getRoleDescription());
                }
                if (sessionRole.getOtherRoleName() != null && !sessionRole.getOtherRoleName().isEmpty()) {
                    memberDtoV2.setOtherRoleDescription(sessionRole.getOtherRoleName());
                }
                memberDtos.add(memberDtoV2);
                membersOldList.add(responseMemberDTO(memberDtoV2));
            }
            List<SessionLinks> sessionLinks = sessionLinksRepository.findSessionLinksBySessionid(session.getId());
            List<SessionLinksDTO> sessionLinksDTO = new ArrayList<>();
            for (SessionLinks sessionLink : sessionLinks) {
                sessionLinksDTO.add(new SessionLinksDTO(sessionLink.getId(), sessionLink.getSession().getId(), sessionLink.getSessionUrl()));
            }
            sessionOldDtoV2.setSessionLinks(sessionLinksDTO);
            sessionOldDtoV2.setMembers(membersOldList);
            sessionOldDtoV2.setStartQrcode(session.getStartQrcode());
            sessionOldDtoV2.setEndQrcode(session.getEndQrcode());


        } catch (Exception exception) {
            LOGGER.error("Error while trying to fetch user detail ,type of rel.: {} because of exception : {}", session.getId(), exception.getMessage());
        }
        TopicInfo details = new TopicInfo();
        for (TopicInfo topic : topicIdsData) {
            if (session.getTopicId().intValue() == (topic.getTopic().getId().intValue())) {
                details = topic;
            }
        }
        List<ContentDTO> contentlist = details.getContent();
        for (ContentDTO content : contentlist) {
            if (content.getVimeo_url() != null) {
                content.setVimeoId(content.getVimeo_url().substring(18));
            }
        }
        sessionOldDtoV2.setTopicInfo(details);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date sessionStartDate;
        try {
            sessionStartDate = null != session.getSessionStartDate() ? format.parse(session.getSessionStartDate()) : null;
            boolean isLive = DateUtil.isCurrentDateTimeAfterADate(sessionStartDate.getTime(), "0");
            sessionOldDtoV2.setSessionProgress(isLive ? Constants.SESSION_STATUS_LIVE : Constants.SESSION_STATUS_UPCOMING);
        } catch (ParseException e) {
            LOGGER.error("EXCEPTION:{}", e.getMessage());
        }
        String qRCodeUrl = appContext.getNginxServerName() + "/attestation/" + session.getId() + "/" + loogedInUserID + "/" + attendance.getRole();
        sessionOldDtoV2.setQrCodeUrl(qRCodeUrl);
        Attendance attendancea = attendanceRepository.findByUserIdAndSessionId(loogedInUserID, session.getId(), attendance.getRole());
        if (attendancea != null) {
            sessionOldDtoV2.setAttestationUrl(attendancea.getAttestationUrl());
        }
        String role = attendance.getRole();
        if (role.equalsIgnoreCase("OTHER")) {
            sessionOldDtoV2.setRole(sessionRoleRepository.getOtherRoleNameOfUserForASession(session.getId(), loogedInUserID));
        } else {
            sessionOldDtoV2.setRole(role);
        }
        if (role != null && !Constants.TRAINEE.equalsIgnoreCase(role)) {
            int num = attendanceRepository.numberOfParticipantsAttendedSession(session.getId());
            sessionOldDtoV2.setNumberOfParticipants(num);
        }
        sessionOldDtoV2.setAttestationDate(attendance.getScanOutDateTime());
        return sessionOldDtoV2;
    }


    public SessionOldDtoV2 getCompleteSessionInfoForAttestation(Session session, String loogedInUserId, Attendance attendance) throws IOException {
        SessionOldDtoV2 sessionOldDtoV2;
        sessionOldDtoV2 = convertSessionEntityToDto(session);
        sessionOldDtoV2.setIsSessionCreator(session.getSessionCreator().equalsIgnoreCase(loogedInUserId));
        sessionOldDtoV2.setProgramName(session.getProgramName());
        try {
            List<SessionRole> sessionRoles = sessionRoleRepository.findMembersRelatedToSession(Arrays.asList(session.getId()));
            List<MemberOldDtoV2> membersOldList = new ArrayList<>();
            List<MemberDtoV2> memberDtos = new ArrayList<>();
            TopicInfo details = new TopicInfo();
            Call<TopicInfo> userRequest = entityDao.topicDetailWithProgramContentDTO(session.getTopicId(), true);

            retrofit2.Response userResponse = userRequest.execute();
            if (!userResponse.isSuccessful()) {
                LOGGER.error("unable to fetch Content And Program details {}", userResponse.errorBody().string());
            } else {
                details = (TopicInfo) userResponse.body();
            }
            sessionOldDtoV2.setTopicInfo(details);
            for (SessionRole sessionRole : sessionRoles) {
                MemberOldDtoV2 sameMember = memberExistInList(sessionRole.getUserId(), membersOldList);
                if (sameMember != null) {
                    String role = sessionRole.getRole();
                    MemberRoleDto memberRoleDto = sameMember.getRoles();
                    if (null != role && !role.isEmpty() && role.equalsIgnoreCase(Constants.TRAINER)) {
                        memberRoleDto.setTrainer(true);

                    }
                    if (null != role && !role.isEmpty() && role.equalsIgnoreCase(Constants.ADMIN_LABEL)) {
                        memberRoleDto.setAdmin(true);
                    }
                    if (null != role && !role.isEmpty() && role.equalsIgnoreCase(Constants.OTHER)) {
                        memberRoleDto.setOther(true);
                        memberRoleDto.setOtherRoleNames(sessionRole.getOtherRoleName());
                    }
                    continue;
                }
                MemberDtoV2 memberDtoV2 = new MemberDtoV2();
                memberDtoV2.setRole(Arrays.asList(sessionRole.getRole()));
                memberDtoV2.setUserId(sessionRole.getUserId());
                memberDtoV2.setSessionId(session.getId());
                memberDtoV2.setTopicId(session.getTopicId());
                ResponseDTO responseDTO = userService.getUserDetails(sessionRole.getUserId(), loogedInUserId, false);
                User user = (User) responseDTO.getResponse();
                sessionOldDtoV2.setUser(user);
                memberDtoV2.setName(user.getName());
                memberDtoV2.setPhoto(user.getPhoto());
                if (sessionRole.getRoleDescription() != null && !sessionRole.getRoleDescription().isEmpty()) {
                    memberDtoV2.setRoleDescription(sessionRole.getRoleDescription());
                }
                if (sessionRole.getOtherRoleName() != null && !sessionRole.getOtherRoleName().isEmpty()) {
                    memberDtoV2.setOtherRoleDescription(sessionRole.getOtherRoleName());
                }
                memberDtos.add(memberDtoV2);
                membersOldList.add(responseMemberDTO(memberDtoV2));
            }
            List<SessionLinks> sessionLinks = sessionLinksRepository.findSessionLinksBySessionid(session.getId());
            List<SessionLinksDTO> sessionLinksDTO = new ArrayList<>();
            for (SessionLinks sessionLink : sessionLinks) {
                sessionLinksDTO.add(new SessionLinksDTO(sessionLink.getId(), sessionLink.getSession().getId(), sessionLink.getSessionUrl()));
            }
            sessionOldDtoV2.setSessionLinks(sessionLinksDTO);
            sessionOldDtoV2.setMembers(membersOldList);
            sessionOldDtoV2.setStartQrcode(session.getStartQrcode());
            sessionOldDtoV2.setEndQrcode(session.getEndQrcode());


        } catch (Exception exception) {
            LOGGER.error("Error while trying to fetch user detail ,type of rel.: {} because of exception : {}", session.getId(), exception.getMessage());
        }
        TopicInfo details = new TopicInfo();
        Call<TopicInfo> userRequest = entityDao.topicDetailWithProgramContentDTO(session.getTopicId(), true);
        retrofit2.Response userResponse = userRequest.execute();
        if (!userResponse.isSuccessful()) {
            LOGGER.error("unable to fetch Content And Program details {}", userResponse.errorBody().string());
        } else {
            details = (TopicInfo) userResponse.body();
        }
        List<ContentDTO> contentlist = details.getContent();
        for (ContentDTO content : contentlist) {
            if (content.getVimeo_url() != null) {
                content.setVimeoId(content.getVimeo_url().substring(18));
            }
        }
        sessionOldDtoV2.setTopicInfo(details);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date sessionStartDate;
        try {
            sessionStartDate = null != session.getSessionStartDate() ? format.parse(session.getSessionStartDate()) : null;
            boolean isLive = DateUtil.isCurrentDateTimeAfterADate(sessionStartDate.getTime(), "0");
            sessionOldDtoV2.setSessionProgress(isLive ? Constants.SESSION_STATUS_LIVE : Constants.SESSION_STATUS_UPCOMING);
        } catch (ParseException e) {
            LOGGER.error("EXCEPTION:{}", e.getMessage());
        }
        String qRCodeUrl = appContext.getNginxServerName() + "/attestation/" + session.getId() + "/" + loogedInUserId + "/" + attendance.getRole();
        sessionOldDtoV2.setQrCodeUrl(qRCodeUrl);
        Attendance attendancea = attendanceRepository.findByUserIdAndSessionId(loogedInUserId, session.getId(), attendance.getRole());
        if (attendancea != null) {
            sessionOldDtoV2.setAttestationUrl(attendancea.getAttestationUrl());
        }
        String role = attendance.getRole();
        if (role.equalsIgnoreCase("OTHER")) {
            sessionOldDtoV2.setRole(sessionRoleRepository.getOtherRoleNameOfUserForASession(session.getId(), loogedInUserId));
        } else {
            sessionOldDtoV2.setRole(role);
        }
        if (role != null && !Constants.TRAINEE.equalsIgnoreCase(role)) {
            int num = attendanceRepository.numberOfParticipantsAttendedSession(session.getId());
            sessionOldDtoV2.setNumberOfParticipants(num);
        }
        sessionOldDtoV2.setAttestationDate(attendance.getScanOutDateTime());
        return sessionOldDtoV2;
    }

    private MemberOldDtoV2 memberExistInList(String userId, List<MemberOldDtoV2> dtoV2s) {
        if (null != dtoV2s && !dtoV2s.isEmpty()) {
            Optional<MemberOldDtoV2> member = dtoV2s.stream().filter(m -> userId.equals(m.getUserId())).findFirst();
            return member.isPresent() ? member.get() : null;
        }
        return null;

    }

    private MemberOldDtoV2 responseMemberDTO(MemberDtoV2 memberDtoV2) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<MemberDtoV2, MemberOldDtoV2>() {
            protected void configure() {
                map(source.getUserId(), destination.getUserId());
                map(source.getSessionId(), destination.getSessionId());
                map(source.getTopicId(), destination.getTopicId());
                map(source.getRoleDescription(), destination.getRoleDescription());
                map(source.getPhoto(), destination.getPhoto());
                map(source.getName(), destination.getName());
            }
        });

        MemberOldDtoV2 memberOldDtoV2 = modelMapper.map(memberDtoV2, MemberOldDtoV2.class);
        MemberRoleDto memberRoleDto = new MemberRoleDto(false, false, false);
        for (String role : memberDtoV2.getRole()) {

            if (null != role && !role.isEmpty() && role.equalsIgnoreCase("TRAINER")) {
                memberRoleDto.setTrainer(true);

            }
            if (null != role && !role.isEmpty() && role.equalsIgnoreCase("ADMIN")) {
                memberRoleDto.setAdmin(true);
            }
            if (null != role && !role.isEmpty() && role.equalsIgnoreCase("OTHER")) {
                memberRoleDto.setOther(true);
            }
            if (null != memberDtoV2.getOtherRoleDescription() && !memberDtoV2.getOtherRoleDescription().isEmpty()) {
                memberRoleDto.setOtherRoleNames(memberDtoV2.getOtherRoleDescription());
            }
        }
        memberOldDtoV2.setRoles(memberRoleDto);
        return memberOldDtoV2;

    }


    private SessionOldDtoV2 convertSessionEntityToDto(Session session) {
        SessionOldDtoV2 sessionDtoV2 = new SessionOldDtoV2();
        sessionDtoV2.setSessionId(session.getId());
        sessionDtoV2.setSessionName(session.getSessionName());
        sessionDtoV2.setSessionDescription(session.getSessionDescription());
        sessionDtoV2.setAddress(session.getAddress());
        sessionDtoV2.setTopicId(session.getTopicId());
        sessionDtoV2.setSessionCreator(session.getSessionCreator());
        sessionDtoV2.setSessionEndDate(session.getSessionEndDate());
        sessionDtoV2.setSessionStartDate(session.getSessionStartDate());
        return sessionDtoV2;
    }

    private List<AttestationDto> transformAttestationList(List<Attendance> attendances, List<Session> sessions) {
        List<AttestationDto> attestationDtos = new ArrayList<>();
        Map<Long, Session> sessionMap = prepareMap(sessions);
        for (Attendance attendance : attendances) {
            Session session = sessionMap.get(attendance.getSessionId());
            if (null != session) {
                String role = "TRAINEE".equals(attendance.getRole()) ? "TRAINEE" : "MEMBER";
                String awsUrl = appContext.getNginxServerName() + "/attestation/" + session.getId() + "/" + attendance.getUserId() + "/" + role;
                AttestationDto attestationDto = new AttestationDto(attendance.getSessionId(), session.getSessionName(), session.getTrainingOrganization(), session.getAddress(), attendance.getScanOutDateTime(), awsUrl, attendance.getAttestationUrl(), attendance.getRole());
                attestationDtos.add(attestationDto);
            }
        }
        return attestationDtos;
    }


    private Map<Long, Session> prepareMap(List<Session> sessions) {
        HashMap<Long, Session> sessionMap = new HashMap<>();
        for (Session session : sessions) {
            sessionMap.put(session.getId(), session);
        }
        return sessionMap;
    }
}
