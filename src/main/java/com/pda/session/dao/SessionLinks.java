package com.pda.session.dao;

import javax.persistence.*;

@Entity
@Table(name = "SessionLinks")
public class SessionLinks {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_Id")
    private Session session;

    private String sessionUrl;
    private String userId;


    public SessionLinks(Session session, String sessionUrl, String userId) {
        this.session = session;
        this.sessionUrl = sessionUrl;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public SessionLinks() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getSessionUrl() {
        return sessionUrl;
    }

    public void setSessionUrl(String sessionUrl) {
        this.sessionUrl = sessionUrl;
    }
}
