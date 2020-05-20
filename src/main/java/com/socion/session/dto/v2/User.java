package com.socion.session.dto.v2;


public class User {

    private String userId;
    private String emailId;
    private String PhoneNo;
    private String name;
    private boolean active;
    private String countryCode;
    private String photo;

    public User() {
    }

    public User(String userId, String emailId, String phoneNo, String name, boolean active, String countryCode, String photo) {
        this.userId = userId;
        this.emailId = emailId;
        PhoneNo = phoneNo;
        this.name = name;
        this.active = active;
        this.countryCode = countryCode;
        this.photo = photo;
    }


    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPhoneNo() {
        return PhoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        PhoneNo = phoneNo;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


}