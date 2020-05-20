package com.socion.session.repository;

import com.socion.session.dao.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Long countByUserIdAndIsReadAndIsDeleted(String userId, Boolean isRead, Boolean isDeleted);

    Long countByUserIdAndIsDeleted(String userId, Boolean isDeleted);

    @Query(value = "Select * from notification where user_id=?1 and is_deleted=false", nativeQuery = true)
    Page<Notification> findAllByUserIdAndIsDeleted(String userId, Boolean isDeleted, Pageable pageable);

    @Query(value = "Select * from notification where session_id=?1", nativeQuery = true)
    List<Notification> findBySessionId(@Param("sessionId") Long sessionId);

}
