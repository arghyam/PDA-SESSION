package com.socion.session.dto.v2;

public class MemberRoleDto {

    Boolean trainer;
    Boolean admin;
    Boolean other;
    String otherRoleNames;

    public MemberRoleDto() {
    }

    public MemberRoleDto(Boolean trainer, Boolean admin, Boolean other) {
        this.trainer = trainer;
        this.admin = admin;
        this.other = other;
    }



    public Boolean getTrainer() {
        return trainer;
    }

    public void setTrainer(Boolean trainer) {
        this.trainer = trainer;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getOther() {
        return other;
    }

    public void setOther(Boolean other) {
        this.other = other;
    }

    public String getOtherRoleNames() {
        return otherRoleNames;
    }

    public void setOtherRoleNames(String otherRoleNames) {
        this.otherRoleNames = otherRoleNames;
    }


}
