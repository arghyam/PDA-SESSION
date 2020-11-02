package com.pda.session.repository;

import com.pda.session.dao.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    @Nullable
    @Query(value = "select * from Attendance where user_id=?1 and session_id=?2 and role=?3", nativeQuery = true)
    Attendance findByUserIdAndSessionIdAndRole(String userId, Long sessionId, String role);

    @Query(value = "Select * from attendance where user_id=?1 and session_id=?2 and deleted=false and role=?3", nativeQuery = true)
    Attendance findByUserIdAndSessionId(@Param("userId") String userId, @Param("sessionId") Long sessionId, @Param("role") String role);

    @Query(value = "Select count(*)>0 from attendance where user_id=?1 and session_id=?2 and deleted=false and role!='TRAINEE'", nativeQuery = true)
    boolean isAttestationCreated(@Param("userId") String userId, @Param("sessionId") Long sessionId);

    @Query(value = "Select count(*)>0 from attendance where user_id=?1 and session_id=?2 and deleted=false and role=?3", nativeQuery = true)
    boolean isAttestationCreatedForUser(String userId,Long sessionId,String role);

    @Query(value = "Select * from attendance where user_id=?1 and is_scan_out=true", nativeQuery = true)
    List<Attendance> findByUserIdAndScannedOut(String userId);

    @Query(value = "Select session_id from attendance where user_id=?1 and is_scan_out=true", nativeQuery = true)
    List<Long> findSessionIdByUserIdAndScannedOut(String userId);

    @Query(value = "Select DISTINCT session_id from attendance where user_id=?1 and is_scan_out=true", nativeQuery = true)
    List<Long> findDistinctSessionIdByUserIdAndScannedOut(String userId);

    @Query(value = "select count(*) from Attendance where is_scan_out=true and session_id=?1", nativeQuery = true)
    Long numberofattestationspersession(Long sessionid);

    @Query(value = "select count(*) from Attendance where is_scan_out=true and session_id=?1 and role='TRAINEE'", nativeQuery = true)
    Long getAllParticipantCount(Long sessionId);

    @Query(value="select count(*) from attendance where created_at between DATE(?1) AND DATE(?2) and deleted=false and (role='TRAINER' or role='ADMIN')",nativeQuery =true)
    Long numberofmemberattgenerated(String date1,String date2);


    @Query(value = "Select count(*) from Attendance where session_Id=?1", nativeQuery = true)
    int findCountOfAttendanceBySessionId(@Param("sessionId") Long sessionId);

    @Query(value = "Select count(*) from Attendance where session_id=?1 and role='TRAINEE'", nativeQuery = true)
    int numberOfParticipantsAttendedSession(Long sessionId);

    @Query(value = "Select count(*)>0 from Attendance where session_id=?1 and role!='TRAINEE'", nativeQuery = true)
    boolean isMemberAttestationGenerated(Long sessionId);

    @Nullable
    @Query(value = "select * from Attendance where session_Id=?1 and role='TRAINEE'", nativeQuery = true)
    List<Attendance> getSessionTraineeBySessionId(@Param("sessionId") Long sessionId);

    @Query(value = "select user_id from Attendance where session_id in (?1) and is_scan_out=true", nativeQuery = true)
    Set<String> getUserIdsBySessionId(@Param("sessionIds") Set<Long> sessionId);

}

