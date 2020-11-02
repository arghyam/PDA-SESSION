package com.pda.session.service.v2;

import com.pda.session.dto.ResponseDTO;
import com.pda.session.dto.v2.RegistryUserWithOsId;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.util.List;

public interface UserService {

    public ResponseDTO getUserDetailsForActiveUser(String userId, Long topicId, String loggedInUserId, boolean piiFilter);

    public ResponseDTO getUserDetails(String userId, String loggedInUserId, boolean piiFilter) throws IOException;

    public ResponseDTO getAllUserDetails(List<String> userId, String loggedInUserId, boolean piiFilter) throws IOException;

    public void valiadtePojo(BindingResult bindingResult);

    ResponseDTO getUserDetailsFromList(List<RegistryUserWithOsId> userDetailsList, String userId);
}
