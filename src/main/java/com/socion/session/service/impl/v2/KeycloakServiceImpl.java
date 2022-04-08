package com.socion.session.service.impl.v2;

import com.socion.session.config.AppContext;
import com.socion.session.dto.v2.AccessTokenResponseDTO;
import com.socion.session.facade.KeycloakDao;
import com.socion.session.facade.KeycloakService;
import com.socion.session.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Component
@Service
public class KeycloakServiceImpl implements KeycloakService {

    @Autowired
    KeycloakDao keycloakDao;

    @Autowired
    AppContext appContext;


    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakServiceImpl.class);


    @Override
    public String generateAccessToken(String username) {
        AccessTokenResponseDTO adminAccessTokenResponse = null;
        LOGGER.debug("Generating access Token for user : {} ", username);
        try {
            adminAccessTokenResponse = keycloakDao.generateAccessTokenUsingCredentials(appContext.getRealm(), appContext.getAdminUserName(),
                  //  Adding the keycloak client secret as a parameter for authentication.                                                                     
                    appContext.getAdminUserpassword(), appContext.getClientId(), appContext.getGrantType(), appContext.getClientSecret()).execute().body();
        } catch (IOException e) {
            LOGGER.error(Constants.ERRORLOG, e);
        }

        return adminAccessTokenResponse.getAccessToken();

    }
}
