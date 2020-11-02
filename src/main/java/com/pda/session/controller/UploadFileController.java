package com.pda.session.controller;

import com.pda.session.service.UploadService;
import com.pda.session.dto.ResponseDTO;
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
