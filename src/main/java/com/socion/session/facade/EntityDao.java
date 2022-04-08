package com.socion.session.facade;

import com.socion.session.dto.CleverTapEventData;
import com.socion.session.dto.ResponseDTO;
import com.socion.session.dto.TemplateDto;
import com.socion.session.dto.v2.TopicDTO;
import com.socion.session.dto.v2.TopicInfo;
import com.socion.session.dto.v2.TopicSessionLinkedDTO;
import org.springframework.stereotype.Repository;
import retrofit2.Call;
import retrofit2.http.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@Repository
public interface EntityDao {

    @GET("eligible/topic/{topicId}/user/{userId}")
    Call<ResponseDTO> userEligiblity(@Path("userId") String userId, @Path("topicId") Long topicid) throws IOException;

    @GET("topic/detail/{id}/status/{deleted}")
    Call<TopicInfo> topicDetailWithProgramContentDTO(@Path("id") Long topicId, @Path("deleted") boolean deleted) throws IOException;

    @POST("topic/details")
    @Headers("Content-Type:application/json")
    Call<List<TopicInfo>> multipleTopicDetailWithProgramContentDTO(@Query("topicIds") List<BigInteger> topicIds) throws IOException;

    @GET("private/topic/list")
    @Headers("Content-Type:application/json")
    Call<ResponseDTO> getPrograms(@Query("topicIds") List<BigInteger> topicIds) throws IOException;

    @GET("topic/{id}")
    Call<TopicInfo> getTopic(@Path("id") Long topicIds) throws IOException;

    @GET("topic/{id}")
    Call<TopicDTO> getTopicdetails(@Path("id") Long topicIds) throws IOException;

    @POST("template/get-users-attestation")
    @Headers("Content-Type:application/json")
    Call<ResponseDTO> getAttestationDetails(@Body TemplateDto body) throws IOException;

    @POST("template/generate-attestation")
    @Headers("Content-Type:application/json")
    Call<ResponseDTO> getAttestation(@Body TemplateDto body) throws IOException;

    @POST("template/generate-multiple-attestation/")
    @Headers("Content-Type:application/json")
    Call<ResponseDTO> getMultiAttestationDetails(@Body List<TemplateDto> body) throws IOException;

    @POST("telemetry")
    @Headers("Content-Type:application/json")
    Call<CleverTapEventData> add(@Body CleverTapEventData cleverTapEventData) throws IOException;

    @POST("list/telemetry")
    @Headers("Content-Type:application/json")
    Call<List<CleverTapEventData>> addMultiple(@Body List<CleverTapEventData> cleverTapEventData) throws IOException;

    @PUT("topic/{id}")
    @Headers("Content-Type:application/json")
    Call<TopicDTO> update(@Body TopicDTO topicDTO, @Path("id") Long id);

    @PUT("topic/linkSession")
    @Headers("Content-Type:application/json")
    Call<TopicSessionLinkedDTO> updateSessionLinkStatus(@Body TopicSessionLinkedDTO topicSessionLinkedDTO);


}

// This change had to be done since the API using the DTO was broken and this is part of the fix.
