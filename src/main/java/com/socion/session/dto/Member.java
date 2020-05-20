package com.socion.session.dto;

import java.util.List;

public class Member {

    private String userId;
    private List<String> roles;
    private String optionalRoleDescription;
    private List<String> resourceList;
    private Long sessionId;
    private String name;
    private String photo;

    public Member() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getOptionalRoleDescription() {
        return optionalRoleDescription;
    }

    public void setOptionalRoleDescription(String optionalRoleDescription) {
        this.optionalRoleDescription = optionalRoleDescription;
    }

    public List<String> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<String> resourceList) {
        this.resourceList = resourceList;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
