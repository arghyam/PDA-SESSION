package com.pda.session.facade;

import com.pda.session.dto.v2.RegistryUserWithOsId;
import org.springframework.stereotype.Repository;
import retrofit2.http.Path;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

import java.io.IOException;
import java.util.List;

@Repository
public interface IamDao {

    @GET("private/detail")
    @Headers("Content-Type:application/json")
    Call<RegistryUserWithOsId> getUser(@Query("callingUserId") String loggedInUserId,@Path("userId") String userId) throws IOException;

    //Call<RegistryUserWithOsId> getUser(@Query("callingUserId") String loggedInUserId,@Query("userId") String userId) throws IOException;

    @GET("private/details/list")
    @Headers("Content-Type:application/json")
    Call<List<RegistryUserWithOsId>> getAllUser(@Query("callingUserId") String loggedInUserId,@Query("userId") List<String> userIds) throws IOException;


}
