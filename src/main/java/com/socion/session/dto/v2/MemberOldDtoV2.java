package com.socion.session.dto.v2;


public class MemberOldDtoV2 {

    private String userId;
    private Long sessionId;
    private Long topicId;
    private MemberRoleDto roles;
    private String roleDescription;
    private String photo;
    private String name;

    public MemberOldDtoV2() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public MemberRoleDto getRoles() {
        return roles;
    }

    public void setRoles(MemberRoleDto roles) {
        this.roles = roles;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
