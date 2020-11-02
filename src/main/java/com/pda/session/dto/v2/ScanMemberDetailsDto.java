package com.pda.session.dto.v2;

public class ScanMemberDetailsDto {

    private String userId;
    private String name;
    private String photo;
    private boolean eligibleAsTrainer;

    public ScanMemberDetailsDto(String userId, String name, String photo, boolean eligibleAsTrainer) {
        this.userId = userId;
        this.name = name;
        this.photo = photo;
        this.eligibleAsTrainer = eligibleAsTrainer;
    }

    public ScanMemberDetailsDto() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isEligibleAsTrainer() {
        return eligibleAsTrainer;
    }

    public void setEligibleAsTrainer(boolean eligibleAsTrainer) {
        this.eligibleAsTrainer = eligibleAsTrainer;
    }
}
