package com.socion.session.service.v2;

import com.socion.session.dto.ResponseDTO;
import com.socion.session.dto.v2.MemberOldDtoV2;
import com.socion.session.dto.v2.SessionAdditionalLinksDTO;
import com.socion.session.dto.v2.SessionOldDtoV2;
import org.keycloak.common.VerificationException;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

public interface SessionServiceV2 {

    public ResponseDTO getSession(Long sessionId);

    public ResponseDTO createSession(SessionOldDtoV2 sessionOldDTO, BindingResult bindingResult);

    public ResponseDTO updateSession(SessionOldDtoV2 sessionOldDTO, BindingResult bindingResult, String loggedInUserId);

    public ResponseDTO addSessionUser(MemberOldDtoV2 member, String callingUserId) throws IOException;

    public ResponseDTO updateSessionUser(MemberOldDtoV2 member, String callingUserId);

    public ResponseDTO deleteSessionUser(String userId, Long sessionId, String callingUserId);

    public ResponseDTO deleteSession(Long sessionId);

    public ResponseDTO getAllSessionsForUser(String userId);

    public ResponseDTO getSessionUserDetailsBySessionId(Long sessionId, String loggedInUserId);

    public ResponseDTO getCompleteSessionInfo(Long sessionId, String accessToken, String loggedInUserId)
            throws VerificationException, ParseException, IOException;

    public ResponseDTO getCompleteSessionInfoForAttestationOptimized(Long sessionId, String accessToken, String role) throws  ParseException, IOException, VerificationException;

    public ResponseDTO getCompleteSessionInfoForAttestation(Long sessionId, String accessToken, String role) throws  ParseException, IOException, VerificationException;

    public ResponseDTO deleteSessionMultipleUser(List<String> userId, Long sessionId);

    public ResponseDTO getCompleteAttestationInfoForWeb(String userId, Long sessionId, String role, String ipAddress) throws ParseException, IOException;

    public ResponseDTO getParticipantList(Long sessionId) throws IOException;

    public ResponseDTO addSessionUrl(Long sessionId, SessionAdditionalLinksDTO additionalLinksDTO, BindingResult bindingResult);

    public ResponseDTO deleteSessionUrl(Long sessionId, String userId, Long sessionUrlId);

    Set<Long> getSessionIdsByProgramId(long programId);

}
