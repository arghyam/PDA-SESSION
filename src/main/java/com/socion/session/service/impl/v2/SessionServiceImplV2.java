package com.socion.session.service.impl.v2;


import com.socion.session.aws.AwsConfigService;
import com.socion.session.config.AppContext;
import com.socion.session.dao.Attendance;
import com.socion.session.dao.Session;
import com.socion.session.dao.SessionLinks;
import com.socion.session.dao.SessionRole;
import com.socion.session.dto.NotificationDTO;
import com.socion.session.dto.ResponseDTO;
import com.socion.session.dto.SessionResponseDTO;
import com.socion.session.dto.v2.*;
import com.socion.session.dto.v2.*;
import com.socion.session.facade.EntityDao;
import com.socion.session.repository.AttendanceRepository;
import com.socion.session.repository.SessionLinksRepository;
import com.socion.session.repository.SessionRepository;
import com.socion.session.repository.SessionRoleRepository;
import com.socion.session.service.NotificationService;
import com.socion.session.service.impl.AttesatationServiceImpl;
import com.socion.session.service.v2.SessionServiceV2;
import com.socion.session.service.v2.UserService;
import com.socion.session.utils.*;
import com.amazonaws.services.s3.AmazonS3;
import com.itextpdf.html2pdf.HtmlConverter;
import com.socion.session.utils.*;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import retrofit2.Call;
import retrofit2.Response;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class SessionServiceImplV2 implements SessionServiceV2 {

    public static final Logger LOGGER = LoggerFactory.getLogger(SessionServiceImplV2.class);

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    SessionRoleRepository sessionRoleRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    EntityDao entityDao;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    UserService userService;

    @Autowired
    AttesatationServiceImpl attesatationService;

    @Autowired
    SessionLinksRepository sessionLinksRepository;

    @Autowired
    AppContext appContext;

    @Autowired
    AwsConfigService awsConfigService;


    public ResponseDTO getSession(Long sessionId) {
        Session session;
        try {
            session = sessionRepository.findByIds(sessionId);
            return HttpUtils.success(session, "Successfully retrieved session");
        } catch (Exception e) {
            return HttpUtils.onFailure(500, "No Session Available for the session Id");
        }
    }

    public ResponseDTO createSession(SessionOldDtoV2 sessionOldDTO, BindingResult bindingResult) {
        SessionDtoV2 sessionDTO = sessionDTOManipulation(sessionOldDTO);
        try {
            if (isDatesMissing(sessionDTO)) {
                return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Session Start or End date is missing");
            }
            String[] startDatetime = sessionDTO.getSessionStartDateTime().split(" ");
            String[] endDatetime = sessionDTO.getSessionEndDateTime().split(" ");

            if (LocalDate.parse(startDatetime[0]).isAfter(LocalDate.parse(endDatetime[0]))) {
                return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Session Start Date must  be after Session EndDate");
            }
            if (LocalDate.parse(startDatetime[0]).equals(LocalDate.parse(endDatetime[0])) && LocalTime.parse(startDatetime[1]).isAfter(LocalTime.parse(endDatetime[1]))) {
                return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Session Start Time must  be after Session EndTime");

            }
            Session session = convertDtoToEntity(sessionDTO);
            if (!ifAllMembersRolesAreValid(sessionDTO.getMembers())) {
                return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), Constants.USER_ROLES_NOT_VALID);
            }
            session.setIs_deleted(false);
            if (OtherRolesDescriptionNeeded(sessionDTO.getMembers())) {
                return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "provide OtherRoleDescription for role:OTHER");
            }

            TopicInfo details;
            Call<TopicInfo> userRequest = entityDao.topicDetailWithProgramContentDTO(session.getTopicId(), true);
            retrofit2.Response userResponse = userRequest.execute();
            if (!userResponse.isSuccessful()) {
                LOGGER.error("unable to fetch Content And Program details {}", userResponse.errorBody().string());
                return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "Content And Program details Not Available");
            } else {
                details = (TopicInfo) userResponse.body();
            }
            session.setSessionTimeZone(sessionDTO.getSessionTimeZone());
            session.setProgramName(details.getProgram().getName());
            session.setTrainingOrganization("Socion");
            session.setProgramId(details.getTopic().getProgramId());
            String utcTime = TimeUtils.convertToUTCTImeZone(session.getSessionEndDate(), appContext.getSessionEndMinutes());

            if (!DateUtil.validateDateTimeFormatMS(utcTime)) {
                return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "Error while Converting Date and Time");
            }
            session.setSessionEndDateUtcTime(utcTime);
            Session s = sessionRepository.save(session);
            addMembers(sessionDTO.getMembers(), s);
            LocalDateTime dateTime = LocalDateTime.now();
            List<MemberDtoV2> sessionDTOMembers = sessionDTO.getMembers();
            for (MemberDtoV2 member : sessionDTOMembers) {
                notificationService.saveNotification(member.getUserId(), session, new NotificationDTO(null, Constants.ADD_MEMBER, null, null, NotificationEvents.SESSION.toString(), dateTime.toLocalDate().toString() + " " + dateTime.toLocalTime().toString(), false, null));
            }
            LOGGER.info("Session created successfully with name : {} and Id : {} ", s.getSessionName(), s.getId());
            session.setStartQrcode(generateQrCodeforsession(s.getId(), session.getSessionName()
                    , session.getSessionStartDate(), Constants.START_QR, "startDate", sessionDTO.getSessionTimeZone()).getResponse().toString());
            session.setEndQrcode(generateQrCodeforsession(s.getId(), session.getSessionName()
                    , session.getSessionEndDate(), Constants.END_QR, "endDate", sessionDTO.getSessionTimeZone()).getResponse().toString());

            TopicDTO topicDTO;

            Call<TopicDTO> topicRequest = entityDao.getTopicdetails(session.getTopicId());
            retrofit2.Response topicResponse = topicRequest.execute();
            topicDTO = (TopicDTO) topicResponse.body();
            if (topicDTO != null) {
                topicDTO.setSessionLinked(true);
            }
            topicDTO.setProgramId(details.getTopic().getProgramId());
            Call<TopicDTO> topicRequest1 = entityDao.update(topicDTO, topicDTO.getId());
            retrofit2.Response topicResponse1 = topicRequest1.execute();
            if (topicResponse1.isSuccessful()) {
                LOGGER.info("Topic session link updated");
            }
            notificationService.saveNotification(session.getSessionCreator(), session, new NotificationDTO(null, Constants.SESSION_CREATED, sessionDTO.getSessionId(), null, NotificationEvents.SESSION.toString(), dateTime.toLocalDate().toString() + " " + dateTime.toLocalTime().toString(), false, null));
            return HttpUtils.success(new SessionResponseDTO(s.getId()), "Session Created Successfully");
        } catch (Exception e) {
            LOGGER.error("Error creating session with name : {} ", sessionDTO.getSessionName());
            return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }


    private ResponseDTO generateQrCodeforsession(Long sessionId, String sessionName, String time, String qrType, String name, String sessionTimeZone) throws IOException, ParseException {
        String sessionNameReformatted = sessionName.replace(" ", "-");
        boolean value = new File(appContext.getSessionQrCodePath() + Constants.QR_CODES).mkdir();
        JSONObject obj = new JSONObject();
        String sessionName1;
        String sessionName2;
        String sessionName3;
        String finalName;
        if (sessionName.length() > 132) {
            finalName = sessionName.substring(0, 129) + "...";
        } else if (sessionName.length() > 88) {
            finalName = sessionName;
        } else {
            finalName = sessionName;
        }
        if (finalName.length() > 88) {
            sessionName1 = finalName.substring(0, 43);
            sessionName2 = finalName.substring(43, 87);
            sessionName3 = finalName.substring(87, finalName.length());
        } else if (finalName.length() > 44) {
            sessionName1 = finalName.substring(0, 43);
            sessionName2 = finalName.substring(43, finalName.length());
            sessionName3 = "";
        } else {
            sessionName1 = finalName;
            sessionName2 = "";
            sessionName3 = "";
        }

        String qrName = null;
        if (qrType.equalsIgnoreCase(Constants.START_QR)) {
            qrName = "startQR";
        }
        if (qrType.equalsIgnoreCase(Constants.END_QR)) {
            qrName = "endstartQR";
        }

        obj.put("type", qrName);
        obj.put("id", sessionId);
        obj.put(name, time);
        if (sessionName.length() > 73) {
            obj.put("sessionName", sessionName.substring(0, 70) + "...");
        } else {
            obj.put("sessionName", sessionName);
        }
        String tobeencrypted = obj.toString();
        LOGGER.info("SESSIONTIMEZONE:{}", sessionTimeZone);
        LOGGER.info("====================encrypted format=============== {}", tobeencrypted);
        LOGGER.info(appContext.getSessionQrCodePath() + "SessionQRcode.html");
        String htmlString = new String(Files.readAllBytes(Paths.get(appContext.getSessionQrCodePath() + "SessionQRcode.html")));
        AesUtil aesUtil = new AesUtil(appContext.getKeySize(), appContext.getIterationCount());
        String encryptedSessionData = aesUtil.encrypt(appContext.getSaltValue(), appContext.getIvValue(),
                appContext.getSecretKey(), tobeencrypted);

        ResponseDTO responseDTO = new ResponseDTO();
        ByteArrayOutputStream bout =
                QRCode.from(encryptedSessionData)
                        .withSize(800, 800)
                        .to(ImageType.PNG)
                        .stream();
        String pngFilePath = null;


        String formattedDateTime = TimeUtils.formatDateTime(time, sessionTimeZone);
        try {
            OutputStream out = new FileOutputStream(appContext.getSessionQrCodePath() + Constants.QR_CODES + sessionId + qrType + ".png");
            bout.writeTo(out);
            out.flush();
            out.close();
            pngFilePath = appContext.getSessionQrCodePath() + Constants.QR_CODES + sessionId + qrType + ".png";
            LOGGER.info("Successfully created Qr Code");
        } catch (FileNotFoundException e) {
            LOGGER.info("There is issue during creation Qr Code");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            htmlString = htmlString.replace("$qrcodepath", pngFilePath);
            htmlString = htmlString.replace("$sessionname1", sessionName1);
            htmlString = htmlString.replace("$sessionname2", sessionName2);
            htmlString = htmlString.replace("$sessionname3", sessionName3);

            htmlString = htmlString.replace("$type", qrType);
//            htmlString = htmlString.replace("$time", time);
            htmlString = htmlString.replace("$time", formattedDateTime);
            File newHtmlFile = new File(appContext.getSessionQrCodePath() + Constants.QR_CODES + sessionId + qrType + Constants.HTML_FORMAT);
            FileUtils.writeStringToFile(newHtmlFile, htmlString, StandardCharsets.UTF_8);
            ImageUtils.cropImage(pngFilePath);
            HtmlConverter.convertToPdf(new File(appContext.getSessionQrCodePath() + Constants.QR_CODES + sessionId + qrType + Constants.HTML_FORMAT), new File(appContext.getSessionQrCodePath() + Constants.QR_CODES + sessionId + qrType + Constants.PDF_FORMAT));

            LOGGER.info("uploading into aws...");
            String sessionDisplayName;
            if (sessionNameReformatted.length() > 20) {
                sessionDisplayName = sessionNameReformatted.substring(0, 20);
            } else {
                sessionDisplayName = sessionNameReformatted;
            }
            AmazonS3 amazonS3 = awsConfigService.awsS3Configuration();
            awsConfigService.putQrcodeInAwsS3(appContext.getSessionQrCodePath() + Constants.QR_CODES + sessionId + qrType + Constants.PDF_FORMAT, sessionId.toString(), qrType, amazonS3, sessionDisplayName);
            String s3BucketUrl = appContext.getAwsS3Url() + appContext.getAwsS3SessionQrFolderName() + "/" + sessionId + "-" + sessionDisplayName + "-" + qrType;
            responseDTO.setResponse(s3BucketUrl);
            responseDTO.setMessage("Successfully created Qr Code");
            responseDTO.setResponseCode(org.apache.http.HttpStatus.SC_OK);
            File htmlFile1 = new File(appContext.getSessionQrCodePath() + Constants.QR_CODES + sessionId + qrType + Constants.HTML_FORMAT);
            File htmlFile2 = new File(appContext.getSessionQrCodePath() + Constants.QR_CODES + sessionId + qrType + Constants.PDF_FORMAT);
            File htmlFile3 = new File(appContext.getSessionQrCodePath() + Constants.QR_CODES + sessionId + qrType + Constants.PNG_FORMAT);
            if (htmlFile1.delete() && htmlFile2.delete() && htmlFile3.delete()) {
                LOGGER.info("File deleted successfully");
            } else {
                LOGGER.error("Failed to delete the file");
            }
        } catch (
                FileNotFoundException e) {
            LOGGER.error(Constants.ERRORLOG + e);
            responseDTO.setMessage("There is issue during creation Qr Code");
            responseDTO.setResponseCode(org.apache.http.HttpStatus.SC_BAD_REQUEST);
        } catch (
                IOException e) {
            LOGGER.error(Constants.ERRORLOG + e);
        }

        return responseDTO;

    }


    private SessionDtoV2 sessionDTOManipulation(SessionOldDtoV2 sessionOldDtoV2) {

        SessionDtoV2 sessionDtoV2 = new SessionDtoV2();
        if (null != sessionOldDtoV2.getSessionId()) {
            sessionDtoV2.setSessionId(sessionOldDtoV2.getSessionId());
        }
        if (null != sessionOldDtoV2.getSessionName()) {
            sessionDtoV2.setSessionName(sessionOldDtoV2.getSessionName());
        }
        if (null != sessionOldDtoV2.getSessionDescription()) {
            sessionDtoV2.setSessionDescription(sessionOldDtoV2.getSessionDescription());
        }
        if (null != sessionOldDtoV2.getAddress()) {
            sessionDtoV2.setAddress(sessionOldDtoV2.getAddress());
        }
        if (null != sessionOldDtoV2.getTopicId()) {
            sessionDtoV2.setTopicId(sessionOldDtoV2.getTopicId());
        }
        if (null != sessionOldDtoV2.getSessionStartDate()) {
            sessionDtoV2.setSessionStartDateTime(sessionOldDtoV2.getSessionStartDate());
        }
        if (null != sessionOldDtoV2.getSessionEndDate()) {
            sessionDtoV2.setSessionEndDateTime(sessionOldDtoV2.getSessionEndDate());
        }
        if (null != sessionOldDtoV2.getSessionCreator()) {
            sessionDtoV2.setSessionCreator(sessionOldDtoV2.getSessionCreator());
        }
        sessionDtoV2.setSessionProgress(sessionOldDtoV2.getSessionProgress());

        if (null != sessionOldDtoV2.getIsSessionCreator()) {
            sessionDtoV2.setIsSessionCreator(sessionOldDtoV2.getIsSessionCreator());
        }
        if (null != sessionOldDtoV2.getAttestationUrl()) {
            sessionDtoV2.setAttestationUrl(sessionOldDtoV2.getAttestationUrl());
        }
        if (null != sessionOldDtoV2.getAttestationDate()) {
            sessionDtoV2.setAttestationDate(sessionOldDtoV2.getAttestationDate());
        }
        if (null != sessionOldDtoV2.getMemberAttestationUrl()) {
            sessionDtoV2.setMemberAttestationUrl(sessionOldDtoV2.getMemberAttestationUrl());
        }
        if (null != sessionOldDtoV2.getTraineeAttestationUrl()) {
            sessionDtoV2.setTraineeAttestationUrl(sessionOldDtoV2.getTraineeAttestationUrl());
        }
        if (null != sessionOldDtoV2.getTopicInfo()) {
            sessionDtoV2.setTopicInfo(sessionOldDtoV2.getTopicInfo());
        }
        if (null != sessionOldDtoV2.getQrCodeUrl()) {
            sessionDtoV2.setQrCodeUrl(sessionOldDtoV2.getQrCodeUrl());
        }
        if (null != sessionOldDtoV2.getRole()) {
            sessionDtoV2.setRole(sessionOldDtoV2.getRole());
        }
        if (null != sessionOldDtoV2.getSessionTimeZone()) {
            sessionDtoV2.setSessionTimeZone(sessionOldDtoV2.getSessionTimeZone());
        }
        sessionDtoV2.setNumberOfParticipants(sessionOldDtoV2.getNumberOfParticipants());

        User user = new User();
        if (null != sessionOldDtoV2.getUser()) {
            if (null != sessionOldDtoV2.getUser().getUserId()) {
                user.setUserId(sessionOldDtoV2.getUser().getUserId());
            }
            if (null != sessionOldDtoV2.getUser().getEmailId()) {
                user.setEmailId(sessionOldDtoV2.getUser().getEmailId());
            }
            if (null != sessionOldDtoV2.getUser().getName()) {
                user.setName(sessionOldDtoV2.getUser().getName());
            }
            if (null != sessionOldDtoV2.getUser().getPhoto()) {
                user.setPhoto(sessionOldDtoV2.getUser().getPhoto());
            }
            if (null != sessionOldDtoV2.getUser().getEmailId()) {
                user.setEmailId(sessionOldDtoV2.getUser().getEmailId());
            }
            if (null != sessionOldDtoV2.getUser().getPhoneNo()) {
                user.setPhoneNo(sessionOldDtoV2.getUser().getPhoneNo());
            }
            sessionDtoV2.setUser(user);

        }

        List<MemberDtoV2> memberDtoV2List = new ArrayList<>();
        for (MemberOldDtoV2 memberOldDtoV2 : sessionOldDtoV2.getMembers()) {
            MemberDtoV2 memberDtoV2 = MemberDTOManipulation(memberOldDtoV2);
            memberDtoV2List.add(memberDtoV2);
        }

        sessionDtoV2.setMembers(memberDtoV2List);

        return sessionDtoV2;
    }


    private MemberDtoV2 MemberDTOManipulation(MemberOldDtoV2 membersOldDtoV2) {

        List<String> rolesList = new ArrayList<>();
        MemberRoleDto roles = membersOldDtoV2.getRoles();
        if (null != roles.getAdmin() && roles.getAdmin()) {
            rolesList.add("ADMIN");
        }
        if (null != roles.getTrainer() && roles.getTrainer()) {
            rolesList.add("TRAINER");
        }
        if (null != roles.getOther() && roles.getOther()) {
            rolesList.add("OTHER");
        }
        MemberDtoV2 memberDtoV2 = new MemberDtoV2();
        if (null != membersOldDtoV2.getName()) {
            memberDtoV2.setName(membersOldDtoV2.getName());
        }
        if (null != membersOldDtoV2.getPhoto()) {
            memberDtoV2.setPhoto(membersOldDtoV2.getPhoto());
        }
        if (null != membersOldDtoV2.getUserId()) {
            memberDtoV2.setUserId(membersOldDtoV2.getUserId());
        }
        if (null != membersOldDtoV2.getSessionId()) {
            memberDtoV2.setSessionId(membersOldDtoV2.getSessionId());
        }
        if (null != membersOldDtoV2.getTopicId()) {
            memberDtoV2.setTopicId(membersOldDtoV2.getTopicId());
        }
        if (null != membersOldDtoV2.getRoleDescription()) {
            memberDtoV2.setRoleDescription(membersOldDtoV2.getRoleDescription());
        }

        if (null != roles.getOtherRoleNames() && !roles.getOtherRoleNames().isEmpty()) {
            memberDtoV2.setOtherRoleDescription(roles.getOtherRoleNames());
        }
        memberDtoV2.setRole(rolesList);


        return memberDtoV2;
    }


    private boolean OtherRolesDescriptionNeeded(List<MemberDtoV2> members) {
        boolean flag = false;
        for (MemberDtoV2 member : members) {
            for (String roles : member.getRole()) {
                if (roles.equalsIgnoreCase("OTHER") && (member.getOtherRoleDescription() == null || member.getOtherRoleDescription().isEmpty())) {
                    flag = true;
                }
            }
        }
        return flag;
    }


    private boolean isDatesMissing(SessionDtoV2 sessionDTO) {

        String startDate = sessionDTO.getSessionStartDateTime();
        String endDate = sessionDTO.getSessionEndDateTime();
        return null == startDate || startDate.isEmpty() || null == endDate || endDate.isEmpty();
    }

    private Session convertDtoToEntity(SessionDtoV2 sessionDtoV2) {

        Session session = new Session();
        session.setTopicId(sessionDtoV2.getTopicId());
        session.setSessionStartDate(sessionDtoV2.getSessionStartDateTime());
        session.setSessionEndDate(sessionDtoV2.getSessionEndDateTime());
        session.setProgramName(sessionDtoV2.getProgramName());
        session.setSessionDescription(sessionDtoV2.getSessionDescription());
        session.setSessionCreator(sessionDtoV2.getSessionCreator());
        session.setTrainingOrganization(sessionDtoV2.getTrainingOrganization());
        session.setAddress(sessionDtoV2.getAddress());
        session.setSessionName(sessionDtoV2.getSessionName());

        return session;
    }


    private void addMembers(List<MemberDtoV2> members, Session session) {
        for (MemberDtoV2 member : members) {
            for (String memberRole : member.getRole()) {
                SessionRole sessionRole = new SessionRole();
                LOGGER.debug("members role is {}", member.getSessionId());
                sessionRole.setRole(memberRole);
                LOGGER.debug("session role of the employee is {}", sessionRole.getRole());
                sessionRole.setRoleDescription(member.getRoleDescription());
                sessionRole.setOtherRoleName(member.getOtherRoleDescription());
                sessionRole.setSession(session);
                sessionRole.setUserId(member.getUserId());
                sessionRole.setIs_deleted(false);
                sessionRoleRepository.save(sessionRole);
            }
        }
        LOGGER.info("Successfully added users to the session : {}", session.getId());
//        LocalDateTime dateTime = LocalDateTime.now();
//        for (MemberDtoV2 member : members) {
//            notificationService.saveNotification(member.getUserId(), session, new NotificationDTO(null, Constants.ADD_MEMBER, null, null, NotificationEvents.SESSION.toString(), dateTime.toLocalDate().toString() + " " + dateTime.toLocalTime().toString(), false));
//        }
    }

    private boolean ifAllMembersRolesAreValidforupdate(List<MemberDtoV2> members) {
        boolean flag = true;
        boolean trainerPresent = false;
        List<String> roleList = new ArrayList<>();
        for (MemberDtoV2 member : members) {
            for (String role : member.getRole()) {
                List<SessionRole> sessionRoles = sessionRoleRepository.findMembersRelatedToSession(Arrays.asList(member.getSessionId()));
                for (SessionRole sessionRole : sessionRoles) {
                    roleList.add(sessionRole.getRole());
                    roleList.add(role);
                }
            }
            Set<String> rolelistset = new HashSet<String>(roleList);
            for (String role1 : rolelistset) {
                if (role1.equals("TRAINER") || role1.equals("ADMIN") || role1.equals("OTHER")) {
                    if (role1.equals("TRAINER")) {
                        trainerPresent = true;
                    }
                    continue;
                } else
                    flag = false;
                break;
            }
        }
        return flag && trainerPresent;
    }

    private boolean ifAllMembersRolesAreValid(List<MemberDtoV2> members) {
        boolean flag = true;
        boolean trainerPresent = false;
        for (MemberDtoV2 member : members) {
            for (String role : member.getRole()) {
                if (role.equals("TRAINER") || role.equals("ADMIN") || role.equals("OTHER")) {
                    if (role.equals("TRAINER")) {
                        trainerPresent = true;
                    }
                    continue;
                } else
                    flag = false;
                break;
            }
        }
        return flag && trainerPresent;
    }

    private boolean membersAreNotEligible(List<MemberOldDtoV2> members, Long topicId, String loggedInUserId) {
        for (MemberOldDtoV2 member : members) {
            ResponseDTO responseDTO = userService.getUserDetailsForActiveUser(member.getUserId(), topicId, loggedInUserId, false);
            ScanMemberDetailsDto scanMemberDetailsDto = (ScanMemberDetailsDto) responseDTO.getResponse();
            if (!scanMemberDetailsDto.isEligibleAsTrainer()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ResponseDTO updateSession(SessionOldDtoV2 sessionOldDTO, BindingResult bindingResult, String
            loggedInUserId) {
        LOGGER.info("==========update session api call ===========");
        userService.valiadtePojo(bindingResult);
        SessionDtoV2 sessionDTO = sessionDTOManipulation(sessionOldDTO);
        LOGGER.info("Updating Session details for {} ", sessionDTO.getSessionId());


        Session session = null;

        try {
            session = sessionRepository.findById(sessionDTO.getSessionId()).get();
        } catch (Exception e) {
            LOGGER.error("Error finding the session : {}. Root Cause : {} ", sessionDTO.getSessionId(), e.getMessage());
            return HttpUtils.onFailure(500, "Error finding the session: " + e.getMessage());
        }
        String[] startDatetime = sessionDTO.getSessionStartDateTime().split(" ");
        String[] endDatetime = sessionDTO.getSessionEndDateTime().split(" ");
        if (LocalDate.parse(startDatetime[0]).isAfter(LocalDate.parse(endDatetime[0]))) {
            return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Session Start Date must  be after Session EndDate");
        }
        if (LocalDate.parse(startDatetime[0]).equals(LocalDate.parse(endDatetime[0])) && LocalTime.parse(startDatetime[1]).isAfter(LocalTime.parse(endDatetime[1]))) {
            return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Session Start Time must  be after Session EndTime");
        }

        if (!ifAllMembersRolesAreValid(sessionDTO.getMembers())) {
            return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), Constants.USER_ROLES_NOT_VALID);
        }
        if (membersAreNotEligible(sessionOldDTO.getMembers(), sessionOldDTO.getTopicId(), loggedInUserId)) {
            return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), Constants.MEMBERS_NOT_ELIGIBLE);
        }

        if (OtherRolesDescriptionNeeded(sessionDTO.getMembers())) {
            return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "provide OtherRoleDescription for role:OTHER");
        }

        try {

            updateEntityObjectWithUpdatedDTO(session, sessionDTO);

            //Retro Call to Entity service
            TopicInfo details = new TopicInfo();
            Call<TopicInfo> userRequest = entityDao.topicDetailWithProgramContentDTO(session.getTopicId(), true);

            retrofit2.Response userResponse = userRequest.execute();
            if (!userResponse.isSuccessful()) {
                LOGGER.error("unable to fetch Content And Program details {}", userResponse.errorBody().string());
                return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "Content And Program details Not Available");
            } else {
                details = (TopicInfo) userResponse.body();
            }
            session.setProgramId(details.getTopic().getProgramId());

            //Deleting previous existing members  in the session
            sessionRoleRepository.deleteSessionById(session);
            addMembers(sessionDTO.getMembers(), session);
            session.setSessionTimeZone(sessionDTO.getSessionTimeZone());

            session.setStartQrcode(generateQrCodeforsession(session.getId(), session.getSessionName()
                    , session.getSessionStartDate(), Constants.START_QR, "startDate", sessionDTO.getSessionTimeZone()).getResponse().toString());
            session.setEndQrcode(generateQrCodeforsession(session.getId(), session.getSessionName()
                    , session.getSessionEndDate(), Constants.END_QR, "endDate", sessionDTO.getSessionTimeZone()).getResponse().toString());


            String utcTime = TimeUtils.convertToUTCTImeZone(session.getSessionEndDate(), appContext.getSessionEndMinutes());
            if (!DateUtil.validateDateTimeFormatMS(utcTime)) {
                return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "Error while Converting Date and Time");
            }
            session.setSessionEndDateUtcTime(utcTime);

            sessionRepository.save(session);
            LOGGER.info("Session updated successfully with name : {} and Id : {} ", session.getSessionName(), session.getId());
        } catch (Exception e) {
            LOGGER.error("Error in updating session with name : {} and id : {}", session.getSessionName(), session.getId());
            return HttpUtils.onFailure(500, "Error in updating Session: " + e.getMessage());
        }
        return HttpUtils.success(new SessionResponseDTO(sessionDTO.getSessionId()), "Successfully updated Session");

    }

    private void updateEntityObjectWithUpdatedDTO(Session session, SessionDtoV2 sessionDTO) {

        List<MemberDtoV2> memberDtoV2List = sessionDTO.getMembers();

        LocalDateTime dateTime = LocalDateTime.now();
        if (!StringUtils.isEmpty(sessionDTO.getSessionDescription()) && !sessionDTO.getSessionDescription().equals(session.getSessionDescription())) {
            session.setSessionDescription(sessionDTO.getSessionDescription());

        }
        if (!StringUtils.isEmpty(sessionDTO.getAddress()) && !sessionDTO.getAddress().equals(session.getAddress())) {
            session.setAddress(sessionDTO.getAddress());
            notificationService.saveNotification(session.getSessionCreator(), session, new NotificationDTO(null, Constants.EDIT_SESSION_ADDRESS_CHANGE, null, null, NotificationEvents.SESSION.toString(), dateTime.toLocalDate().toString() + " " + dateTime.toLocalTime().toString(), false, null));
            for (MemberDtoV2 member : memberDtoV2List) {
                notificationService.saveNotification(member.getUserId(), session, new NotificationDTO(null, Constants.EDIT_SESSION_ADDRESS_CHANGE, null, null, NotificationEvents.SESSION.toString(), dateTime.toLocalDate().toString() + " " + dateTime.toLocalTime().toString(), false, null));
            }
            LOGGER.debug("EDIT SESSION LOCATION {}", sessionDTO.getAddress());
        }
        if (!StringUtils.isEmpty(sessionDTO.getSessionStartDateTime()) && !sessionDTO.getSessionStartDateTime().equals(session.getSessionStartDate())) {
            session.setSessionStartDate(sessionDTO.getSessionStartDateTime());
            notificationService.saveNotification(session.getSessionCreator(), session, new NotificationDTO(null, Constants.EDIT_SESSION_DATE, null, null, NotificationEvents.SESSION.toString(), dateTime.toLocalDate().toString() + " " + dateTime.toLocalTime().toString(), false, null));
            for (MemberDtoV2 member : memberDtoV2List) {
                notificationService.saveNotification(member.getUserId(), session, new NotificationDTO(null, Constants.EDIT_SESSION_DATE, null, null, NotificationEvents.SESSION.toString(), dateTime.toLocalDate().toString() + " " + dateTime.toLocalTime().toString(), false, null));
            }
            LOGGER.debug("EDIT SESSION START DATE {}", session.getSessionStartDate());
        }
        if (!StringUtils.isEmpty(sessionDTO.getSessionEndDateTime()) && !sessionDTO.getSessionEndDateTime().equals(session.getSessionEndDate())) {
            session.setSessionEndDate(sessionDTO.getSessionEndDateTime());
            notificationService.saveNotification(session.getSessionCreator(), session, new NotificationDTO(null, Constants.EDIT_SESSION_END_DATE, null, null, NotificationEvents.SESSION.toString(), dateTime.toLocalDate().toString() + " " + dateTime.toLocalTime().toString(), false, null));
            for (MemberDtoV2 member : memberDtoV2List) {
                notificationService.saveNotification(member.getUserId(), session, new NotificationDTO(null, Constants.EDIT_SESSION_END_DATE, null, null, NotificationEvents.SESSION.toString(), dateTime.toLocalDate().toString() + " " + dateTime.toLocalTime().toString(), false, null));
            }
            LOGGER.debug("EDIT SESSION END DATE {}", sessionDTO.getSessionEndDateTime());
        }
        session.setSessionName(sessionDTO.getSessionName());


        session.setTopicId(sessionDTO.getTopicId());
    }

    @Override
    public ResponseDTO addSessionUser(MemberOldDtoV2 memberOldDtoV2, String callingUserId) throws IOException {
        MemberDtoV2 member = MemberDTOManipulation(memberOldDtoV2);
        LOGGER.debug("Adding user : {} to the session : {}", member.getUserId(), member.getSessionId());
        Session session = new Session();
        LOGGER.debug("user and topic id are {}, {}", member.getUserId(), member.getTopicId());

        try {
            session = sessionRepository.findById(member.getSessionId()).get();
            if (!session.getSessionCreator().equalsIgnoreCase(callingUserId)) {
                return HttpUtils.onFailure(HttpStatus.UNAUTHORIZED.value(), "This user does not have permission to add members");
            }
        } catch (Exception e) {
            LOGGER.error("Unable to find the session : {}. Reason: {}", member.getSessionId(), e.getMessage());
            return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "Unable to find the session. Reason: " + e.getMessage());
        }
        List<MemberDtoV2> members = new ArrayList<>();
        members.add(member);
        if (OtherRolesDescriptionNeeded(members)) {
            return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "provide OtherRoleDescription for role:OTHER");
        }

        LOGGER.debug("Checking if User  is already added to the session");

        if (Boolean.TRUE.equals(sessionRoleRepository.isUserAlreadyAddedtoSession(member.getUserId(), member.getSessionId()))) {
            LOGGER.error(Constants.USER_ALREADY_ADDED_MESSAGE + member.getUserId());
            return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), Constants.USER_ALREADY_ADDED_MESSAGE);
        } else {
            SessionRole sessionRole = new SessionRole();
            sessionRole.setRoleDescription(member.getRoleDescription());
            sessionRole.setOtherRoleName(member.getOtherRoleDescription());
            sessionRole.setSession(session);
            sessionRole.setIs_deleted(false);
            sessionRole.setUserId(member.getUserId());
            for (String roles : member.getRole()) {
                if (roles.equals("OTHER") && (member.getOtherRoleDescription() == null || member.getOtherRoleDescription().isEmpty())) {
                    return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "For Role:OTHER otherRole Must be provided!");
                }
                sessionRole.setRole(roles);
                sessionRoleRepository.save(sessionRole);
            }
            LOGGER.info("Successfully added user : {} to the session : {}", member.getUserId(), member.getSessionId());

            LocalDateTime dateTime = LocalDateTime.now();
            notificationService.saveNotification(member.getUserId(), session, new NotificationDTO(null, Constants.ADD_MEMBER, null, null, NotificationEvents.SESSION.toString(), dateTime.toLocalDate().toString() + " " + dateTime.toLocalTime().toString(), false, null));
            return HttpUtils.success("SUCCESS", "Successfully added user to the session ");
        }


    }


    @Override
    public ResponseDTO updateSessionUser(MemberOldDtoV2 memberOldDtoV2, String callingUserId) {
        MemberDtoV2 member = MemberDTOManipulation(memberOldDtoV2);
        if (Boolean.FALSE.equals(sessionRoleRepository.isUserAlreadyAddedtoSession(member.getUserId(), member.getSessionId()))) {
            return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "This user is not part of this Session");
        } else {
            ResponseDTO userDetails = userService.getUserDetailsForActiveUser(member.getUserId(), member.getTopicId(), callingUserId, false);
            if (userDetails.getResponseCode() != HttpStatus.OK.value()) {
                return userDetails;
            }
            LOGGER.debug("Add the new set of roles");
            return addSessionUserOnUpdate(member, callingUserId);
        }
    }

    private ResponseDTO addSessionUserOnUpdate(MemberDtoV2 member, String callingUserId) {
        LOGGER.debug("Adding user : {} to the session : {}", member.getUserId(), member.getSessionId());
        try {
            Session session = sessionRepository.findById(member.getSessionId()).get();
            if (!session.getSessionCreator().equalsIgnoreCase(callingUserId)) {
                return HttpUtils.onFailure(HttpStatus.UNAUTHORIZED.value(), "This user does not have permission to add members");
            }
        } catch (Exception e) {
            LOGGER.error("Unable to find the session : {}. Reason: {}", member.getSessionId(), e.getMessage());
            return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "Unable to find the session. Reason: " + e.getMessage());
        }

        List<MemberDtoV2> members = new ArrayList<>();
        members.add(member);
        if (!ifAllMembersRolesAreValidforupdate(members)) {
            return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), Constants.USER_ROLES_NOT_VALID);
        }
        List<String> roleList = sessionRoleRepository.collectRolesForUserforSession(member.getUserId(), member.getSessionId());
        boolean currentUserFlag = false;
        for (String role : roleList) {
            if (role.equalsIgnoreCase("TRAINER")) {
                currentUserFlag = true;
            }
        }
        if (sessionRoleRepository.getSessionTrainersBySessionId(member.getSessionId()).size() == 1 && currentUserFlag) {
            boolean flag = false;
            for (String roles : member.getRole()) {
                if (roles.equalsIgnoreCase("TRAINER")) {
                    flag = true;
                }
            }
            if (Boolean.FALSE.equals(flag)) {
                return HttpUtils.onFailure(HttpStatus.NOT_MODIFIED.value(), "Unable to Update as no trainer present in updated member ");
            }
        }

        Session session = sessionRepository.findByIds(member.getSessionId());
        sessionRoleRepository.deleteUserByUserIDAndSessionId(member.getUserId(), session);
        List<SessionRole> sessionRoles = new ArrayList<>();

        for (String roles : member.getRole()) {
            sessionRoles.add(new SessionRole(roles, member.getUserId(), session, member.getRoleDescription(), member.getOtherRoleDescription(), false));
        }
        sessionRoleRepository.saveAll(sessionRoles);
        LOGGER.info("Successfully added user : {} to the session : {}", member.getUserId(), member.getSessionId());
        LocalDateTime dateTime = LocalDateTime.now();
        notificationService.saveNotification(member.getUserId(), session, new NotificationDTO(null, Constants.ADD_MEMBER, null, null, NotificationEvents.SESSION.toString(), dateTime.toLocalDate().toString() + " " + dateTime.toLocalTime().toString(), false, null));
        return HttpUtils.success("SUCCESS", "Successfully added user to the session ");
    }


    @Override
    public ResponseDTO deleteSessionUser(String userId, Long sessionId, String callingUserId) {
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(sessionId)) {
            return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "UserId and/or SessionId is null");
        }
        Session session = sessionRepository.findByIds(sessionId);
        if (session == null) {
            return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "No session Found ");
        }
        List<SessionRole> sessionRoles = sessionRoleRepository.findBySessionAndUserId(userId, session);
        if (!callingUserId.equals(session.getSessionCreator())) {
            return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "You are not allowed to delete users");
        }
        if (sessionRoles.size() == 0) {
            return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "No session Found for the userId");
        }
        if (sessionRoleRepository.getSessionTrainersBySessionId(sessionId).size() == 1 && sessionRoleRepository.getRoleByUserIdAndSessionIdForTrainer(userId, session)) {
            return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), " Atleast one trainer is required for a session");
        }
        for (SessionRole sessionRole : sessionRoles) {
            sessionRoleRepository.deleteByUserId(sessionRole.getUserId(), session);
        }
        LocalDateTime dateTime = LocalDateTime.now();
        notificationService.saveNotification(userId, session, new NotificationDTO(null, Constants.DELETE_MEMBER, null, null, NotificationEvents.SESSION.toString(), dateTime.toLocalDate().toString() + " " + dateTime.toLocalTime().toString(), false, null));
        return HttpUtils.onSuccess(null, "Successfully deleted  user from session ");
    }


    @Override
    public ResponseDTO deleteSessionMultipleUser(List<String> userIds, Long sessionId) {
        for (String usercheck : userIds) {
            try {
                if (StringUtils.isEmpty(usercheck) || StringUtils.isEmpty(sessionId)) {
                    throw new NullPointerException("UserId and/or SessionId is null");
                }
            } catch (Exception e) {
                LOGGER.error("Error while trying to delete users: {} and session: {} because of execption : {}", usercheck, sessionId, e.getMessage());
                return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Error while trying to delete the relationship between user:" + usercheck + " and session:" + sessionId + " because of exception:" + e.getMessage());
            }
            if (sessionRoleRepository.findBySessionAndUser(usercheck, sessionId) == 0) {
                return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "No user Id is found with id " + usercheck + " in session with sessionId " + sessionId);
            }
        }
        for (String user : userIds) {
            LOGGER.info("Successfully deleted the user : {} and session : {} ", user, sessionId);
            sessionRoleRepository.memberSoftDelete(user, sessionId);
            LocalDateTime dateTime = LocalDateTime.now();
            Session session = sessionRepository.findByIds(sessionId);
            List<String> userIdList = Arrays.stream(user.split(", ")).collect(Collectors.toList());
            for (String userId : userIdList) {
                notificationService.saveNotification(userId, session, new NotificationDTO(null, Constants.DELETE_MEMBER, null, null, NotificationEvents.SESSION.toString(), dateTime.toLocalDate().toString() + " " + dateTime.toLocalTime().toString(), false, null));
            }


        }
        return HttpUtils.onSuccess(null, "Successfully deleted users in session ");
    }

    @Override
    public ResponseDTO deleteSession(Long sessionId) {
        Session session = sessionRepository.findByIds(sessionId);
        List<SessionRole> sessionmemberroles = sessionRoleRepository.getrolesForSession(sessionId);
        LocalDateTime dateTime = LocalDateTime.now();
        notificationService.saveNotification(session.getSessionCreator(), session, new NotificationDTO(null, Constants.DELETE_SESSION, sessionId, null, NotificationEvents.SESSION.toString(), dateTime.toLocalDate().toString() + " " + dateTime.toLocalTime().toString(), false, null));
        for (SessionRole sessionRole1 : sessionmemberroles) {
            notificationService.saveNotification(sessionRole1.getUserId(), session, new NotificationDTO(null, Constants.DELETE_SESSION, sessionId, null, NotificationEvents.SESSION.toString(), dateTime.toLocalDate().toString() + " " + dateTime.toLocalTime().toString(), false, null));
        }
        try {
            sessionRepository.softDelete(sessionId);
            int noOfSessions = sessionRepository.findDistinctSessionsForTopic(session.getTopicId());
            if (noOfSessions == 0) {
                TopicSessionLinkedDTO topicSessionLinkedDTO = new TopicSessionLinkedDTO(session.getTopicId(), false);
                Response entityResponse = entityDao.updateSessionLinkStatus(topicSessionLinkedDTO).execute();
                if (!entityResponse.isSuccessful()) {
                    return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Error while updating the SessionLink field");
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error while trying to delete the session: {} because of execption : {}", sessionId, e.getMessage());
            return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Error while trying to delete the session:" + sessionId + " because of exception:" + e.getMessage());

        }
        LOGGER.info("Successfully deleted the  Session : {} ", sessionId);

        return HttpUtils.success(null, "Successfully deleted the Session: " + sessionId);
    }

    @Override
    public ResponseDTO getAllSessionsForUser(String userId) {

        List<BigInteger> sessionsPartOf = sessionRoleRepository.getMySessions(userId);
        List<BigInteger> sessionByMe = sessionRepository.getMySessions(userId);

        Set<BigInteger> distinctSessions = new LinkedHashSet<>();
        distinctSessions.addAll(sessionByMe);
        distinctSessions.addAll(sessionsPartOf);

        List<SessionCardDTOV2> sessionCards = new ArrayList<>();
        ResponseDTO responseDTO = new ResponseDTO();
        for (BigInteger sessionsbyme : distinctSessions) {
            List<Session> session = sessionRepository.findBysessionId(sessionsbyme);
            for (Session sessions : session) {
                try {
//                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                    LocalDateTime now = LocalDateTime.now();
//                    String todayDate = dtf.format(now);
//                    String[] nowdate = todayDate.split(" ");
//                    String[] endDatetime = sessions.getSessionEndDate().split(" ");
//                    if (!LocalDate.parse(endDatetime[0]).isBefore(LocalDate.parse(nowdate[0]))) {
//                        SessionCardDTOV2 sessionCardDTO = generateSessionCardDTO(sessions, responseDTO);
//                        sessionCards.add(sessionCardDTO);
//                    }

                    if (DateUtil.isCurrentDateAndTimeBeforeADate(sessions.getSessionEndDateUtcTime())) {
                        SessionCardDTOV2 sessionCardDTO = generateSessionCardDTO(sessions, responseDTO);
                        sessionCards.add(sessionCardDTO);
                    }
                } catch (Exception p) {
                    LOGGER.error("Exception : {}", p.getMessage());
                }
            }
        }
        responseDTO.setResponseCode(HttpStatus.OK.value());
        responseDTO.setResponse(sessionCards);
        responseDTO.setMessage("Successfully fetch all session Details ");
        return responseDTO;
    }

    private SessionCardDTOV2 generateSessionCardDTO(Session session, ResponseDTO responseDTO) {
        SessionCardDTOV2 sessionCardDTO = modelMapper.map(session, SessionCardDTOV2.class);
        if (sessionCardDTO.getSessionId() == null) {
            sessionCardDTO.setSessionId(session.getId());
        }
        try {
            if (!StringUtils.isEmpty(session.getSessionEndDate()) && !DateUtil.validateDateTimeFormatMS(session.getSessionEndDate())) {
                responseDTO.setResponseCode(HttpStatus.BAD_REQUEST.value());
                responseDTO.setMessage("Invalid session end date");
                return sessionCardDTO;
            }
            if (!StringUtils.isEmpty(session.getSessionStartDate()) && !DateUtil.validateDateTimeFormatMS(session.getSessionStartDate())) {
                responseDTO.setResponseCode(HttpStatus.BAD_REQUEST.value());
                responseDTO.setMessage("Invalid session start date");
                return sessionCardDTO;
            }
            boolean isLive = DateUtil.isCurrentDateAndTimeAfterADate(session.getSessionStartDate());
            sessionCardDTO.setSessionProgress(isLive ? Constants.SESSION_STATUS_LIVE : Constants.SESSION_STATUS_UPCOMING);

        } catch (Exception p) {
            LOGGER.error("Exception : {}", p.getMessage());
        }

        responseDTO.setResponseCode(HttpStatus.OK.value());
        return sessionCardDTO;
    }

    @Override
    public ResponseDTO getSessionUserDetailsBySessionId(Long sessionId, String loggedInUserId) {
        try {
            List<String> userIds = sessionRoleRepository.findUserIdsForASession(sessionId);
            List<MemberOldDtoV2> membersOldList = new ArrayList<>();
            for (String userId : userIds) {
                List<String> roles = sessionRoleRepository.collectRolesForUserforSession(userId, sessionId);
                Session session = sessionRepository.findByIds(sessionId);
                MemberDtoV2 memberDtoV2 = new MemberDtoV2();
                memberDtoV2.setRole(roles);
                memberDtoV2.setUserId(userId);
                memberDtoV2.setSessionId(sessionId);
                memberDtoV2.setTopicId(session.getTopicId());
                List<SessionRole> sessionRoles = sessionRoleRepository.findBySessionAndUserId(userId, session);
                for (SessionRole sessionRole : sessionRoles) {
                    if (sessionRole.getRoleDescription() != null && !sessionRole.getRoleDescription().isEmpty()) {
                        memberDtoV2.setRoleDescription(sessionRole.getRoleDescription());
                    }
                    if (sessionRole.getOtherRoleName() != null && !sessionRole.getOtherRoleName().isEmpty()) {
                        memberDtoV2.setOtherRoleDescription(sessionRole.getOtherRoleName());
                    }
                    ResponseDTO responseDTO = userService.getUserDetails(userId, loggedInUserId, false);
                    User user = (User) responseDTO.getResponse();
                    memberDtoV2.setName(user.getName());
                    memberDtoV2.setPhoto(user.getPhoto());
                }
                membersOldList.add(responseMemberDTO(memberDtoV2));
            }

            if (userIds.size() == 0) {
                return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "No user for particular Session Id");
            } else {

                LOGGER.info("Successfully fetch userDetail and type of rel using SessionId : {} ", sessionId);
                return HttpUtils.success(membersOldList, "Successfully fetch userDetail and type of rel using SessionId: " + sessionId);
            }

        } catch (Exception exception) {
            LOGGER.error("Error while trying to fetch user detail ,type of rel.: {} because of exception : {}", sessionId, exception.getMessage());
            return HttpUtils.onFailure(HttpStatus.BAD_REQUEST.value(), "Error while trying to fetch user detail ,type of rel.: {} because of exception :" + sessionId + " because of exception:" + exception.getMessage());
        }

    }


    private MemberOldDtoV2 responseMemberDTO(MemberDtoV2 memberDtoV2) {
        MemberOldDtoV2 memberOldDtoV2 = new MemberOldDtoV2();
        memberOldDtoV2.setUserId(memberDtoV2.getUserId());
        memberOldDtoV2.setSessionId(memberDtoV2.getSessionId());
        memberOldDtoV2.setTopicId(memberDtoV2.getTopicId());
        memberOldDtoV2.setRoleDescription(memberDtoV2.getRoleDescription());
        memberOldDtoV2.setPhoto(memberDtoV2.getPhoto());
        memberOldDtoV2.setUserId(memberDtoV2.getUserId());
        memberOldDtoV2.setName(memberDtoV2.getName());

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


    @Override
    public ResponseDTO getCompleteSessionInfo(Long sessionId, String accessToken, String loggedInUserId) throws
            IOException, ParseException {
        LOGGER.info("=====api call get complete session info======= redd");
        Session sessions = sessionRepository.findByIds(sessionId);
        if (sessions == null) {
            return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "No session Found ");
        }
        SessionOldDtoV2 sessionOldDtoV2;
        Session session = sessionRepository.findByIds(sessionId);
        sessionOldDtoV2 = convertSessionEntityToDto(session);
        sessionOldDtoV2.setIsSessionCreator(session.getSessionCreator().equalsIgnoreCase(loggedInUserId));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date sessionStartDate = format.parse(session.getSessionStartDate());

        boolean isLive = DateUtil.isCurrentDateTimeAfterADate(sessionStartDate.getTime(), "0");
        sessionOldDtoV2.setSessionProgress(isLive ? Constants.SESSION_STATUS_LIVE : Constants.SESSION_STATUS_UPCOMING);
        List<String> userIds = sessionRoleRepository.findDistinctMemberIdsForASession(sessionId);
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

        List<MemberOldDtoV2> membersOldList = new ArrayList<>();
        for (String userId : userIds) {
            List<String> roles = sessionRoleRepository.collectRolesForUserforSession(userId, sessionId);
            MemberDtoV2 memberDtoV2 = new MemberDtoV2();
            memberDtoV2.setRole(roles);
            memberDtoV2.setUserId(userId);
            memberDtoV2.setSessionId(sessionId);
            memberDtoV2.setTopicId(session.getTopicId());
            ResponseDTO responseDTO = userService.getUserDetails(userId, loggedInUserId, false);
            User user = (User) responseDTO.getResponse();
            memberDtoV2.setName(user.getName());
            memberDtoV2.setPhoto(user.getPhoto());
            List<SessionRole> sessionRoleList = sessionRoleRepository.findBySessionAndUserId(userId, session);
            for (SessionRole otherRole : sessionRoleList) {
                if (otherRole.getRole().equalsIgnoreCase("OTHER")) {
                    memberDtoV2.setOtherRoleDescription(otherRole.getOtherRoleName());
                }
                memberDtoV2.setRoleDescription(otherRole.getRoleDescription());
            }
            membersOldList.add(responseMemberDTO(memberDtoV2));
        }
        ResponseDTO responseDTO = userService.getUserDetails(loggedInUserId, loggedInUserId, false);
        User user = (User) responseDTO.getResponse();
        if (null != user) {
            sessionOldDtoV2.setUser(user);
        }
        sessionOldDtoV2.setMembers(membersOldList);
        sessionOldDtoV2.setMember(sessionRoleRepository.getMembersForSession(loggedInUserId, sessionId));
        ResponseDTO sessionCreator = userService.getUserDetails(session.getSessionCreator(), loggedInUserId, false);
        User sessionCreatorResponse = (User) sessionCreator.getResponse();
        if (null != sessionCreatorResponse) {
            sessionOldDtoV2.setSessionCreatorProfile(sessionCreatorResponse);
        }
        List<SessionLinks> sessionLinks = sessionLinksRepository.findSessionLinksBySessionid(sessionId);
        List<SessionLinksDTO> sessionLinksDTO = new ArrayList<>();
        for (SessionLinks sessionLink : sessionLinks) {
            sessionLinksDTO.add(new SessionLinksDTO(sessionLink.getId(), sessionLink.getSession().getId(), sessionLink.getSessionUrl()));

        }
        sessionOldDtoV2.setSessionLinks(sessionLinksDTO);
        sessionOldDtoV2.setStartQrcode(session.getStartQrcode());
        sessionOldDtoV2.setEndQrcode(session.getEndQrcode());

        return HttpUtils.success(sessionOldDtoV2, Constants.SUCESSFULLY_FETCHED_SESSIONS);
    }

    private SessionOldDtoV2 convertSessionEntityToDto(Session session) {
        SessionOldDtoV2 sessionDtoV2 = new SessionOldDtoV2();
        sessionDtoV2.setSessionId(session.getId());
        sessionDtoV2.setSessionName(session.getSessionName());
        sessionDtoV2.setSessionDescription(session.getSessionDescription());
        sessionDtoV2.setAddress(session.getAddress());
        sessionDtoV2.setTopicId(session.getTopicId());

        sessionDtoV2.setSessionEndDate(session.getSessionEndDate());
        sessionDtoV2.setSessionStartDate(session.getSessionStartDate());
        return sessionDtoV2;
    }


    @Override
    public ResponseDTO getCompleteSessionInfoForAttestation(Long sessionId, String accessToken, String role) throws
            IOException, org.keycloak.common.VerificationException {
        Session session = sessionRepository.findById(sessionId).get();
        String loogedInUserID = KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm(),appContext.getKeycloakPublickey());
        Attendance attendance = attendanceRepository.findByUserIdAndSessionId(loogedInUserID, sessionId, role);
        SessionOldDtoV2 sessionOldDtoV2 = attesatationService.getCompleteSessionInfoForAttestation(session, loogedInUserID, attendance);
        return HttpUtils.success(sessionOldDtoV2, Constants.SUCESSFULLY_FETCHED_SESSIONS);
    }

    @Override
    public ResponseDTO getCompleteSessionInfoForAttestationOptimized(Long sessionId, String accessToken, String
            role) throws
            IOException, org.keycloak.common.VerificationException {
        Session session = sessionRepository.findById(sessionId).get();
        String loogedInUserID = KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm(),appContext.getKeycloakPublickey());
        Attendance attendance = attendanceRepository.findByUserIdAndSessionId(loogedInUserID, sessionId, role);
        List<BigInteger> topicIds = new ArrayList<>();
        topicIds.add(BigInteger.valueOf(session.getTopicId()));
        List<TopicInfo> topicIdsData = null;
        Call<List<TopicInfo>> userRequest = entityDao.multipleTopicDetailWithProgramContentDTO(topicIds);
        retrofit2.Response userResponse = userRequest.execute();
        if (!userResponse.isSuccessful()) {
            LOGGER.error("unable to fetch Content And Program details {}", userResponse.errorBody().string());
        } else {
            topicIdsData = (List<TopicInfo>) userResponse.body();
        }
        List<String> allUserIdsRelatedToSession = sessionRoleRepository.findUserIdOfMembersRelatedToSession(Arrays.asList(sessionId));
        ResponseDTO responseDTO = userService.getAllUserDetails(allUserIdsRelatedToSession, loogedInUserID, false);
        if (responseDTO.getResponseCode() != HttpStatus.OK.value()) {
            return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "Error while fetching user Details");
        }

        SessionOldDtoV2 sessionOldDtoV2 = attesatationService.getCompleteSessionInfoForAttestationOptimized(session, loogedInUserID, attendance, topicIdsData, (List<RegistryUserWithOsId>) responseDTO.getResponse());
        return HttpUtils.success(sessionOldDtoV2, Constants.SUCESSFULLY_FETCHED_SESSIONS);
    }

    @Override
    public ResponseDTO getCompleteAttestationInfoForWeb(String userId, Long sessionId, String role, String
            ipAddress) throws ParseException, IOException {
        Session session = sessionRepository.findById(sessionId).get();
        SessionOldDtoV2 sessionDtoV2 = new SessionOldDtoV2();
        sessionDtoV2.setIsSessionCreator(session.getSessionCreator().equalsIgnoreCase(userId));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date sessionStartDate = format.parse(session.getSessionStartDate());
        boolean isLive = DateUtil.isCurrentDateTimeAfterADate(sessionStartDate.getTime(), "0");
        sessionDtoV2.setSessionProgress(isLive ? Constants.SESSION_STATUS_LIVE : Constants.SESSION_STATUS_UPCOMING);
        Attendance attendance = attendanceRepository.findByUserIdAndSessionIdAndRole(userId, sessionId, role);

        if (attendance != null) {
            sessionDtoV2.setAttestationUrl(attendance.getAttestationUrl());
        }
        ResponseDTO responseDTO = userService.getUserDetails(userId, null, false);
        User user = (User) responseDTO.getResponse();
        sessionDtoV2.setUser(user);
        LOGGER.info("user fetched Name:" + user.getName() + "userId: " + user.getUserId());
        sessionDtoV2.setSessionId(session.getId());
        sessionDtoV2.setSessionName(session.getSessionName());
        sessionDtoV2.setSessionDescription(session.getSessionDescription());
        sessionDtoV2.setAddress(session.getAddress());
        sessionDtoV2.setSessionStartDate(session.getSessionStartDate());
        sessionDtoV2.setSessionEndDate(session.getSessionEndDate());
        sessionDtoV2.setSessionCreator(session.getSessionCreator());
        sessionDtoV2.setTopicId(session.getId());

        TopicInfo details;
        Call<TopicInfo> userRequest = entityDao.topicDetailWithProgramContentDTO(session.getTopicId(), true);
        retrofit2.Response userResponse = userRequest.execute();
        if (!userResponse.isSuccessful()) {
            LOGGER.error("unable to fetch Content And Program details {}", userResponse.errorBody().string());
            return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "Content And Program details Not Available");
        } else {
            details = (TopicInfo) userResponse.body();
        }
        sessionDtoV2.setTopicInfo(details);

        try {
            List<SessionRole> sessionRoles = sessionRoleRepository.findMembersRelatedToSession(Arrays.asList(session.getId()));
            List<MemberOldDtoV2> membersOldList = new ArrayList<>();
            List<MemberDtoV2> memberDtos = new ArrayList<>();
            for (SessionRole sessionRole : sessionRoles) {
                MemberOldDtoV2 sameMember = memberExistInList(sessionRole.getUserId(), membersOldList);
                if (sameMember != null) {
                    String userRole = sessionRole.getRole();
                    MemberRoleDto memberRoleDto = sameMember.getRoles();
                    if (null != userRole && !userRole.isEmpty() && userRole.equalsIgnoreCase("TRAINER")) {
                        memberRoleDto.setTrainer(true);
                    }
                    if (null != userRole && !userRole.isEmpty() && userRole.equalsIgnoreCase("ADMIN")) {
                        memberRoleDto.setAdmin(true);
                    }
                    if (null != userRole && !userRole.isEmpty() && userRole.equalsIgnoreCase("OTHER")) {
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
                ResponseDTO responseData = userService.getUserDetails(sessionRole.getUserId(), null, false);
                User userData = (User) responseData.getResponse();
                memberDtoV2.setName(userData.getName());
                memberDtoV2.setPhoto(userData.getPhoto());
                if (null != sessionRole.getRoleDescription() && !sessionRole.getRoleDescription().isEmpty()) {
                    memberDtoV2.setRoleDescription(sessionRole.getRoleDescription());
                }
                if (null != sessionRole.getOtherRoleName() && !sessionRole.getOtherRoleName().isEmpty()) {
                    memberDtoV2.setOtherRoleDescription(sessionRole.getOtherRoleName());
                }
                memberDtos.add(memberDtoV2);
                membersOldList.add(responseMemberDTO(memberDtoV2));
            }
            sessionDtoV2.setMembers(membersOldList);
        } catch (Exception exception) {
            LOGGER.error("Error while trying to fetch user detail ,type of rel.: {} because of exception : {}", session.getId(), exception.getMessage());
        }

        sessionDtoV2.setRole(attendance.getRole());

        if (role != null && !Constants.TRAINEE.equalsIgnoreCase(role)) {
            int num = attendanceRepository.numberOfParticipantsAttendedSession(session.getId());
            sessionDtoV2.setNumberOfParticipants(num);
        }
        sessionDtoV2.setAttestationDate(attendance.getScanOutDateTime());
        return HttpUtils.success(sessionDtoV2, Constants.SUCESSFULLY_FETCHED_SESSIONS);
    }

    private MemberOldDtoV2 memberExistInList(String userId, List<MemberOldDtoV2> dtoV2s) {
        if (null != dtoV2s && !dtoV2s.isEmpty()) {
            Optional<MemberOldDtoV2> member = dtoV2s.stream().filter(m -> userId.equals(m.getUserId())).findFirst();
            return member.isPresent() ? member.get() : null;
        }
        return null;
    }

    public ResponseDTO getParticipantList(Long sessionId) throws IOException {
        List<ParticipantListDTO> participantList = new ArrayList<>();
        List<Attendance> participants = attendanceRepository.getSessionTraineeBySessionId(sessionId);
        List<String> userIds = sessionRoleRepository.findDistinctMemberIdsForASession(sessionId);
        Session session = sessionRepository.findByIds(sessionId);
        TopicInfo details;
        Call<TopicInfo> topicRequest = entityDao.topicDetailWithProgramContentDTO(session.getTopicId(), true);
        retrofit2.Response topicResponse = topicRequest.execute();
        if (!topicResponse.isSuccessful()) {
            LOGGER.error("unable to fetch Content And Program details {}", topicResponse.errorBody().string());
            return HttpUtils.onFailure(org.springframework.http.HttpStatus.NOT_FOUND.value(), "Content details Not Available");
        } else {
            details = (TopicInfo) topicResponse.body();
        }


        for (Attendance attendance : participants) {
            try {
                ResponseDTO userDetails = userService.getUserDetails(attendance.getUserId(), null, true);
                User user = (User) userDetails.getResponse();
                if (user != null) {

                    participantList.add(new ParticipantListDTO(user.getName(), user.getPhoneNo(), user.getCountryCode(), user.getEmailId(), attendance.getScanInDateTime(), attendance.getScanOutDateTime(), sessionId, Constants.TRAINEE, Boolean.TRUE.equals(attendance.getScanOut()) ? Boolean.TRUE : Boolean.FALSE));
                }
            } catch (IOException e) {
                LOGGER.info("Error fetching User Info:{}", e);
            }
        }
        List<MemberListDTO> membersParticipantList = new ArrayList<>();
        for (String userId : userIds) {
            try {
                ResponseDTO userDetails = userService.getUserDetails(userId, null, true);
                User user = (User) userDetails.getResponse();
                if (user != null) {
                    boolean value = attendanceRepository.isAttestationCreated(user.getUserId(), sessionId);
                    membersParticipantList.add(new MemberListDTO(user.getName(), user.getPhoneNo(), user.getCountryCode(), user.getEmailId(), null, null, sessionId, sessionRoleRepository.collectRolesForMemberforSession(userId, sessionId), user.getUserId(), user.getPhoto(), value));
                }
            } catch (IOException e) {
                LOGGER.info("Error fetching User Info:{}", e);
            }

        }
        ParticipantDataDTO participantDataDTO = new ParticipantDataDTO(participantList, membersParticipantList, details.getContent());
        return HttpUtils.success(participantDataDTO, "Successfully fetched participants for session");
    }

    public ResponseDTO addSessionUrl(Long sessionId, SessionAdditionalLinksDTO additionalLinksDTO, BindingResult
            bindingResult) {
        userService.valiadtePojo(bindingResult);
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            Session session = sessionRepository.findByIds(sessionId);
            List<String> userIds = sessionRoleRepository.findUserIdOfMembersRelatedToSession(Arrays.asList(sessionId));
            userIds.add(session.getSessionCreator());
            for (String id : userIds) {
                if (id.equalsIgnoreCase(additionalLinksDTO.getUserId())) {
                    if (additionalLinksDTO.getUrl().contains("\n")) {
                        additionalLinksDTO.setUrl(additionalLinksDTO.getUrl().replace("\n", ""));
                    }
                    SessionLinks sessionLinks = new SessionLinks(session, additionalLinksDTO.getUrl(), additionalLinksDTO.getUserId());
                    sessionLinksRepository.save(sessionLinks);
                    responseDTO.setResponseCode(HttpStatus.OK.value());
                    responseDTO.setResponse(null);
                    responseDTO.setMessage("Sucessfully Updated Session Url");
                    break;
                } else {
                    responseDTO.setResponseCode(HttpStatus.UNAUTHORIZED.value());
                    responseDTO.setResponse(null);
                    responseDTO.setMessage("User is not Authorized to update Session Url");
                }
            }
            List<String> allUserIds = sessionRoleRepository.findUserIdsForASession(sessionId);
            if (!allUserIds.contains(session.getSessionCreator())) {
                allUserIds.add(session.getSessionCreator());
            }
            LocalDateTime dateTime = LocalDateTime.now();
            allUserIds.forEach(id -> notificationService.saveNotification(id, session, new NotificationDTO(null, Constants.ADD_ADDITIONAL_LINKS_TO_SESSION, sessionId, null, NotificationEvents.SESSION.toString(), dateTime.toLocalDate().toString() + " " + dateTime.toLocalTime().toString(), false, null)));
        } catch (Exception e) {
            return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "Unable to Update Session Url");
        }
        return responseDTO;
    }

    public ResponseDTO deleteSessionUrl(Long sessionId, String userId, Long sessionUrlId) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            Session session = sessionRepository.findByIds(sessionId);
            List<String> userIds = sessionRoleRepository.findUserIdOfMembersRelatedToSession(Arrays.asList(sessionId));
            userIds.add(session.getSessionCreator());
            for (String id : userIds) {
                if (id.equalsIgnoreCase(userId)) {
                    sessionLinksRepository.deleteLinksById(sessionUrlId);
                    responseDTO.setResponseCode(HttpStatus.OK.value());
                    responseDTO.setResponse(null);
                    responseDTO.setMessage("Sucessfully deleted Session Url");
                    break;
                } else {
                    responseDTO.setResponseCode(HttpStatus.UNAUTHORIZED.value());
                    responseDTO.setResponse(null);
                    responseDTO.setMessage("User is not Authorized to delete Session Url");
                }
            }
        } catch (Exception e) {
            LOGGER.info("EXCEPTION:{}", e);
            return HttpUtils.onFailure(HttpStatus.NOT_FOUND.value(), "Unable to Delete Session Url");
        }
        return responseDTO;


    }

    @Override
    public Set<Long> getSessionIdsByProgramId(long programId) {
        return sessionRepository.findSessionIdsByProgramId(programId);
    }


}

