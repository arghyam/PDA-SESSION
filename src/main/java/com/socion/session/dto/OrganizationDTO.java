package com.socion.session.dto;


import java.io.Serializable;


public class OrganizationDTO implements Serializable {

    private int orgId;
    private String orgName;

    public OrganizationDTO() {
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}