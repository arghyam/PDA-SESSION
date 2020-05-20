package com.socion.session.repository;

import com.socion.session.dao.SessionLinks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionLinksRepository extends JpaRepository<SessionLinks, Long> {

    @Query(value = "DELETE FROM session_links where id=?1 returning null", nativeQuery = true)
    void deleteLinksById(Long id);

    @Query(value = "select * FROM session_links where session_id=?1", nativeQuery = true)
    List<SessionLinks> findSessionLinksBySessionid(Long id);
}
