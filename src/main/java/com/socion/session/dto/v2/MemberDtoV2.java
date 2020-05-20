package com.socion.session.dto.v2;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class MemberDtoV2 {
    @NotEmpty
    @NotNull(message = "Session Name is Mandatory")
    private String userId;
    private Long sessionId;
    private Long topicId;
    private List<String> role;
    private String roleDescription;
    private String otherRoleDescription;
    private String photo;
    private String name;

    public MemberDtoV2() {
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

    public void setRole(List<String> role) {
        this.role = role;
    }

    public List<String> getRole() {
        return role;
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

    public String getOtherRoleDescription() {
        return otherRoleDescription;
    }

    public void setOtherRoleDescription(String otherRoleDescription) {
        this.otherRoleDescription = otherRoleDescription;
    }
}
