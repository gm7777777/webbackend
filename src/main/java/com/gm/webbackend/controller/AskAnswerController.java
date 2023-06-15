package com.gm.webbackend.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gm.webbackend.bo.QuestionBO;
import com.gm.webbackend.bo.RemarkBO;
import com.gm.webbackend.common.BaseUtils;
import com.gm.webbackend.common.FileUtils;
import com.gm.webbackend.common.HeadBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/AskAnswer")
public class AskAnswerController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping("getQues")
    public String getQues(@RequestBody String jsonStr){
        JSONObject obj = (JSONObject) JSON.parse(jsonStr);
        JSONObject header = obj.getJSONObject("ctxHeader");
        int curPage = header.getInteger("curPage");
        int pageSize = header.getInteger("pageSize");
        JSONObject body = obj.getJSONObject("ctxBody");
        String sector = body.getString("sector");
        String author = body.getString("user");
        Boolean edit = body.getBoolean("isedit");
        Query q = new Query();
        q.addCriteria(Criteria.where("sectorcode").is(sector));
        q.skip((curPage-1)*pageSize).limit(pageSize);

        q.with(new Sort(Sort.Direction.DESC,"createdate"));

        if(edit){
            q.addCriteria(Criteria.where("author").is(author));
        }
        List<QuestionBO> list = mongoTemplate.find(q, QuestionBO.class);
//        String id = obj.getString("id");
        HeadBO bo = new HeadBO();
        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        bo.setData(list);
        return JSON.toJSONString(bo);
    }


    @RequestMapping("addTopic")
    public String getAddTopic(HttpServletRequest request, @RequestBody String jsonStr){
        JSONObject obj = (JSONObject) JSON.parse(jsonStr);
        JSONObject body = obj.getJSONObject("ctxBody");
        String sector = body.getString("sector");
        String title = body.getString("title");
        String date = body.getString("createDate");
        String author = body.getString("user");
        String content= body.getString("content");
//        String sourceIP= body.getString("sourceIp");
        String logosrc = body.getString("pathsrc");
        String files = body.getString("files");
        QuestionBO quesBO = new QuestionBO();
        quesBO.setSectorcode(sector);
        quesBO.setTitle(title);
        quesBO.setCreatedate(BaseUtils.transferString2Date(date));
        quesBO.setAuthor(author);
        quesBO.setContent(content);
        quesBO.setSourceip(request.getRemoteAddr());
        quesBO.setLogosrc(logosrc);
        quesBO.setFilesrc(FileUtils.transferStringtoList(files));
        mongoTemplate.save(quesBO);
        HeadBO bo = new HeadBO();
        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        return JSON.toJSONString(bo);
    }


    @RequestMapping("updTopic")
    public String getUpdTopic(HttpServletRequest request, @RequestBody String jsonStr){
        JSONObject obj = (JSONObject) JSON.parse(jsonStr);
        JSONObject body = obj.getJSONObject("ctxBody");
        String id = body.getString("id");
//        String sector = body.getString("sector");
        String title = body.getString("title");
        String date = body.getString("createDate");
        String author = body.getString("user");
        String content= body.getString("content");
//        String sourceIP= body.getString("sourceIp");
//        String logosrc = body.getString("pathsrc");
        String files = body.getString("files");

        Query q = new Query();
        q.addCriteria(Criteria.where("_id").is(id));
        QuestionBO temp = mongoTemplate.findOne(q,QuestionBO.class);

        Update upd = new Update();
        if(title!=null){
            upd.set("title",title);
        }
        if(date!=null){
            upd.set("createdate",BaseUtils.transferString2Date(date));
        }
        if(author!=null){
            upd.set("author",author);
        }
        if(content!=null){
            upd.set("content",content);
        }
        if(files!=null){
            upd.set("filesrc",FileUtils.transferStringtoList(files));
        }

        mongoTemplate.updateFirst(q,upd,QuestionBO.class);

        HeadBO bo = new HeadBO();
        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        return JSON.toJSONString(bo);
    }

    @RequestMapping("addRemark")
    public String getAddRemark(HttpServletRequest request, @RequestBody String jsonStr){
        JSONObject obj = (JSONObject) JSON.parse(jsonStr);
        JSONObject body = obj.getJSONObject("ctxBody");
        String questionid = body.getString("questionId");
        String title = body.getString("title");
        String date = body.getString("createDate");
        String author = body.getString("user");
        String content= body.getString("content");
//        String sourceIP= body.getString("sourceIp");
        RemarkBO remarkBO = new RemarkBO();
        remarkBO.setQuestionid(questionid);
        remarkBO.setTitle(title);
        remarkBO.setCreatedate(BaseUtils.transferString2Date(date));
        remarkBO.setAuthor(author);
        remarkBO.setSourceip(request.getRemoteAddr());
        remarkBO.setContent(content);
        mongoTemplate.save(remarkBO);
        HeadBO bo = new HeadBO();
        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        return JSON.toJSONString(bo);
    }


    @RequestMapping("getRemarks")
    public String getRemarks(@RequestBody String jsonStr){
        JSONObject obj = (JSONObject) JSON.parse(jsonStr);
        JSONObject header = obj.getJSONObject("ctxHeader");
        int curPage = header.getInteger("curPage");
        int pageSize = header.getInteger("pageSize");
        JSONObject body = obj.getJSONObject("ctxBody");
        String questionid = body.getString("questionId");
//        String author = body.getString("user");
//        Boolean edit = body.getBoolean("isedit");
        Query q = new Query();
        q.addCriteria(Criteria.where("questionid").is(questionid));
        q.skip((curPage-1)*pageSize).limit(pageSize);

        q.with(new Sort(Sort.Direction.DESC,"createdate"));
        
        List<QuestionBO> list = mongoTemplate.find(q, QuestionBO.class);
//        String id = obj.getString("id");
        HeadBO bo = new HeadBO();
        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        bo.setData(list);
        return JSON.toJSONString(bo);
    }

}
