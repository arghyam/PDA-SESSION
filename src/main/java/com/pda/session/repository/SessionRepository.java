package com.pda.session.repository;

import com.pda.session.dao.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface SessionRepository extends JpaRepository<Session, Long> {

    @Nullable
    @Query(value = "Select * from session where id=?1 and is_deleted=false", nativeQuery = true)
    Session findByIds(Long id);

    @Query(value = "Select * from session where id=?1 and is_deleted=false", nativeQuery = true)
    List<Session> findBysessionId(@Param("sessionId") BigInteger sessionId);

    @Query(value = "UPDATE session set is_deleted='true' where id=?1 returning null", nativeQuery = true)
    void softDelete(@Param("sessionId") Long sessionId);


    @Query(value = "SELECT distinct id FROM Session where session_creator=?1 and is_deleted=false", nativeQuery = true)
    List<BigInteger> getMySessions(@Param("userId") String userId);

    @Query(value = "select * from session where is_deleted=false", nativeQuery = true)
    List<Session> findByIdIn(String userId);

    @Query(value = "select * from session where is_deleted=false and id in (?1)", nativeQuery = true)
    List<Session> findByIdList(List<Long> sessionIds);

    @Query(value = "select DISTINCT topic_id from session where id in (?1)", nativeQuery = true)
    List<BigInteger> findByTopicIdList(List<Long> sessionIds);

    @Nullable
    @Query(value = "select DISTINCT session_time_zone from session where id in (?1)", nativeQuery = true)
    List<String> findBySessionTimeZoneList(List<Long> sessionIds);

    @Query(value = "select id from Session where session_end_date_utc_time between (?1) AND (?2) and is_deleted=false", nativeQuery = true)
    List<Long> findCompletedSessionOnThisDate(String date1, String date2);

    @Query(value = "Select session_start_date from Session where is_deleted=false and id=?1 ORDER BY session_start_date LIMIT 1 ;", nativeQuery = true)
    String findScanInBySessionId(Long sessionId);

    @Query(value = "select session_end_date from Session where is_deleted=false and id=?1 ORDER BY session_end_date LIMIT 1 ;", nativeQuery = true)
    String findScanOutBySessionId(Long sessionId);

    @Query(value = "select count(*) from session where topic_id=?1 and is_deleted='false'", nativeQuery = true)
    int findDistinctSessionsForTopic(Long topicId);

    @Query(value = "select id from Session where program_id=?1", nativeQuery = true)
    Set<Long> findSessionIdsByProgramId(Long programId);
}
