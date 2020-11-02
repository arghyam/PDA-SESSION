package com.pda.session.service;

import com.pda.session.dao.Attendance;
import com.pda.session.dto.AttendanceDTO;
import com.pda.session.dto.ResponseDTO;

import java.io.IOException;
import java.util.Set;

public interface AttendanceService {

    public ResponseDTO attendance(String accessToken, AttendanceDTO attendanceDTO) throws IOException;

    public ResponseDTO sendAttestationsToRegistry(String userId, Attendance attendance, AttendanceDTO attendanceDTO, String role) throws IOException;

    public ResponseDTO getLinkedPrograms(String accessToken);

    Set<String> getUserIdsAttendedByProgramId(long programId);
}
