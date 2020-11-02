package com.pda.session.dto.v2;

import java.util.List;

public class ParticipantDataDTO {
    private List<ParticipantListDTO> participants;
    private List<MemberListDTO> members;
    private List<ContentDTO> contents;


    public ParticipantDataDTO(List<ParticipantListDTO> participants, List<MemberListDTO> members, List<ContentDTO> contents) {
        this.participants = participants;
        this.members = members;
        this.contents = contents;
    }

    public List<ParticipantListDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantListDTO> participants) {
        this.participants = participants;
    }

    public List<MemberListDTO> getMembers() {
        return members;
    }

    public void setMembers(List<MemberListDTO> members) {
        this.members = members;
    }

    public List<ContentDTO> getContents() {
        return contents;
    }

    public void setContents(List<ContentDTO> contents) {
        this.contents = contents;
    }
}
