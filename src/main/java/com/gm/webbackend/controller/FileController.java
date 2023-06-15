package com.gm.webbackend.controller;

import com.alibaba.fastjson.JSON;
import com.gm.webbackend.bo.FileBO;
import com.gm.webbackend.common.FileUtils;
import com.gm.webbackend.common.HeadBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/File")
public class FileController {

    @Value("file.path")
    String filePath;

    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String upload(HttpServletRequest request ,@RequestPart("files") MultipartFile[] files ){
        List ids = new ArrayList();
        for(int i=0;i<files.length;i++){
            FileUtils.fileSave(files[i],filePath);
            FileBO file = new FileBO();
            file.setName(files[i].getOriginalFilename());
            file.setPath(filePath);
            file.setCreatedate(new Date());
            mongoTemplate.save(file);
            ids.add(file.getId());
        }
        HeadBO bo = new HeadBO();
        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        bo.setData(ids);
        return JSON.toJSONString(bo);
    }

        @RequestMapping("/download/{id}")
        public void download(@PathVariable("id")String id, HttpServletResponse response) throws IOException {

            Query q = new Query();
            q.addCriteria(Criteria.where("_id").is(id));
            FileBO fileBO = mongoTemplate.findOne(q,FileBO.class );
            // 路径可以指定当前项目相对路径
            File file = new File(filePath + fileBO.getName());
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                ServletOutputStream outputStream = response.getOutputStream();
//                if(!isOnLine){
                    response.setContentType("application/octet-stream");
                    // 如果文件名为中文需要设置编码
                    response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileBO.getName(), "utf8"));
                    // 返回前端文件名需要添加
                    response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
//                }
                byte[] bytes = new byte[1024];
                int len;
                while ((len = fileInputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, len);
                }
            }
    }


}
