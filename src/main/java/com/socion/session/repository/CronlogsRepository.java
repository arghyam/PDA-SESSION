package com.socion.session.repository;

import com.socion.session.dao.Cronlogsdao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface CronlogsRepository extends JpaRepository<Cronlogsdao ,Long> {

    @Query(value = "SELECT * FROM cronlogsdao ORDER BY id DESC LIMIT 1 ", nativeQuery = true)
    Cronlogsdao lastentry();
}