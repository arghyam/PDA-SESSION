package com.socion.session.controller;

import com.socion.session.config.AppContext;
import com.socion.session.dto.AttendanceDTO;
import com.socion.session.dto.ResponseDTO;
import com.socion.session.service.AttendanceService;
import com.socion.session.utils.KeycloakUtil;
import org.keycloak.common.VerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(path = "/api/v2/session", produces = MediaType.APPLICATION_JSON_VALUE)
public class AttendanceController {

    @Autowired
    AttendanceService service;

    @Autowired
    AppContext appContext;

    @PostMapping(path = "/attendance", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO attendance(@RequestHeader("access-token") String accessToken, @RequestBody AttendanceDTO attendanceDTO) throws IOException {
        return service.attendance(accessToken, attendanceDTO);

    }

    @GetMapping(path = "/link/programs")
    public ResponseDTO getConnectedUserPrograms(@RequestHeader("access-token") String accessToken) throws VerificationException {
       KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm(),appContext.getKeycloakPublickey());
            return service.getLinkedPrograms(accessToken);

    }

    @GetMapping(path = "/private/attendance/program/{programId}/user")
    public Set<String> attendanceCountByProgramId(@PathVariable(value = "programId" , name = "programId") long programId) throws IOException {
        return service.getUserIdsAttendedByProgramId(programId);
    }

}
