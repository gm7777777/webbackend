package com.gm.webbackend.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gm.webbackend.bo.QuestionBO;
import com.gm.webbackend.bo.RemarkBO;
import com.gm.webbackend.bo.UserBO;
import com.gm.webbackend.common.BaseUtils;
import com.gm.webbackend.common.EmailUtils;
import com.gm.webbackend.common.FileUtils;
import com.gm.webbackend.common.HeadBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
@CrossOrigin
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
        String title = body.getString("title");
        boolean edit = body.getBoolean("isedit");
        Query q = new Query();
        q.addCriteria(Criteria.where("sectorcode").is(sector));
        q.skip((curPage-1)*pageSize).limit(pageSize);

        q.with(new Sort(Sort.Direction.DESC,"createdate"));

        if(edit){
            q.addCriteria(Criteria.where("author").is(author));
        }
        if(title!=null){
            q.addCriteria(Criteria.where("title").regex(Pattern.compile("^.*" + title + ".*$", Pattern.CASE_INSENSITIVE)));
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


    @RequestMapping("getQuesById")
    public String getQuesById(@RequestBody String jsonStr){
        JSONObject obj = (JSONObject) JSON.parse(jsonStr);
        JSONObject body = obj.getJSONObject("ctxBody");
        String id = body.getString("topicid");
        Query q = new Query();
        q.addCriteria(Criteria.where("_id").is(id));
        QuestionBO data = mongoTemplate.findOne(q, QuestionBO.class);
//        String id = obj.getString("id");
        data.setCreateDate(BaseUtils.transferDate2String(data.getCreatedate()));
       HeadBO bo = new HeadBO();
        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        bo.setData(data);
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
        JSONArray files = body.getJSONArray("files");
        if(author==null){
            author="游客";
        }
        QuestionBO quesBO = new QuestionBO();
        quesBO.setSectorcode(sector);
        quesBO.setTitle(title);
        quesBO.setCreatedate(BaseUtils.transferString2Date(date));
        quesBO.setAuthor(author);
        quesBO.setContent(content);
        quesBO.setSourceip(request.getRemoteAddr());
        quesBO.setLogosrc(logosrc);
        quesBO.setFilesrc(files);
        mongoTemplate.save(quesBO);
        Query q = new Query();
        UserBO user = mongoTemplate.findOne(q, UserBO.class);
        try {
            EmailUtils.createEmailWithWarning(EmailUtils.createSession(),author,title,user.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        int num = body.getInteger("remarknum");

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
//            upd.set("filesrc",FileUtils.transferStringtoList(files));
            upd.set("filesrc",files);
        }
        if(num>0){
//            upd.set("filesrc",FileUtils.transferStringtoList(files));
            upd.set("remarksnum",temp.getRemarksnum()+num);
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
        String questionid = body.getString("topicId");
        String title = body.getString("title");
        String date = body.getString("createDate");
        String author = body.getString("user");
        String content= body.getString("content");
//        String sourceIP= body.getString("sourceIp");
        RemarkBO remarkBO = new RemarkBO();
        remarkBO.setQuestionid(questionid);
        remarkBO.setTitle(title);
        remarkBO.setCreatedate(BaseUtils.transferString2Date(date));
        if(null==author||"".equals(author)||"null".equals(author)){
            author = "游客";
        }
        remarkBO.setAuthor(author);
        remarkBO.setSourceip(request.getRemoteAddr());
        remarkBO.setContent(content);
        mongoTemplate.save(remarkBO);
        Query q = new Query();
        q.addCriteria(Criteria.where("_id").is(questionid));
        QuestionBO temp = mongoTemplate.findOne(q,QuestionBO.class);
        Update upd = new Update();
        upd.set("remarksnum",temp.getRemarksnum()+1);
        mongoTemplate.updateFirst(q,upd,QuestionBO.class);
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
        String questionid = body.getString("topicid");
//        String author = body.getString("user");
//        Boolean edit = body.getBoolean("isedit");
        Query q = new Query();
        q.addCriteria(Criteria.where("questionid").is(questionid));
        q.skip((curPage-1)*pageSize).limit(pageSize);

        q.with(new Sort(Sort.Direction.DESC,"createdate"));

        List<RemarkBO> list = mongoTemplate.find(q, RemarkBO.class);
//        String id = obj.getString("id");
        HeadBO bo = new HeadBO();
        bo.setCode("200");
        bo.setStatus(0);
        bo.setMsg("成功！");
        bo.setData(list);
        return JSON.toJSONString(bo);
    }



}
