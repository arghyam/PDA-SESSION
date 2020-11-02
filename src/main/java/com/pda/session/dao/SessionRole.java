package com.pda.session.dao;

import javax.persistence.*;

@Entity
@Table(name = "SessionRole")
public class    SessionRole extends BaseEntity {
    @GeneratedValue
    @Id
    private Long id;
    private String role;
    private String userId;
    @ManyToOne
    @JoinColumn(name = "session_Id")
    private Session session;
    private Boolean is_deleted;
    private String RoleDescription;
    private String otherRoleName;

    public SessionRole() {
    }

    public SessionRole(String role, String userId, Session session, String roleDescription, String otherRoleName, Boolean is_deleted) {
        this.role = role;
        this.userId = userId;
        this.session = session;
        RoleDescription = roleDescription;
        this.otherRoleName = otherRoleName;
        this.is_deleted = is_deleted;
    }


    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public Session getSession() {

        return session;
    }


    public void setSession(Session session) {
        this.session = session;
    }

    public String getRoleDescription() {
        return RoleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        RoleDescription = roleDescription;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOtherRoleName() {
        return otherRoleName;
    }

    public void setOtherRoleName(String otherRoleName) {
        this.otherRoleName = otherRoleName;
    }
}

