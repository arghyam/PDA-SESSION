package com.socion.session.controller;

import com.socion.session.dto.ResponseDTO;
import com.socion.session.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/v2/session", produces = {MediaType.APPLICATION_JSON_VALUE})
public class
UploadFileController {

    @Autowired
    UploadService uploadService;

    @PostMapping(path = "/upload/content", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDTO uploadContent(@RequestParam(name = "file", required = false) MultipartFile file) {
        return uploadService.uploadContentToS3(file);
    }

    @DeleteMapping(path = "/delete/content")
    public ResponseDTO deleteContent(@RequestParam(name = "fileName") String fileName) {
        return uploadService.deleteContent(fileName);
    }
}
