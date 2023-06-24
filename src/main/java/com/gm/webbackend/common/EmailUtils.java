package com.gm.webbackend.common;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtils {

        public static Session createSession(){
                    // SMTP服务器地址
                    String smtp = "smtp.126.com";

                    // 邮箱账号和密码(授权密码)
                    String userName = "elec_stack@126.com";
                    String password = "VTDZYPSLCYODUJRA";
//                    String password = "1234567Gm";

                    // SMTP服务器的连接信息
                    Properties props = new Properties();
                    props.put("mail.smtp.host", smtp); // SMTP主机号
                    props.put("mail.smtp.port", "25"); // 主机端口号
                    props.put("mail.smtp.auth", "true"); // 是否需要认证
                    props.put("mail.smtp.starttls.enable", "true"); // 启用TLS加密

                    // 创建Session
                    // 参数1：SMTP服务器的连接信息
                    // 参数2：用户认证对象(Authenticator接口的匿名实现类)
                    Session session = Session.getInstance(props, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(userName, password);
                        }
                    });

                    // 开启调试模式
                    session.setDebug(true);

//                    System.out.println(session);
                    return session;


        }


        public static void createEmailWithAuthCode(Session session ,String code,String emailaddress) throws Exception {

                        // 1.创建Session

                        // 2.创建邮件对象(Message抽象类的子类对象)
                        MimeMessage msg = new MimeMessage(session); // 传入session
                        msg.setFrom(new InternetAddress("elec_stack@126.com")); // 发件人
                        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(emailaddress)); // 收件人
                        msg.setRecipient(Message.RecipientType.CC, new InternetAddress("1689104420@qq.com")); // 抄送人
                        msg.setSubject("注册计算机白丁网站的授权码","utf-8"); // 标题

                        // 邮件正文中包含有“html”标签(控制文本的格式)
//                        msg.setText("<br>恭喜您注册了计算机白丁的网站<br>以下为激活您账号的的六位授权码","utf-8","html"); // 正文
                        msg.setText("<br>恭喜您注册了计算机白丁的网站<br>以下为激活您账号的的六位授权码"+"<br>授权码:<p>"+code+"<p>","utf-8","html"); // 正文

                        // 3.发送
                        Transport.send(msg);

        }

    public static void createEmailWithWarning(Session session ,String name,String title,String emailaddress) throws Exception {

        // 1.创建Session

        // 2.创建邮件对象(Message抽象类的子类对象)
        MimeMessage msg = new MimeMessage(session); // 传入session
        msg.setFrom(new InternetAddress("elec_stack@126.com")); // 发件人
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress("1689104420@qq.com")); // 收件人
//        msg.setRecipient(Message.RecipientType.CC, new InternetAddress("1689104420@qq.com")); // 抄送人
        msg.setSubject("通知来自计算机白丁网站","utf-8"); // 标题

        // 邮件正文中包含有“html”标签(控制文本的格式)
//                        msg.setText("<br>恭喜您注册了计算机白丁的网站<br>以下为激活您账号的的六位授权码","utf-8","html"); // 正文
        msg.setText("<br>用户:"+name+"<br>邮箱："+emailaddress+"<br>咨询了问题:<p>"+title+"<p>","utf-8","html"); // 正文

        // 3.发送
        Transport.send(msg);

    }



    //邮件发送方的邮箱


    public static void sendEmail(String toAddr,String code){
        String send="elec_stack@126.com";
        //发送方的授权码(刚刚上面获取到的授权码)
        String password="VTDZYPSLCYODUJRA";
        //邮件接收方的邮箱
//         String to="xxxxx@outlook.com";
         Session session=null;
         MimeMessage msg=null;
        Transport transport=null;
        Properties p=System.getProperties();
        //设置邮件服务器
        p.setProperty("mail.host", "smtp.126.com");
        // 发送服务器需要身份验证
        p.setProperty("mail.smtp.auth", "true");
        //获取默认的Session对象
        session=Session.getInstance(p);
        //创建默认的MimeMessage
        msg=new MimeMessage(session);
        //设置发送方头部头字段
        try {
            //设置发送方的邮件地址
            msg.setFrom(new InternetAddress(send));
            //设置目标方(即收件人),若想发送给多人，第二个入参换成数组即可
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddr));
            transport = session.getTransport();
            // 连接邮件服务器
            transport.connect(send, password);
            sendContext(msg,transport,"注册计算机白丁网站的授权码",code,toAddr);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    /**
     * 发送文本邮件
     * @param subject 邮件主题
     * @throws AddressException
     * @throws MessagingException
     */
    public static void sendContext(MimeMessage msg ,Transport transport,String subject,String code,String toAddr) throws AddressException, MessagingException {

        //设置邮件主题
        msg.setSubject(subject);

        //设置邮件内容
        msg.setText("<br>恭喜您注册了计算机白丁的网站<br>以下为激活您账号的的六位授权码","utf-8","html"); // 正文
        msg.setText("<br>授权码:<p>"+code+"<p>","utf-8","html"); // 正文


        // 发送邮件
        transport.sendMessage(msg, new Address[]{new InternetAddress(toAddr)});
        // 关闭连接
        transport.close();
    }
}
