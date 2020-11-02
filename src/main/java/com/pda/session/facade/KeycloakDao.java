package com.pda.session.facade;


import com.pda.session.utils.Constants;
import com.pda.session.dto.v2.AccessTokenResponseDTO;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface KeycloakDao {


    @POST(Constants.GENERATE_ACCESS_TOKEN)
    @FormUrlEncoded
    Call<AccessTokenResponseDTO> generateAccessTokenUsingCredentials(@Path("realm") String realm, @Field("username") String username,
                                                                     @Field("password") String password,
                                                                     @Field("client_id") String clientId,
                                                                     @Field("grant_type") String grantType,
                                                                     @Field("client_secret") String clientSecret);

}
