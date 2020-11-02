package com.pda.session.dto.v2;

public class LinkedProgramsDTO {
    private Long programId;
    private String programName;

    public LinkedProgramsDTO(Long programId, String programName) {
        this.programId = programId;
        this.programName = programName;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }
}
