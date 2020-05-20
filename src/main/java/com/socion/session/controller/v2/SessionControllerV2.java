package com.socion.session.controller.v2;

import com.socion.session.config.AppContext;
import com.socion.session.dto.ResponseDTO;
import com.socion.session.dto.v2.*;
import com.socion.session.dto.v2.*;
import com.socion.session.facade.EntityDao;
import com.socion.session.service.v2.SessionServiceV2;
import com.socion.session.service.v2.UserService;
import com.socion.session.utils.HttpUtils;
import com.socion.session.utils.KeycloakUtil;
import org.keycloak.common.VerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import retrofit2.Call;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(value = "/api/v2/session", produces = {"application/json"})
public class SessionControllerV2 {

    @Autowired
    SessionServiceV2 sessionService;

    @Autowired
    AppContext appContext;

    @Autowired
    EntityDao entityDao;

    @Autowired
    UserService userService;

    protected static final Logger LOGGER = LoggerFactory.getLogger(SessionControllerV2.class);

    @GetMapping("/{id}")
    public ResponseDTO getSession(@RequestHeader("access-token") String accessToken, @PathVariable("id") Long id) {
        try {
            KeycloakUtil.verifyToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            return sessionService.getSession(id);
        } catch (VerificationException exception) {
            return HttpUtils.handleAccessTokenException(exception);
        }
    }

