package com.gm.webbackend.common;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class FileUtils {

    public static String fileSave(MultipartFile file, String path) {

        BufferedOutputStream bufferedOutputStream = null;
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(
                        new File(path + file.getOriginalFilename())));
                bufferedOutputStream.write(bytes);
                bufferedOutputStream.close();
                return file.getOriginalFilename() + "success upload";
            } catch (Exception e) {
                return file.getOriginalFilename() + "failed to upload ---> " + e;
            }
        } else {
            return file.getOriginalFilename() + "You failed to upload file was empty.";
        }
    }
}
