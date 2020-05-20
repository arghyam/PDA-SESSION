package com.socion.session.service;

import com.socion.session.dao.Attendance;
import com.socion.session.dao.Session;
import com.socion.session.dto.ResponseDTO;
import com.socion.session.dto.v2.SessionIdsDTO;
import com.socion.session.dto.v2.SessionOldDtoV2;
import com.socion.session.dto.v2.SessionidsattestationcountDTO;

import java.io.IOException;
import java.util.List;

public interface AttestationService {

    public ResponseDTO getAttestationsForLoggedInUser(String accessToken);

    public List<SessionidsattestationcountDTO> getnumberofattestations(SessionIdsDTO sessionIdsDTO);

    ResponseDTO getAttestationsWithSessionInfoForLoggedInUser(String accessToken) throws IOException;

    ResponseDTO getAttestationsWithSessionInfoForLoggedInUserOptimized(String accessToken) throws IOException;

    SessionOldDtoV2 getCompleteSessionInfoForAttestation(Session session, String loogedInUserID, Attendance attendance) throws IOException;

}
