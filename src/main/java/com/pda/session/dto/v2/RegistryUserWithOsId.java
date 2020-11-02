package com.pda.session.dto.v2;

import javax.validation.constraints.NotEmpty;

public class RegistryUserWithOsId {

    @NotEmpty
    private String name;
    private String emailId;
    private String salutation;
    @NotEmpty
    private String phoneNumber;
    @NotEmpty
    private String photo;
    @NotEmpty
    private String userId;
    @NotEmpty
    private String crtdDttm;
    @NotEmpty
    private String updtDttm;
    @NotEmpty
    private String osid;
    @NotEmpty
    private boolean active;
    @NotEmpty
    private String countryCode;
    private String profileCardUrl;
    protected boolean piiInfo;
    protected String country;
    protected String state;
    protected String district;
    protected String city;
    protected String latitude;
    protected String longitude;
    protected boolean baseLocation;


    public RegistryUserWithOsId(@NotEmpty String name, String emailId, String salutation, @NotEmpty String phoneNumber, @NotEmpty String photo, @NotEmpty String userId, @NotEmpty String crtdDttm, @NotEmpty String updtDttm, @NotEmpty String osid, @NotEmpty boolean active, @NotEmpty String countryCode, String profileCardUrl, boolean piiInfo, String country, String state, String district, String city, String latitude, String longitude, boolean baseLocation) {
        this.name = name;
        this.emailId = emailId;
        this.salutation = salutation;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
        this.userId = userId;
        this.crtdDttm = crtdDttm;
        this.updtDttm = updtDttm;
        this.osid = osid;
        this.active = active;
        this.countryCode = countryCode;
        this.profileCardUrl = profileCardUrl;
        this.piiInfo = piiInfo;
        this.country = country;
        this.state = state;
        this.district = district;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.baseLocation = baseLocation;
    }

    public RegistryUserWithOsId() {
    }

    public boolean isBaseLocation() {
        return baseLocation;
    }

    public void setBaseLocation(boolean baseLocation) {
        this.baseLocation = baseLocation;
    }

    public boolean isPiiInfo() {
        return piiInfo;
    }

    public void setPiiInfo(boolean piiInfo) {
        this.piiInfo = piiInfo;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCrtdDttm() {
        return crtdDttm;
    }

    public void setCrtdDttm(String crtdDttm) {
        this.crtdDttm = crtdDttm;
    }

    public String getUpdtDttm() {
        return updtDttm;
    }

    public void setUpdtDttm(String updtDttm) {
        this.updtDttm = updtDttm;
    }

    public String getOsid() {
        return osid;
    }

    public void setOsid(String osid) {
        this.osid = osid;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getProfileCardUrl() {
        return profileCardUrl;
    }

    public void setProfileCardUrl(String profileCardUrl) {
        this.profileCardUrl = profileCardUrl;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }


}
