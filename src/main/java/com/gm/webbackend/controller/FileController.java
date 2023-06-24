package com.gm.webbackend.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gm.webbackend.bo.FileBO;
import com.gm.webbackend.bo.QuestionBO;
import com.gm.webbackend.common.FileUtils;
import com.gm.webbackend.common.HeadBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.net.InetAddress;

@CrossOrigin
@RestController
@RequestMapping("/File")
public class FileController {

    @Value("${file.path}")
    String filePath;

    @Value("${server.port}")
    int port;

    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String upload(HttpServletRequest request ,@RequestPart("file") MultipartFile[] files ){
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

    @RequestMapping(value = "/uploadImg", method = RequestMethod.POST)
    @ResponseBody
    public String uploadImg(HttpServletRequest request ,HttpServletResponse response,@RequestPart("file") MultipartFile[] files ){
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
        InetAddress address = null;
        HeadBO bo = new HeadBO();
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            bo.setCode("300");
            bo.setStatus(1);
            bo.setMsg("获取ip失败！");
            return JSON.toJSONString(bo);
        }
        String url = "http://"+address.getHostAddress()+":"+port+"/docs/";

        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        JSONObject data = new JSONObject();
        data.put("url",url);
        data.put("id",ids.get(0));
        bo.setData(data);
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


    @RequestMapping(value = "/getFilesByTopic", method = RequestMethod.POST)
    @ResponseBody
    public String getFilesByNam(@RequestBody String jsonStr ){
        JSONObject obj = (JSONObject) JSON.parse(jsonStr);
        JSONObject body = obj.getJSONObject("ctxBody");
        String topid = body.getString("topid");

//        FileBO file = new FileBO();
        Query q = new Query();
        q.addCriteria(Criteria.where("_id").is(topid));
        QuestionBO data = mongoTemplate.findOne(q,QuestionBO.class);

        HeadBO bo = new HeadBO();
        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        bo.setData(data.getFilesrc());
        return JSON.toJSONString(bo);
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @ResponseBody
    public String remove(@RequestBody String jsonStr){
        JSONObject obj = (JSONObject) JSON.parse(jsonStr);
        JSONObject body = obj.getJSONObject("ctxBody");
        String id = body.getString("id");
        String topid = body.getString("topid");

        Query q = new Query();
        q.addCriteria(Criteria.where("_id").is(id));
        mongoTemplate.remove(q,FileBO.class);

        if(topid!=null&&!"".equals(topid)){
            Query qtopic = new Query();
            qtopic.addCriteria(Criteria.where("_id").is(topid));
            QuestionBO questionBO = mongoTemplate.findOne(qtopic, QuestionBO.class);
            Iterator it = questionBO.getFilesrc().iterator();
            while(it.hasNext()){
                JSONObject object = (JSONObject) JSON.toJSON(it.next());
                if(object.getString("id").equals(id)){
                    it.remove();
                }
            }
            Update update = new Update();
            update.set("filesrc",questionBO.getFilesrc());
            mongoTemplate.updateFirst(qtopic,update,FileBO.class);
        }
        HeadBO bo = new HeadBO();
        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        return JSON.toJSONString(bo);
    }

}
