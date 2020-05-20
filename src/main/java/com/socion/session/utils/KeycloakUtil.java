package com.socion.session.utils;

import org.keycloak.RSATokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeycloakUtil {

    protected static Logger logger = LoggerFactory.getLogger(KeycloakUtil.class);

        private static String publicKeyString = "";


    public static String fetchUserIdFromToken(String accessToken, String baseUrl, String realm) throws VerificationException {
        try {
            PublicKey publicKey = toPublicKey(publicKeyString);
            AccessToken token = RSATokenVerifier.verifyToken(accessToken, publicKey, baseUrl + "realms/" + realm);
            return token.getSubject();
        }
        catch(VerificationException e) {
            logger.error("Invalid access token. Please verify the access token : {}", e.getLocalizedMessage());
            throw new VerificationException(e);
        }
    }

    public static Boolean verifyToken(String accessToken, String baseUrl, String realm) throws VerificationException {
        try {
            PublicKey publicKey = toPublicKey(publicKeyString);
            AccessToken token = RSATokenVerifier.verifyToken(accessToken, publicKey, baseUrl + "realms/" + realm);
            return token.isActive();
        }
        catch(VerificationException e) {
            logger.error("Invalid access token. Please verify the access token : {}", e.getLocalizedMessage());
            throw new VerificationException(e);
        }
    }


    private static PublicKey toPublicKey(String publicKeyString){
        try{
            byte[] bytes = Base64.getDecoder().decode(publicKeyString);
            X509EncodedKeySpec keySpecification = new X509EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpecification);
        }
        catch(Exception e){
            logger.error("Error Creating public key");
            return null;
        }
    }
}