package com.gm.webbackend.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gm.webbackend.bo.UserBO;
import com.gm.webbackend.common.BaseUtils;
import com.gm.webbackend.common.EmailUtils;
import com.gm.webbackend.common.HeadBO;
//import com.mongodb.client.FindIterable;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.model.Filters;
//import com.sun.org.apache.xalan.internal.xsltc.dom.FilterIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/Auth")
public class LoginController {

//    MongoCollection<Document> collection = MongoLinkClient.getInstance().getMgdatabase().getCollection("user");
//    private String jsonStr;
@Autowired
private MongoTemplate mongoTemplate;

    private boolean isExistByNam(String name){
//        Bson nameCond = Filters.eq("name",name);
////        Bson authCond = Filters.eq("auth",true);
////        Bson fCond = Filters.and(nameCond,authCond);
//        FindIterable<Document> data = collection.find(nameCond);
//        if(data==null||!data.iterator().hasNext()){
//            return false;
//        }
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name));
        return mongoTemplate.exists(query,UserBO.class);
//        return true;
    }

    @RequestMapping("login")
    public String getLogin(@RequestBody String jsonStr){
        JSONObject obj = (JSONObject) JSON.parse(jsonStr);
        JSONObject body = obj.getJSONObject("ctxBody");
        String name = body.getString("name");
        String pwd = body.getString("password");
        HeadBO bo = new HeadBO();
        if(null==name||null==pwd||"".equals(name)||"".equals(pwd)){
            bo.setCode("300");
            bo.setStatus(1);
            bo.setMsg("登录名或者密码不能为空！");
            return JSON.toJSONString(bo);
        }


        if(isExistByNam(name)){
//            Bson nameCond = Filters.eq("name",name);
//            Bson authCond = Filters.eq("auth",true);
//            Bson fCond = Filters.and(nameCond,authCond);
//            FindIterable<Document> data = collection.find(fCond);

            Query query = new Query();
            query.addCriteria(Criteria.where("name").is(name));
            query.addCriteria(Criteria.where("auth").is("true"));
            UserBO user = mongoTemplate.findOne(query,UserBO.class);
            if(BaseUtils.decode(user.getPassword().getBytes()).equals(pwd)){
                bo.setCode("200");
                bo.setStatus(0);
                //先暂时代替token使用
                bo.setMsg("登录成功!");
                Map map = new HashMap();
                map.put("token",UUID.randomUUID());// 目前没用
                map.put("user",name);
                bo.setData(UUID.randomUUID());
                return JSON.toJSONString(bo);
            }
//            for(Document doc :data){
//                if(BaseUtils.decode(doc.getString("pwd").getBytes()).equals(pwd)){
//                    bo.setCode("200");
//                    bo.setStatus(0);
//                    //先暂时代替token使用
//                    bo.setMsg(UUID.randomUUID());
//                }
//            }
            bo.setCode("200");
            bo.setStatus(1);
            bo.setMsg("登录密码错误或用户未授权！");
        }else{
            bo.setCode("200");
            bo.setStatus(1);
            bo.setMsg("用户不存在！");
        }
        return JSON.toJSONString(bo);
    }

    @RequestMapping("register")
    public String getReg(@RequestBody String jsonStr){
//        this.jsonStr = jsonStr;
        JSONObject obj = (JSONObject) JSON.parse(jsonStr);
        JSONObject body = obj.getJSONObject("ctxBody");
        String name = body.getString("name");
        String pwd = body.getString("password");
        String email = body.getString("email");
        String phone = body.getString("phonenum");

        HeadBO bo = new HeadBO();
        if(null==email||"".equals(email)){
            bo.setCode("300");
            bo.setStatus(1);
            bo.setMsg("邮箱不能为空！");
            return JSON.toJSONString(bo);
        }
        if(null==phone||"".equals(phone)){
            bo.setCode("300");
            bo.setStatus(1);
            bo.setMsg("联系电话不能为空！");
            return JSON.toJSONString(bo);
        }

        if(null==name||null==pwd||"".equals(name)||"".equals(pwd)){
            bo.setCode("300");
            bo.setStatus(1);
            bo.setMsg("登录名或者密码不能为空！");
            return JSON.toJSONString(bo);
        }
        if(isExistByNam(name)){
//            Bson nameCond = Filters.eq("name",name);
//            FindIterable<Document> data = collection.find(nameCond);

            Query query = new Query();
            query.addCriteria(Criteria.where("name").is(name));
            query.addCriteria(Criteria.where("auth").is("true"));
            UserBO user = mongoTemplate.findOne(query,UserBO.class);
            if(user!=null){
                bo.setCode("300");
                bo.setStatus(1);
                bo.setMsg("用户已存在！");
                return JSON.toJSONString(bo);
            }
//            query.addCriteria(Criteria.where("auth").is(true));
//            UserBO user = mongoTemplate.findOne(query,UserBO.class);

            Update update = new Update();
            update.set("email",email);
            update.set("phone",phone);
            update.set("password",BaseUtils.encode(pwd));
            update.set("updatetime",new Date());
            StringBuffer s = new StringBuffer();
            Random r = new Random();
            for(int i = 0; i < 6;i++){
                s.append(r.nextInt(9));
            }
            update.set("auth",s.toString());
            Query q = new Query();
            q.addCriteria(Criteria.where("name").is(name));
            mongoTemplate.updateFirst(q,update,UserBO.class);
            bo.setCode("200");
            bo.setStatus(0);
            bo.setMsg("已注册！");

//            Document user = new Document();
//            StringBuffer s = new StringBuffer();
//            if(!data.iterator().hasNext()){
//                for(Document temp : data){
//                    user.put("id", UUID.randomUUID());
//                    user.put("name", name);
//                    user.put("email", email);
//                    user.put("phone",phone);
//                    user.put("password",pwd);
//                    user.put("updatetime", new Date());
////                    int nums[] = new int[6];
//
//                    Random r = new Random();
//                    for(int i = 0; i < 6;i++){
//                        s.append(r.nextInt(9));
//                    }
//                    user.put("auth",s.toString());
//                }
//                collection.insertOne(user);
            try {
                EmailUtils.createEmailWithAuthCode(EmailUtils.createSession(),s.toString(),email);
            } catch (Exception e) {
                bo.setCode("300");
                bo.setStatus(1);
                bo.setMsg("授权邮件发送失败！");
                e.printStackTrace();
            }
//                EmailUtils.sendEmail(email,s.toString());
//            }
//            bo.setCode("200");
//                   bo.setStatus(0);
//                   bo.setMsg("已注册！");

//            for(Document doc :data){
//                if(BaseUtils.decode(doc.getString("pwd").getBytes()).equals(pwd)){
//                    bo.setCode("200");
//                    bo.setStatus(0);
//                    bo.setMsg(UUID.randomUUID());
//                }
//            }
        }else{
            UserBO user = new UserBO();
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(BaseUtils.encode(pwd));
            user.setUpdatetime(new Date());
            StringBuffer s = new StringBuffer();
            Random r = new Random();
            for(int i = 0; i < 6;i++){
                s.append(r.nextInt(9));
            }
            user.setAuth(s.toString());
            mongoTemplate.save(user);
            bo.setCode("200");
            bo.setStatus(0);
            bo.setMsg("已注册,授权后登录");
            bo.setData(user.getId());
            try {
                EmailUtils.createEmailWithAuthCode(EmailUtils.createSession(),s.toString(),email);
            } catch (Exception e) {
                bo.setCode("300");
                bo.setStatus(1);
                bo.setMsg("授权邮件发送失败！");
                e.printStackTrace();
//                bo.setData(user.getId());
            }
//            EmailUtils.sendEmail(email,s.toString());
        }
        return JSON.toJSONString(bo);
    }


    @RequestMapping("auth")
    public String getAuth(@RequestBody String jsonStr){
        JSONObject obj = (JSONObject) JSON.parse(jsonStr);
        JSONObject body = obj.getJSONObject("ctxBody");
        String name = body.getString("name");
        String num1 = body.getString("num1");
        String num2 = body.getString("num2");
        String num3 = body.getString("num3");
        String num4 = body.getString("num4");
        String num5 = body.getString("num5");
        String num6 = body.getString("num6");

        HeadBO bo = new HeadBO();
        if(isExistByNam(name)){
//            Bson nameCond = Filters.eq("name",name);
//            FindIterable<Document> data = collection.find(nameCond);
            Query query = new Query();
            query.addCriteria(Criteria.where("name").is(name));
//            query.addCriteria(Criteria.where("auth").is(true));
            UserBO user = mongoTemplate.findOne(query,UserBO.class);
            StringBuffer s = new StringBuffer();
            s.append(num1);
            s.append(num2);
            s.append(num3);
            s.append(num4);
            s.append(num5);
            s.append(num6);
            if(s.toString().equals(user.getAuth())) {

                Update update = new Update();
                update.set("auth", "true");
                mongoTemplate.updateFirst(query,update,UserBO.class);

                bo.setCode("200");
                bo.setStatus(0);
                bo.setMsg("账户已授权,请重新登录");
//                return JSON.toJSONString(bo);
            }else{
                bo.setCode("300");
                bo.setStatus(1);
                bo.setMsg("授权码不匹配！");
//                return JSON.toJSONString(bo);
            }
//            if(!data.iterator().hasNext()){
//                for(Document user:data){
//                    if(s.toString().equals(user.getString("auth"))){
////                        user.put("auth",true);
//                        Bson idCond = Filters.eq("id",user.getString("id"));
//                        collection.updateOne(idCond,new Document("$set",new Document("auth",true)));
//                        bo.setCode("200");
//                        bo.setStatus(0);
//                        bo.setMsg("账户已授权");
//                    }
//                }
//
//            }
        }else{
            bo.setCode("300");
            bo.setStatus(1);
            bo.setMsg("用户不存在！");
        }
        return JSON.toJSONString(bo);
    }

}
