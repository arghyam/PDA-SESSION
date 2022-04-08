package com.socion.session.service.impl.v2;

import com.socion.session.dao.SessionRole;
import com.socion.session.dto.ResponseDTO;
import com.socion.session.dto.v2.RegistryUserWithOsId;
import com.socion.session.dto.v2.ScanMemberDetailsDto;
import com.socion.session.dto.v2.User;
import com.socion.session.exceptions.UnprocessableEntitiesException;
import com.socion.session.exceptions.ValidationError;
import com.socion.session.facade.EntityDao;
import com.socion.session.facade.IamDao;
import com.socion.session.service.v2.UserService;
import com.socion.session.utils.Constants;
import com.socion.session.utils.HttpUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import retrofit2.Call;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    IamDao iamDao;

    @Autowired
    EntityDao entityDao;

    public static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public ResponseDTO getUserDetails(String userId, String loggedInUserId, boolean piiFilter) throws IOException {

        User user = null;
        // Changed, since both the parameters in the method call were the same.
        Call<RegistryUserWithOsId> userRequest = iamDao.getUser(userId);
        retrofit2.Response userResponse = userRequest.execute();
        if (!userResponse.isSuccessful()) {
            LOGGER.error("unable to fetch user details {}", userResponse.errorBody().string());
            return HttpUtils.onFailure(500, "Unable to fetch user");
        } else {
            RegistryUserWithOsId userDetails = (RegistryUserWithOsId) userResponse.body();
            if (null != userDetails.getUserId() && !userDetails.getUserId().isEmpty()) {
                if (!userDetails.isActive()) {
                    return HttpUtils.onFailure(403, "Inactive User");
                }
                user = new User(userId, userDetails.getEmailId(), userDetails.getPhoneNumber(), userDetails.getName(), userDetails.isActive(), userDetails.getCountryCode(), userDetails.getPhoto());

            }

        }
        return HttpUtils.success(user, "Successfully added user to the session ");
    }

    @Override
    public ResponseDTO getUserDetailsForActiveUser(String userId, Long topicId, String loggedInUserId, boolean piiFilter) {
        SessionRole sessionRole1 = new SessionRole();
        sessionRole1.setUserId(userId);
        try {
            Call<RegistryUserWithOsId> userRequest = iamDao.getUser(userId);
            retrofit2.Response userResponse = userRequest.execute();
            Boolean isEligible = Boolean.FALSE;
            if(topicId!=null){
                Call<ResponseDTO> userRequest1 = entityDao.userEligiblity(userId, topicId);
                retrofit2.Response userResponse1 = userRequest1.execute();

                if (userResponse1.isSuccessful()) {
                    ResponseDTO eligiblityResponse = (ResponseDTO) userResponse1.body();
                    isEligible = (Boolean) eligiblityResponse.getResponse();
                }
            }


            if (!userResponse.isSuccessful()) {
                LOGGER.error("unable to fetch user details {}", userResponse.errorBody().string());
            } else {
                RegistryUserWithOsId userDetails = (RegistryUserWithOsId) userResponse.body();


                if (null != userDetails.getUserId() && !userDetails.getUserId().isEmpty()
                        && null != sessionRole1.getUserId()) {
                    if (!userDetails.isActive()) {
                        return HttpUtils.onFailure(403, "Inactive User");
                    }

                    return HttpUtils.success(new ScanMemberDetailsDto(userDetails.getUserId(), userDetails.getName(), userDetails.getPhoto(), isEligible), Constants.SCAN_MEMBER_SUCCESS);
                }
            }
        } catch (Exception e) {
            return HttpUtils.onFailure(HttpStatus.SC_NOT_FOUND, e.getMessage());
        }

        return HttpUtils.onFailure(HttpStatus.SC_NOT_FOUND, Constants.USER_NOT_FOUND);
    }



    public ResponseDTO getAllUserDetails(List<String> userId, String loggedInUserId, boolean piiFilter) {
        ResponseDTO responseDTO = new ResponseDTO();
        List<RegistryUserWithOsId> userDetails = null;
        try {
            Call<List<RegistryUserWithOsId>> userRequest = iamDao.getAllUser(loggedInUserId==null?"null":loggedInUserId, userId);
            retrofit2.Response userResponse = userRequest.execute();

            if (!userResponse.isSuccessful()) {
                LOGGER.error("unable to fetch user details {}", userResponse.errorBody().string());
                HttpUtils.onFailure(HttpStatus.SC_NOT_FOUND, "Error while fetching user details");
            }
            userDetails = (List<RegistryUserWithOsId>) userResponse.body();
        } catch (Exception e) {
            HttpUtils.onFailure(HttpStatus.SC_NOT_FOUND, e.getMessage());
        }
        responseDTO.setMessage("Sucessfully fetched user data");
        responseDTO.setResponse(userDetails);
        responseDTO.setResponseCode(HttpStatus.SC_OK);
        return responseDTO;

    }

    @Override
    public void valiadtePojo(BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<ValidationError> errorList = new ArrayList<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                ValidationError validationError = new ValidationError(error.getField(), error.getDefaultMessage());
                errorList.add(validationError);
            }
            throw new UnprocessableEntitiesException(errorList);
        }

    }

    public ResponseDTO getUserDetailsFromList(List<RegistryUserWithOsId> userDetailsList, String userId) {
        ResponseDTO responseDTO = new ResponseDTO();
        User user = null;
        for (RegistryUserWithOsId userDetails : userDetailsList) {
            if (null != userDetails.getUserId() && !userDetails.getUserId().isEmpty() && userDetails.getUserId().equalsIgnoreCase(userId)) {
                if (!userDetails.isActive()) {
                    return HttpUtils.onFailure(403, "Inactive User");
                }
                user = new User(userId, userDetails.getEmailId(), userDetails.getPhoneNumber(), userDetails.getName(), userDetails.isActive(), userDetails.getCountryCode(), userDetails.getPhoto());
            }
        }
        responseDTO.setMessage("Sucessfully fetched user Details");
        responseDTO.setResponse(user);
        responseDTO.setResponseCode(HttpStatus.SC_OK);
        return responseDTO;

    }
}
