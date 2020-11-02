package com.pda.session.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppContext {


    @Value("${iam-baseurl}")
    private String iamBaseUrl;


    @Value("${getGenerateAttestationUrl}")
    private String getGenerateAttestationUrl;


    @Value("${server.nginx.name}")
    private String nginxServerName;

    @Value("${keycloak.auth-server-url}")
    private String keyCloakServiceUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${aws-accesskey}")
    private String awsAccessKey;

    @Value("${aws-secretkey}")
    private String awsSecretKey;

    @Value("${aws-s3-bucket-name}")
    private String awsS3BucketName;

    @Value("${aws-s3-region}")
    private String awsS3Region;

    @Value("${session-qr-code-path}")
    private String sessionQrCodePath;


    @Value("${aws-s3-url}")
    private String awsS3Url;

    @Value("${attestation.generate}")
    private String generateAttestationUrl;

    @Value("${attestation.generate.multiple}")
    private String generateMutipleAttestationUrl;

    @Value("${Entity.get}")
    private String getEntityUrl;

    @Value("${clevertap.account-id}")
    private String cleverTapAccId;

    @Value("${clevertap.passcode}")
    private String cleverTapPasscode;

    @Value("${clevertap.base.uri}")
    private String cleverTapBaseUri;

    @Value("${salt-value}")
    private String saltValue;

    @Value("${iv-value}")
    private String ivValue;

    @Value("${secret-key}")
    private String secretKey;

    @Value("${key-size}")
    private int keySize;

    @Value("${iteration-count}")
    private int iterationCount;

    @Value("${registry-base-url}")
    private String registryBaseUrl;

    @Value("${admin-user-username}")
    private String adminUserName;

    @Value("${admin-user-password}")
    private String adminUserpassword;

    @Value("${keycloak-client-id}")
    private String clientId;

    @Value("${client.granttype}")
    private String grantType;

    @Value("${sourcemailid}")
    private String sourceEmail;


    @Value("${sourceemailpassword}")
    private String sourceEmailPassword;

    @Value("${mail-smtp-auth}")
    private String smtpAuth;

    @Value("${mail-smtp-starttls-enable}")
    private String smtpMailTls;

    @Value("${mail-smtp-host}")
    private String smtpHost;

    @Value("${smtp-gmail-com}")
    private String smtpMail;

    @Value("${port}")
    private String port;

    @Value("${mail-smtp-port}")
    private String smtpPort;

    @Value("${cron-email-path}")
    private String cronEmailPath;

    @Value("${cron-email-subject}")
    private String cronEmailSubject;

    @Value("${keycloak-public-key}")
    private String keycloakPublickey;

    @Value("${aws-s3-session-qr-folder-name}")
    private  String awsS3SessionQrFolderName;

    @Value("${member-attestation-schedular-email}")
    private String memberAttestationSchedulerEmail;

    @Value("${session-end-minutes}")
    private Integer sessionEndMinutes;


    public String getIamBaseUrl() {
        return iamBaseUrl;
    }

    public String getGetGenerateAttestationUrl() {
        return getGenerateAttestationUrl;
    }

    public String getNginxServerName() {
        return nginxServerName;
    }

    public String getKeyCloakServiceUrl() {
        return keyCloakServiceUrl;
    }

    public String getRealm() {
        return realm;
    }

    public String getAwsAccessKey() {
        return awsAccessKey;
    }

    public String getAwsSecretKey() {
        return awsSecretKey;
    }

    public String getAwsS3BucketName() {
        return awsS3BucketName;
    }

    public String getAwsS3Region() {
        return awsS3Region;
    }

    public String getSessionQrCodePath() {
        return sessionQrCodePath;
    }

    public String getAwsS3Url() {
        return awsS3Url;
    }

    public String getGenerateAttestationUrl() {
        return generateAttestationUrl;
    }

    public String getGenerateMutipleAttestationUrl() {
        return generateMutipleAttestationUrl;
    }

    public String getGetEntityUrl() {
        return getEntityUrl;
    }


    public String getCleverTapAccId() {
        return cleverTapAccId;
    }

    public String getCleverTapPasscode() {
        return cleverTapPasscode;
    }

    public String getCleverTapBaseUri() {
        return cleverTapBaseUri;
    }

    public String getSaltValue() {
        return saltValue;
    }

    public String getIvValue() {
        return ivValue;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public int getKeySize() {
        return keySize;
    }

    public int getIterationCount() {
        return iterationCount;
    }

    public String getRegistryBaseUrl() {
        return registryBaseUrl;
    }

    public String getAdminUserName() {
        return adminUserName;
    }

    public String getAdminUserpassword() {
        return adminUserpassword;
    }

    public String getClientId() {
        return clientId;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getSourceEmail() {
        return sourceEmail;
    }

    public String getSourceEmailPassword() {
        return sourceEmailPassword;
    }

    public String getSmtpAuth() {
        return smtpAuth;
    }

    public String getSmtpMailTls() {
        return smtpMailTls;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public String getSmtpMail() {
        return smtpMail;
    }

    public String getPort() {
        return port;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public String getCronEmailPath() {
        return cronEmailPath;
    }

    public String getCronEmailSubject() {
        return cronEmailSubject;
    }

    public String getKeycloakPublickey() {
        return keycloakPublickey;
    }

    public String getAwsS3SessionQrFolderName() {
        return awsS3SessionQrFolderName;
    }

    public String getMemberAttestationSchedulerEmail() {
        return memberAttestationSchedulerEmail;
    }

    public void setMemberAttestationSchedulerEmail(String memberAttestationSchedulerEmail) {
        this.memberAttestationSchedulerEmail = memberAttestationSchedulerEmail;
    }

    public Integer getSessionEndMinutes() {
        return sessionEndMinutes;
    }

    public void setSessionEndMinutes(Integer sessionEndMinutes) {
        this.sessionEndMinutes = sessionEndMinutes;
    }
}