    @PostMapping("/create")
    public ResponseDTO createSession(@RequestHeader("access-token") String accessToken, @Validated @RequestBody SessionOldDtoV2 sessionOldDTO, BindingResult bindingResult) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            String userId = KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            sessionOldDTO.setSessionCreator(userId);
            responseDTO = sessionService.createSession(sessionOldDTO, bindingResult);
        } catch (VerificationException exception) {
            responseDTO = HttpUtils.handleAccessTokenException(exception);
        }
        return responseDTO;
    }

    @PutMapping("/update")
    public ResponseDTO updateSession(@RequestHeader("access-token") String accessToken, @Validated @RequestBody SessionOldDtoV2 sessionOldDTO, BindingResult bindingResult) {
        try {
            String callingUserId = KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            return sessionService.updateSession(sessionOldDTO, bindingResult, callingUserId);
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);
        }
    }

    @PostMapping("/member/add")
    public ResponseDTO addMemberToSession(@RequestHeader("access-token") String accessToken, @RequestBody MemberOldDtoV2 memberOldDtoV2) throws IOException {
        try {
            String callingUserId = KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            return sessionService.addSessionUser(memberOldDtoV2, callingUserId);
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);
        }
    }

    @PutMapping("/member/update")
    public ResponseDTO updateSessionMemberRole(@RequestHeader("access-token") String accessToken, @RequestBody MemberOldDtoV2 memberOldDtoV2) {
        try {
            String callingUserId = KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            return sessionService.updateSessionUser(memberOldDtoV2, callingUserId);
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);
        }
    }

    @DeleteMapping("/member/remove")
    public ResponseDTO deleteSessionMember(@RequestHeader("access-token") String accessToken, @RequestParam("userId") String userIds, @RequestParam("sessionId") Long sessionId) {
        try {

            String callingUserId = KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            return sessionService.deleteSessionUser(userIds, sessionId, callingUserId);
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);
        }
    }

    @DeleteMapping("/member/remove-multiple")
    public ResponseDTO deleteMutipleSessionMember(@RequestHeader("access-token") String accessToken, @RequestParam List<String> userIds, @RequestParam("sessionId") Long sessionId) {
        try {
            KeycloakUtil.verifyToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            return sessionService.deleteSessionMultipleUser(userIds, sessionId);
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseDTO deleteSession(@RequestHeader("access-token") String accessToken, @PathVariable("id") Long id) {
        try {
            KeycloakUtil.verifyToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            return sessionService.deleteSession(id);
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);
        }
    }

    @GetMapping(value = "/list")
    public ResponseDTO getAllSessionsForUser(@RequestHeader("access-token") String accessToken) {
        try {
            String userId = KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            return sessionService.getAllSessionsForUser(userId);
        } catch (VerificationException exception) {
            return HttpUtils.handleAccessTokenException(exception);
        }
    }

    @GetMapping("/get-session-users")
    public ResponseDTO getSessionUserDetails(@RequestHeader("access-token") String accessToken, @RequestParam("sessionId") Long sessionId) {
        try {

            String callingUserId = KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            return sessionService.getSessionUserDetailsBySessionId(sessionId, callingUserId);
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);

        }
    }

    @GetMapping("/get-complete-session-information/{id}")
    public ResponseDTO getCompleteSessionInformation(@RequestHeader("access-token") String accessToken, @PathVariable("id") Long sessionId) throws ParseException, IOException {
        try {
            String userId = KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            return sessionService.getCompleteSessionInfo(sessionId, accessToken, userId);
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);

        }
    }

    @GetMapping("/topic/{id}")
    public ResponseDTO getTopic(@RequestHeader("access-token") String accessToken, @PathVariable("id") Long topicId) throws IOException {
        try {

            String userId = KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            ResponseDTO responseDTO = userService.getUserDetailsForActiveUser(userId, topicId, userId, false);
            ScanMemberDetailsDto scanMemberDetailsDto = (ScanMemberDetailsDto) responseDTO.getResponse();

            if (responseDTO.getResponseCode() == HttpStatus.FORBIDDEN.value()) {
                return HttpUtils.onFailure(400, responseDTO.getMessage());

            }
            if (!scanMemberDetailsDto.isEligibleAsTrainer()) {
                return HttpUtils.onFailure(400, "User not eligible for the program");
            }
            TopicInfo details;
            Call<TopicInfo> userRequest = entityDao.topicDetailWithProgramContentDTO(topicId, false);
            retrofit2.Response userResponse = userRequest.execute();

            if (!userResponse.isSuccessful()) {
                LOGGER.error("unable to fetch Content And Program details {}", userResponse.errorBody().string());
                return HttpUtils.onFailure(400, "Content And Program details Not Available");
            } else {
                details = (TopicInfo) userResponse.body();
            }
            if (details.isDeleted()) {
                LOGGER.error("Topic is deactivated");
                return HttpUtils.onFailure(400, "Session cannot be created with the deactivated topic.");

            }

            Call<ResponseDTO> usereligibility = entityDao.userEligiblity(userId, topicId);
            retrofit2.Response usereligibilityresponse = usereligibility.execute();
            if (null == usereligibilityresponse) {
                return HttpUtils.onFailure(400, "User not eligible for the program");
            }


            return HttpUtils.success(details, "fetched topic");
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);

        }
    }

    @GetMapping("/get-participant-list/{sessionId}")
    public ResponseDTO getParticipantDetails(@PathVariable("sessionId") Long sessionId) throws IOException {
        return sessionService.getParticipantList(sessionId);
    }

    @PostMapping("link/{sessionId}")
    public ResponseDTO addSessionLinks(@RequestHeader("access-token") String accessToken, @Validated @RequestBody SessionAdditionalLinksDTO additionalLinksDTO, BindingResult bindingResult, @PathVariable("sessionId") Long sessionId) {
        try {
            KeycloakUtil.verifyToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            return sessionService.addSessionUrl(sessionId, additionalLinksDTO, bindingResult);
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);
        }
    }


    @DeleteMapping("/link/{sessionId}/{linkId}")
    public ResponseDTO deleteSessionLinks(@RequestHeader("access-token") String accessToken, @PathVariable("linkId") Long sessionUrlId, @PathVariable("sessionId") Long sessionId) {
        try {
            String userId = KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            return sessionService.deleteSessionUrl(sessionId, userId, sessionUrlId);
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);
        }
    }
}

