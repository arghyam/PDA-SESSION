package com.pda.session.controller.v2;

import com.pda.session.service.v2.UserService;
import com.pda.session.config.AppContext;
import com.pda.session.dto.ResponseDTO;
import com.pda.session.utils.HttpUtils;
import com.pda.session.utils.KeycloakUtil;
import org.keycloak.common.VerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2/session/user", produces = {"application/json"})
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppContext appContext;

    @GetMapping("/get-user-details")
    public ResponseDTO getUserDetails(@RequestHeader("access-token") String accessToken, @RequestParam("userId") String userId, @RequestParam("topicId") Long topicId) {
        try {
            KeycloakUtil.verifyToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm(),appContext.getKeycloakPublickey());
            return userService.getUserDetailsForActiveUser(userId, topicId, null, false);
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);
        }
    }

    @GetMapping("/get-trainee-details")
    public ResponseDTO getUserDetails(@RequestHeader("access-token") String accessToken, @RequestParam("userId") String userId) {
        try {
            KeycloakUtil.verifyToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm(),appContext.getKeycloakPublickey());
            return userService.getUserDetailsForActiveUser(userId, null, null, false);
        } catch (VerificationException e) {
            return HttpUtils.handleAccessTokenException(e);
        }
    }


}
