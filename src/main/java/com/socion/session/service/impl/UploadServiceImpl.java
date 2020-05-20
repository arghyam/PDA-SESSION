package com.socion.session.service.impl;

import com.socion.session.config.AppContext;
import com.socion.session.dto.ResponseDTO;
import com.socion.session.service.UploadService;
import com.socion.session.utils.Constants;
import com.socion.session.utils.FileUtils;
import com.socion.session.utils.HttpUtils;
import com.amazonaws.AmazonClientException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

@Service
public class UploadServiceImpl implements UploadService {



    private static final Logger LOGGER = LoggerFactory.getLogger(UploadServiceImpl.class);

    @Autowired
    AmazonS3 amazonClient;

    @Autowired
    AppContext appContext;

    @Override
    public ResponseDTO uploadContentToS3(MultipartFile file) {
        if (null != file) {
            if (FileUtils.restrictFileType(file)) {
                TransferManager tm = TransferManagerBuilder.standard().withS3Client(amazonClient)
                        .withMultipartUploadThreshold((long) (5 * 1024 * 1025)).build();
                String uploadFileName = file.getOriginalFilename().replace(" ", "");
                File nwfile = new File(uploadFileName);
                try {
                    nwfile.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(nwfile);
                    fileOutputStream.write(file.getBytes());
                    fileOutputStream.close();
                } catch (IOException e) {
                    LOGGER.error("File Conversion Failed: " + e.toString());
                }

                try {
                    Upload upload = tm.upload(appContext.getAwsS3BucketName() + "/content", uploadFileName,
                            nwfile);
                    LOGGER.info(Constants.UPLD_START);
                    upload.waitForCompletion();
                    LOGGER.info(Constants.UPLD_COMPLETE);

                    URL url = amazonClient.getUrl(appContext.getAwsS3BucketName(), "content/" + uploadFileName);
                    return HttpUtils.onSuccess(null != url ? url.getFile() : null, Constants.UPLD_COMPLETE);
                } catch (AmazonClientException | InterruptedException e) {
                    LOGGER.error(Constants.UPLD_FAIL + ": " + e.toString());
                    return HttpUtils.onFailure(500, "Upload File Failed: " + Constants.FAIL_S3);
                }
            }
            return HttpUtils.onFailure(400, Constants.FILE_TYPE_NOT_SUPPORTED);
        }
        return HttpUtils.onFailure(400, Constants.PARAMETER_FILE_IS_NOT_PRESENT);
    }

    @Override
    public ResponseDTO deleteContent(String fileName) {
        if (null != fileName && !fileName.isEmpty()) {
            try {
                if (amazonClient.doesObjectExist(appContext.getAwsS3BucketName(), "content/" + fileName)) {
                    amazonClient.deleteObject(appContext.getAwsS3BucketName(), "content/" + fileName);
                    LOGGER.info(Constants.DEL_COMPLETE);
                    return HttpUtils.onSuccess(fileName + " deleted successfully", Constants.DEL_COMPLETE);
                }
            } catch (SdkClientException e) {
                LOGGER.error("Delete Content Failed: " + e.toString());
                return HttpUtils.onFailure(500, "Delete Content failed: " + Constants.FAIL_S3);
            }
            return HttpUtils.onFailure(409, fileName + " does not exists");
        }
        return HttpUtils.onFailure(404, Constants.FILENAME_SHOULD_NOT_BE_NULL_OR_EMPTY);
    }

}
