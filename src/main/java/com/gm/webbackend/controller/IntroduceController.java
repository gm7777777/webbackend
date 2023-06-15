package com.gm.webbackend.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gm.webbackend.ao.TreeSectorAO;
import com.gm.webbackend.bo.CaseBO;
import com.gm.webbackend.bo.SectorBO;
import com.gm.webbackend.common.HeadBO;
import com.gm.webbackend.config.MongoLinkClient;
import com.google.gson.JsonArray;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import org.springframework.data.mongodb.core.MongoTemplate;

@RestController
@RequestMapping("/Intro")
public class IntroduceController {

//    MongoCollection<Document> collection = MongoLinkClient.getInstance().getMgdatabase().getCollection("sector");
    @Autowired
    private MongoTemplate mongoTemplate;


    @RequestMapping("sectors")
    public String getSectors(@RequestBody String jsonStr){
//        MongoCollection<Document> sectorColl = MongoLinkClient.getInstance().getMgdatabase().getCollection("sector");
//        FindIterable<Document> datas = sectorColl.find();
        List<SectorBO> dataList = mongoTemplate.find(new Query(),SectorBO.class);
        HeadBO bo = new HeadBO();
//        if(null==name||null==pwd||"".equals(name)||"".equals(pwd)){
//            bo.setCode("300");
//            bo.setStatus(1);
//            bo.setMsg("登录名或者密码不能为空！");
//        }
        List list = new ArrayList();

        for(SectorBO data : dataList){
            JSONObject ret = new JSONObject();
            ret.put("code",data.getCode());
            ret.put("title",data.getTitle());
            list.add(ret);
        }
        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        bo.setData(list);
        return JSON.toJSONString(bo);
    }

    @RequestMapping("caseByPage")
    public String getCases(@RequestBody String jsonStr){
        JSONObject obj = (JSONObject) JSON.parse(jsonStr);
        JSONObject header = obj.getJSONObject("ctxHeader");
        int pageSize = header.getInteger("pagesize");
        int num = header.getInteger("index");

//        MongoCollection<Document> caseColl = MongoLinkClient.getInstance().getMgdatabase().getCollection("case");
//        int page = (num+1)/pageSize;

        Query query = new Query();
        query.skip(num).limit(pageSize);
        List<CaseBO> list = mongoTemplate.find(query,CaseBO.class);

//        caseColl.find().skip(page).limit(pageSize);

//        FindIterable<Document> datas = caseColl.find();

        HeadBO bo = new HeadBO();
//        if(null==name||null==pwd||"".equals(name)||"".equals(pwd)){
//            bo.setCode("300");
//            bo.setStatus(1);
//            bo.setMsg("登录名或者密码不能为空！");
//        }
//        JSONObject ret = new JSONObject();
//        for(Document data : datas){
//            ret.put("code",data.getString("code"));
//            ret.put("title",data.getString("title"));
//        }
        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        bo.setData(list);
        return JSON.toJSONString(bo);
    }



    @RequestMapping("caseById")
    public String getCase(@RequestBody String jsonStr){
        JSONObject obj = (JSONObject) JSON.parse(jsonStr);
        JSONObject body = obj.getJSONObject("ctxBody");
        String id = body.getString("id");

//        MongoCollection<Document> caseColl = MongoLinkClient.getInstance().getMgdatabase().getCollection("case");
//        int page = (num+1)/pageSize;

        Query query = new Query(Criteria.where("_id").is(id));
//        query.skip(num).limit(pageSize);
//        List<CaseBO> list = mongoTemplate.find(query,CaseBO.class);
        CaseBO caseBO = mongoTemplate.findOne(query,CaseBO.class);
//        caseColl.find().skip(page).limit(pageSize);

//        FindIterable<Document> datas = caseColl.find();

        HeadBO bo = new HeadBO();
        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        bo.setData(caseBO);
        return JSON.toJSONString(bo);
    }

    @RequestMapping("treecases")
    public String getTreeCases(@RequestBody String jsonStr){

        Query query = new Query();
        List<SectorBO> list  = mongoTemplate.find(query, SectorBO.class);

        List<CaseBO> dataList = mongoTemplate.find(query,CaseBO.class);
        List<TreeSectorAO> retList = new ArrayList<>();
        for(SectorBO sectorBO:list){
            TreeSectorAO treeAO = new TreeSectorAO();
            treeAO.setId(sectorBO.getCode());
            treeAO.setLabel(sectorBO.getTitle());
            treeAO.setData(sectorBO.getRemarks());
            treeAO.setChildren(new ArrayList<>());

            retList.add(treeAO);
            Iterator it =  dataList.iterator();
            while(it.hasNext()){
                CaseBO caseBO = (CaseBO) it.next();
                if(sectorBO.getCode().equals(caseBO.getSectorcode())){
                    TreeSectorAO temp = new TreeSectorAO();
                    temp.setId(caseBO.getCode());
                    temp.setLabel(caseBO.getName());
                    temp.setData(caseBO.getContent());
                    treeAO.getChildren().add(temp);
                    it.remove();
                }
            }
        }

        HeadBO bo = new HeadBO();
        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        bo.setData(retList);
        return JSON.toJSONString(bo);
    }

}
