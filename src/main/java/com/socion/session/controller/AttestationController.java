package com.socion.session.controller;

import com.socion.session.config.AppContext;
import com.socion.session.dto.ResponseDTO;
import com.socion.session.dto.v2.SessionIdsDTO;
import com.socion.session.dto.v2.SessionidsattestationcountDTO;
import com.socion.session.service.AttestationService;
import com.socion.session.service.v2.SessionServiceV2;
import com.socion.session.utils.HttpUtils;
import com.socion.session.utils.KeycloakUtil;
import org.keycloak.common.VerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(path = "/api/v2/session", produces = MediaType.APPLICATION_JSON_VALUE)
public class AttestationController {

    @Autowired
    AttestationService attestationService;

    @Autowired
    SessionServiceV2 sessionService;

    @Autowired
    AppContext appContext;

    @GetMapping("/my-attestations")
    public ResponseDTO getAttestationsForLoggedInUser(@RequestHeader("access-token") String accessToken) {
        return attestationService.getAttestationsForLoggedInUser(accessToken);
    }

    @PostMapping("/numberofattestationspersession")
    public List<SessionidsattestationcountDTO> getnumberofattestationsforlistsessions(@RequestBody SessionIdsDTO sessionIds) {

        return attestationService.getnumberofattestations(sessionIds);

    }


    @GetMapping("/my-attestations-session-info")
    public ResponseDTO getAttestationswithsessionInfoForLoggedInUser(@RequestHeader("access-token") String accessToken) throws IOException {
        return attestationService.getAttestationsWithSessionInfoForLoggedInUser(accessToken);

    }

    @GetMapping("/get-session-info-attestation-optimized")
    public ResponseDTO getAttestationswithsessionInfoForLoggedInUserOptimized(@RequestHeader("access-token") String accessToken) throws IOException {
        return attestationService.getAttestationsWithSessionInfoForLoggedInUserOptimized(accessToken);

    }


    @GetMapping("/my-attestations-session-info-optimized")
    public ResponseDTO getCompleteSessionInformation(@RequestHeader("access-token") String accessToken, @RequestParam("sessionId") Long sessionId, @RequestParam("role") String role) throws ParseException, IOException {
        try {
            boolean isTokenActive = KeycloakUtil.verifyToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm(),appContext.getKeycloakPublickey());

            if (isTokenActive) {
                return sessionService.getCompleteSessionInfoForAttestation(sessionId, accessToken, role);

            } else {
                return HttpUtils.onFailure(HttpStatus.UNAUTHORIZED.value(), "Invalid access token");
            }

        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);

        }
    }

    @GetMapping("/get-session-info-attestation")
    public ResponseDTO getCompleteSessionInformationOptimized(@RequestHeader("access-token") String accessToken, @RequestParam("sessionId") Long sessionId, @RequestParam("role") String role) throws ParseException, IOException {
        try {
            boolean isTokenActive = KeycloakUtil.verifyToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm(),appContext.getKeycloakPublickey());

            if (isTokenActive) {
                return sessionService.getCompleteSessionInfoForAttestationOptimized(sessionId, accessToken, role);

            } else {
                return HttpUtils.onFailure(HttpStatus.UNAUTHORIZED.value(), "Invalid access token");
            }

        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);

        }
    }

    @GetMapping("/get-attestation-details")
    public ResponseDTO getCompleteSessionDetailsForWeb(@RequestParam("userId") String userId, @RequestParam("sessionId") Long sessionId, @RequestParam("role") String role, @RequestParam(value = "ip-address", required = false) String ipAddress) throws ParseException, IOException {
        return sessionService.getCompleteAttestationInfoForWeb(userId, sessionId, role, ipAddress);
    }


}
