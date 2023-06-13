package com.gm.webbackend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.UUID;

/**
 * @ClassName UploadController
 * @Author Higher
 * @date 2023/6/2 20:19
 * @Version 1.0
 */
    /**
     * 文件上传和下载
     */
    @RestController
    @RequestMapping("/common")
    @Slf4j
    public class CommonController {
        @Value("${reggie.path}")
        private String basePath;

        @PostMapping("/upload")
        public R<String> upload(MultipartFile file) {
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexof("."));
            String fileName = UUID.randomUUID().toString() + suffix;
            File dir = new File(basePath);
            if(!dir.exists()){
                dir.mkdirs();
            }
            try{
                file.transferTo(new File(basePath + fileName));
            }catch (IOException e){
                e.printStackTrace();
            }
            return R.success(fileName);
        }
    }

