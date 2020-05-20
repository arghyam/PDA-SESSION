package com.socion.session.facade;


import com.socion.session.dto.v2.RegistryRequest;
import com.socion.session.dto.v2.RegistryResponse;
import com.socion.session.utils.Constants;
import org.springframework.stereotype.Repository;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

import java.io.IOException;

@Repository
public interface RegistryDao {

    @POST(Constants.REGISRY_ADD_USER)
    Call<RegistryResponse> addAttestations(@Header("x-authenticated-user-token") String adminAccessToken,
                                           @Body RegistryRequest registryRequest) throws IOException;
}
