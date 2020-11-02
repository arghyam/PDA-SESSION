package com.pda.session.repository;

import com.pda.session.dao.Session;
import com.pda.session.dao.SessionRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface SessionRoleRepository extends JpaRepository<SessionRole, Long> {

    @Query(value = "SELECT distinct session_id FROM Session_role where user_id=?1 and role!='TRAINEE' and is_deleted=false", nativeQuery = true)
    List<BigInteger> getMySessions(@Param("userId") String userId);

    @Query(value = "UPDATE session_role set is_deleted='true' where user_id=?1 and session_id=?2 returning null", nativeQuery = true)
    void memberSoftDelete(String userId, Long sessionId);

    @Query(value = "Select distinct user_id from Session_role where session_id=?1 and is_deleted=false", nativeQuery = true)
    List<String> findUserIdsForASession(Long sessionId);

    @Query(value = "Select DISTINCT user_id from Session_role where session_id=?1 and is_deleted=false and role!='TRAINEE'", nativeQuery = true)
    List<String> findDistinctMemberIdsForASession(Long sessionId);

    @Query(value="select count(*) from session_role where created_at between DATE(?1) AND DATE(?2) and is_deleted=false and role!='TRAINEE'",nativeQuery =true)
    Long totalrecords(String date1,String date2);
    @Nullable
    @Query(value = "select * from Session_role where user_id=?1 and session_id=?2 and is_deleted=false", nativeQuery = true)
    List<SessionRole> findBySessionAndUserId(String userId, Session sessionId);

    @Nullable
    @Query(value = "select count(*) from Session_role where user_id=?1 and session_id=?2 and is_deleted=false", nativeQuery = true)
    int findBySessionAndUser(String userId, Long sessionId);

    @Query(value = "Delete from session_role  where session_id=?1 returning null", nativeQuery = true)
    void deleteSessionById(Session session);

    @Query(value = "delete from session_role where user_id=?1 and session_id=?2 returning null", nativeQuery = true)
    void deleteByUserId(String userId, Session sessionId);

    @Query(value = "delete from session_role where user_id=?1 and session_id=?2 returning null", nativeQuery = true)
    void deleteUserByUserIDAndSessionId(String userId, Session session);

    @Query(value = "Select  role from Session_Role where user_Id =?1 and session_Id=?2 ", nativeQuery = true)
    List<String> collectRolesForUserforSession(String userId, Long sessionId);

    @Query(value = "Select CASE WHEN role='OTHER' THEN other_role_name ELSE role END AS role from Session_Role where user_Id =?1 and session_Id=?2 and role !='TRAINEE'", nativeQuery = true)
    List<String> collectRolesForMemberforSession(String userId, Long sessionId);


    @Query(value = "Select Distinct session_id from Session_role where role='TRAINEE' AND session_Id in (?1)", nativeQuery = true)
    List<BigInteger> findSessionswithAtleastOneParticipant(@Param("sessionIds") List<Long> sessionIds);


    @Query(value = "Select * from Session_role where role!='TRAINEE' and session_Id in (?1)", nativeQuery = true)
    List<SessionRole> findMembersRelatedToSession(List<Long> sessionId);

    @Query(value = "Select DISTINCT user_id from Session_role where role!='TRAINEE' and session_Id in (?1)", nativeQuery = true)
    List<String> findUserIdOfMembersRelatedToSession(List<Long> sessionId);

    @Query(value = "select count(*)>0 from session_role where user_id=?1 and session_id=?2", nativeQuery = true)
    Boolean isUserAlreadyAddedtoSession(@Param("userId") String userId, @Param("sessionId") Long sessionId);

    @Query(value = "select * from session_role where session_id=?1 and (role='TRAINER' or role='OTHER' or role='ADMIN')", nativeQuery = true)
    List<SessionRole> getrolesForSession(Long sessionId);

    @Query(value = "select other_role_name from Session_Role where session_Id=?1 and role='OTHER' and user_Id=?2", nativeQuery = true)
    String getOtherRoleNameOfUserForASession(Long sessionId, String userId);

    @Query(value = "select count(*) from Session_Role where session_id=?1 and role!='TRAINEE'", nativeQuery = true)
    Long getMembersCount(Long sessionId);

    @Query(value = "select count(*) from Session_Role where session_id=?1 and role='TRAINEE'", nativeQuery = true)
    Long getAllParticipantCount(Long sessionId);

    @Nullable
    @Query(value = "select * from Session_Role where session_Id=?1 and role='TRAINER'", nativeQuery = true)
    List<SessionRole> getSessionTrainersBySessionId(@Param("sessionId") Long sessionId);

    @Nullable
    @Query(value = "select * from Session_Role where session_Id=?1 and role!='TRAINEE'", nativeQuery = true)
    List<SessionRole> getSessionMembersBySessionId(@Param("sessionId") Long sessionId);

    @Query(value = "SELECT * from session_role  where session_Id=?1", nativeQuery = true)
    List<SessionRole> getSessionRoleBuSessionId(@Param("sessionId") Long sessionId);

    @Query(value = "select count(*)>0 from session_role where user_id=?1 and session_id=?2 and role='TRAINER'", nativeQuery = true)
    Boolean getRoleByUserIdAndSessionIdForTrainer(String userId, Session session);

    @Query(value = "select count(*)>0 from session_role where user_id=?1 and session_id=?2 and (role='TRAINER' or role='OTHER' or role='ADMIN')", nativeQuery = true)
    Boolean getMembersForSession(String userId, Long sessionId);
}

