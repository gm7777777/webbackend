package com.gm.webbackend.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gm.webbackend.ao.TreeSectorAO;
import com.gm.webbackend.bo.DocBO;
import com.gm.webbackend.bo.SectorBO;
import com.gm.webbackend.bo.UserBO;
import com.gm.webbackend.common.HeadBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/Reader")
public class DocReaderController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${file.path}")
    String filePath;

    @Value("${server.port}")
    int port;

    @RequestMapping("docs")
    public String getDocs(@RequestBody String jsonStr){
        JSONObject obj = (JSONObject) JSON.parse(jsonStr);
        JSONObject body = obj.getJSONObject("ctxBody");
        String user = body.getString("user");
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(user));
        query.addCriteria(Criteria.where("auth").is("true"));
        String fixedfile = null;
        if(!mongoTemplate.exists(query,UserBO.class)){
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
            String url = "http://"+address.getHostAddress()+":"+port+"/docs/"+"Login.pdf";
            fixedfile = url;//查询不到登录只提供请登录后查看图书文档
        }
//        MongoCollection<Document> sectorColl = MongoLinkClient.getInstance().getMgdatabase().getCollection("sector");
//        FindIterable<Document> datas = sectorColl.find();
        List<SectorBO> datas = mongoTemplate.find(new Query(),SectorBO.class);
        HeadBO bo = new HeadBO();
//        if(null==name||null==pwd||"".equals(name)||"".equals(pwd)){
//            bo.setCode("300");
//            bo.setStatus(1);
//            bo.setMsg("登录名或者密码不能为空！");
//        }
        List<TreeSectorAO> list = new ArrayList();
        List<DocBO> dataList = mongoTemplate.find(new Query(),DocBO.class);
        for(SectorBO data : datas){
            TreeSectorAO sectorAO = new TreeSectorAO();
            sectorAO.setId(data.getCode());
            sectorAO.setLabel(data.getTitle());
            sectorAO.setData(data.getRemarks());
            sectorAO.setChildren(new ArrayList<>());
            list.add(sectorAO);
            Iterator it = dataList.iterator();
            while(it.hasNext()){
                DocBO docBO = (DocBO) it.next();
                if(sectorAO.getId().equals(docBO.getSectorcode())){
                    if(sectorAO.getChildren()!=null&&sectorAO.getChildren().size()>0){
                        for(TreeSectorAO docName:sectorAO.getChildren()){
                            if(docName.getId().equals(docBO.getCode())){
                                TreeSectorAO tempSub = new TreeSectorAO();
                                tempSub.setId(docBO.getCode()+docBO.getSection());
                                tempSub.setLabel(docBO.getName()+"("+docBO.getSection()+")");
                                tempSub.setData(fixedfile==null?docBO.getSrcaddr():fixedfile);
                                docName.getChildren().add(tempSub);
                                it.remove();
                                continue;
                            }
                        }

                    }
                        TreeSectorAO temp = new TreeSectorAO();
                        temp.setId(docBO.getCode());
                        temp.setLabel(docBO.getName());
//                        temp.setData();
                        temp.setChildren(new ArrayList<>());
                        TreeSectorAO tempSub = new TreeSectorAO();
                        tempSub.setId(docBO.getCode()+docBO.getSection());
                        tempSub.setLabel(docBO.getName()+"("+docBO.getSection()+")");
                        tempSub.setData(fixedfile==null?docBO.getSrcaddr():fixedfile);
                        temp.getChildren().add(tempSub);
                        sectorAO.getChildren().add(temp);
                        it.remove();
                }
            }
        }
        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        bo.setData(list);
        return JSON.toJSONString(bo);
    }

    @RequestMapping("add")
    public String addDoc(@RequestBody String jsonStr){
        JSONObject obj = (JSONObject) JSON.parse(jsonStr);
        JSONObject body = obj.getJSONObject("ctxBody");
        String user = body.getString("user");
        String code = body.getString("code");
        String sector = body.getString("sector");
        String name = body.getString("title");
        JSONArray files = body.getJSONArray("files");

        for(int i = 0; i< files.size();i++){
            JSONObject jobj = (JSONObject) files.get(i);
            String id = jobj.getString("id");
            String pathsrc = jobj.getString("url");
            DocBO doc = new DocBO();
            doc.setCode(code);
            doc.setCreatedate(new Date());
            doc.setName(name);
            doc.setSectorcode(sector);
            doc.setSection(i);
            doc.setSrcaddr(pathsrc);
            mongoTemplate.save(doc);
        }
        HeadBO bo = new HeadBO();
        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        return JSON.toJSONString(bo);
    }

}
