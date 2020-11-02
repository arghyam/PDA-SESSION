package com.pda.session.dto;

import com.pda.session.dao.Session;

import java.util.List;

public class MemberAttestationInfo {

    Session session;
    List<User> members;

    public MemberAttestationInfo() {
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }


}
