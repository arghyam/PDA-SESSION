package com.pda.session.utils;

import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

    private FileUtils() {
    }

    public static boolean restrictFileType(MultipartFile multipartFile) {
        if (null != multipartFile) {
            String fileExtentions = ".mp4,.3gp,.mpeg,.flv,.jpg,.jpeg,.png,.svg,.audio,.m4a,.mp4,.mp3,.aac,.doc,.docx,.pdf";
            String fileName = multipartFile.getOriginalFilename();
            int lastIndex = fileName.lastIndexOf('.');
            String substring = fileName.substring(lastIndex, fileName.length());
            return fileExtentions.contains(substring);
        }
        return false;
    }


}
