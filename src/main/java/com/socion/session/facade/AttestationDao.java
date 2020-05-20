package com.socion.session.facade;

import com.socion.session.dto.ResponseDTO;
import com.socion.session.dto.TemplateDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import java.io.IOException;
import java.util.List;

public interface AttestationDao {


    @POST("template/get-users-attestation")
    @Headers("Content-Type:application/json")
    Call<ResponseDTO> getAttestationDetails(@Body TemplateDto body) throws IOException;

    @POST("template/generate-multiple-attestation/")
    @Headers("Content-Type:application/json")
    Call<ResponseDTO> getMultiAttestationDetails(@Body List<TemplateDto> body) throws IOException;
}
