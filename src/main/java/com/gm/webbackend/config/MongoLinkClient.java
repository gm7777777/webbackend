package com.gm.webbackend.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;

public class MongoLinkClient {

    @Value("${spring.mongodb.ip}")
    String serverIp;
    @Value("${spring.mongodb.port}")
    int port;
    @Value("${spring.mongodb.user}")
    String user;
    @Value("${spring.mongodb.password}")
    String pwd;

    private static class InnerClass{
        private static final MongoLinkClient singleton = new MongoLinkClient();
    }

    public static MongoLinkClient  getInstance() { return InnerClass.singleton;}

    MongoDatabase mgdatabase;
    MongoClient mongo;
    public MongoLinkClient(){

    }

    public void initClient(String database){
        if(mgdatabase==null){
            mongo = new MongoClient( serverIp, port );

            // 连接到 MongoDB
//            MongoCredential credential= MongoCredential.createCredential(user, database,pwd.toCharArray());

            // 连接到数据库
            mgdatabase = mongo.getDatabase(database);
        }
    }

    public void changeDatabase(String database){
        this.initClient(database);
        mgdatabase = mongo.getDatabase(database);
    }

    public MongoDatabase getMgdatabase() {
        return mgdatabase;
    }

    public void setMgdatabase(MongoDatabase mgdatabase) {
        this.mgdatabase = mgdatabase;
    }
}
