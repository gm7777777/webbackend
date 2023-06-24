package com.gm.webbackend.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gm.webbackend.bo.CaseBO;
import com.gm.webbackend.bo.DocBO;
import com.gm.webbackend.bo.QuestionBO;
import com.gm.webbackend.common.HeadBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/Brand")
public class BrandController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping("headindex")
    public String head(@RequestBody String jsonStr){
        JSONObject obj = (JSONObject) JSON.parse(jsonStr);
        Query q = new Query();
        List codeColl = new ArrayList();
        codeColl.add("001");
        codeColl.add("002");
        codeColl.add("003");
        codeColl.add("004");

        q.addCriteria(Criteria.where("code").in(codeColl));
        List<CaseBO> datas = mongoTemplate.find(q, CaseBO.class);
        HeadBO bo = new HeadBO();
        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        bo.setData(datas);
        return JSON.toJSONString(bo);
    }

    @RequestMapping("topicindex")
    public String topic(@RequestBody String jsonStr){
        JSONObject obj = (JSONObject) JSON.parse(jsonStr);
        Query q = new Query();
        List codeColl = new ArrayList();

        q.skip(0).limit(2);

        q.with(new Sort(Sort.Direction.DESC,"createdate"));

        Calendar calendar = Calendar.getInstance();  //得到日历
        calendar.setTime(new Date());//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        q.addCriteria(Criteria.where("createdate").gte(calendar.getTime()));
        List<QuestionBO> questionBOS = mongoTemplate.find(q, QuestionBO.class);

        Query qcond = new Query();
        int add = 0;
        if(questionBOS.size()==2){
            qcond.skip(0).limit(6);
//            q.with(new Sort(Sort.Direction.DESC,"remarksnum"));
//            qcond.addCriteria(qcond,QuestionBO.class);
            add = 4;
        }else if(questionBOS.size()==1){
            qcond.skip(0).limit(7);
//            q.with(new Sort(Sort.Direction.DESC,"remarksnum"));
            add = 5;
        }else if(null==questionBOS||questionBOS.isEmpty()){
            qcond.skip(0).limit(8);
            add = 6;
        }
        qcond.with(new Sort(Sort.Direction.DESC,"remarksnum"));

        List<QuestionBO> questionBOS1 = mongoTemplate.find(qcond,QuestionBO.class);

        for(QuestionBO temp :questionBOS1){
            boolean addflag = true;
            for(QuestionBO innerTemp: questionBOS){
                if(innerTemp.getId().equals(temp.getId())){
                    addflag = false;
                    break;
                }
            }
            if(addflag==true){
                questionBOS.add(temp);
                add--;
            }
            if(add<1){
                break;
            }
        }
        HeadBO bo = new HeadBO();
        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        bo.setData(questionBOS);
        return JSON.toJSONString(bo);
    }
}
