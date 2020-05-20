package com.socion.session.aws;


import com.socion.session.config.AppContext;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;

@Component
@Service
public class AwsConfigServiceImpl implements AwsConfigService {

    @Autowired
    AppContext appContext;

    public AmazonS3 awsS3Configuration() {
        AWSCredentials credentials = new BasicAWSCredentials(
                appContext.getAwsAccessKey(),
                appContext.getAwsSecretKey()
        );

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(appContext.getAwsS3Region())
                .build();

    }

    public PutObjectResult putQrcodeInAwsS3(String pathOfCertificate, String sessionId,String type, AmazonS3 amazonS3,String sessionName) {

        PutObjectRequest request1 = new PutObjectRequest(appContext.getAwsS3BucketName() + appContext.getAwsS3SessionQrFolderName(), sessionId+"-"+sessionName+"-"+type, new File(pathOfCertificate));
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("x-amz-meta-title", "UserCard");
        request1.setMetadata(metadata);
        return amazonS3.putObject(request1);


    }


}
