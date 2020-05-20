package com.socion.session.repository;

import com.socion.session.dao.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    @Nullable
    @Query(value = "Select * from Location where user_id=?1 and role='TRAINEE' and session_id=?2", nativeQuery = true)
    Location findByUserIdAndRoleAndSessionId(String userId, Long sessionId);

}
