package com.socion.session.service;

import com.socion.session.dao.Attendance;
import com.socion.session.dto.AttendanceDTO;
import com.socion.session.dto.ResponseDTO;

import java.io.IOException;
import java.util.Set;

public interface AttendanceService {

    public ResponseDTO attendance(String accessToken, AttendanceDTO attendanceDTO) throws IOException;

    public ResponseDTO sendAttestationsToRegistry(String userId, Attendance attendance, AttendanceDTO attendanceDTO, String role) throws IOException;

    public ResponseDTO getLinkedPrograms(String accessToken);

    Set<String> getUserIdsAttendedByProgramId(long programId);
}
