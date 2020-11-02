package com.pda.session.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;

public interface AwsConfigService {

    public AmazonS3 awsS3Configuration();

    public PutObjectResult putQrcodeInAwsS3(String pathOfCard, String sessionId,String qrtype, AmazonS3 amazonS3,String sessionName);

}

