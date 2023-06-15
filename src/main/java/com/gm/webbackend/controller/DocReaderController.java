package com.gm.webbackend.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gm.webbackend.ao.TreeSectorAO;
import com.gm.webbackend.bo.DocBO;
import com.gm.webbackend.bo.SectorBO;
import com.gm.webbackend.common.HeadBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/Reader")
public class DocReaderController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping("docs")
    public String getDocs(@RequestBody String jsonStr){
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
}
