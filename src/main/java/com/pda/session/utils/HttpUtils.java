package com.pda.session.utils;

import com.pda.session.dto.ResponseDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.keycloak.common.VerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class HttpUtils {

    private HttpUtils() {
    }

    protected static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    public static ResponseDTO onSuccess(Object responseObject, String message) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setResponseCode(HttpStatus.OK.value());
        responseDTO.setMessage(message);
        if (responseObject != null) {
            Gson gson = new GsonBuilder().create();
            String response = (gson).toJson(responseObject);
            responseDTO.setResponse(response);
        }
        return responseDTO;
    }

    public static ResponseDTO success(Object responseObject, String message) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setResponseCode(HttpStatus.OK.value());
        responseDTO.setMessage(message);
        responseDTO.setResponse(responseObject);
        return responseDTO;
    }

    public static ResponseDTO onFailure(int statusCode, String message) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setResponseCode(statusCode);
        responseDTO.setMessage(message);
        return responseDTO;
    }

    public static ResponseDTO handleAccessTokenException(VerificationException accessTokenException) {
        ResponseDTO responseDTO = new ResponseDTO();
        LOGGER.error("Issue with Access token, please try again. Reason : {} ", accessTokenException.getMessage());
        responseDTO.setMessage("Issue with Access token, please try again. Reason : " + accessTokenException.getMessage());
        responseDTO.setResponseCode(HttpStatus.UNAUTHORIZED.value());
        return responseDTO;
    }

}
