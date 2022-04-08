package com.socion.session.config;

import com.socion.session.facade.*;
import com.socion.session.facade.*;
import com.socion.session.service.CleverTapDao;
import com.socion.session.utils.Constants;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;
/* This is a temporary change since we haven’t enabled SSL for the backend services. 
This change can be skipped if we have a valid hostname with a signed certificate for each backend service.*/


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

@Configuration
public class ServiceConfiguration {

    @Autowired
    AppContext appContext;

    private OkHttpClient setClient() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(Constants.ONE_TWENTY_SECONDS, TimeUnit.SECONDS)
                .readTimeout(Constants.ONE_TWENTY_SECONDS_L, TimeUnit.SECONDS)
                .writeTimeout(Constants.ONE_TWENTY_SECONDS_L, TimeUnit.SECONDS)
		.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                //TODO: Make this more restrictive
                return true;
            }
        });
        return httpClient.build();

    }

    @Bean
    public CleverTapDao initCleverTap() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(setClient())
                .baseUrl(appContext.getCleverTapBaseUri())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit.create(CleverTapDao.class);

    }

    @Bean
    public IamDao initIam() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(setClient())
                .baseUrl(appContext.getIamBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit.create(IamDao.class);
    }

    @Bean
    public AttestationDao initAttestation() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(setClient())
                .baseUrl(appContext.getGenerateAttestationUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit.create(AttestationDao.class);
    }

    @Bean
    public EntityDao initEntity() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(setClient())
                .baseUrl(appContext.getGetEntityUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit.create(EntityDao.class);
    }

    @Bean
    public RegistryDao initRegistry() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(setClient())
                .baseUrl(appContext.getRegistryBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit.create(RegistryDao.class);
    }

    @Bean
    public KeycloakDao initKeyCloak() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(setClient())
                .baseUrl(appContext.getKeyCloakServiceUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit.create(KeycloakDao.class);

    }
}
