package com.pda.session.service;

import com.pda.session.dto.CleverTapEventData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.io.IOException;
import java.util.List;

public interface CleverTapDao {

    @POST("telemetry")
    Call<CleverTapEventData> uploadSingleEvent(
            @Body CleverTapEventData body) throws IOException;

    @POST("telemetry/multiple")
    Call<List<CleverTapEventData>> uploadMultipleEvent(
            @Body List<CleverTapEventData> body) throws IOException;

}
