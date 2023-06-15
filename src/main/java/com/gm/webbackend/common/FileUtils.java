package com.gm.webbackend.common;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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

    public static List transferStringtoList(String files){
        List temp = new ArrayList();
        StringTokenizer result = new StringTokenizer(files,",");
        while(result.hasMoreTokens()){
            temp.add(result.nextToken());
        }
        return temp;
    }
}
