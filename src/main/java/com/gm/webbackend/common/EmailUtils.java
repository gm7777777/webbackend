package com.gm.webbackend.common;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtils {

        public static Session createSession(){
                    // SMTP服务器地址
                    String smtp = "stmp.126.com";

                    // 邮箱账号和密码(授权密码)
                    String userName = "elec_statck@126.com";
                    String password = "ERFGOSKIXHMOGGCN";

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


        public static void createEmailWithAuthCode(Session session ,String code,String emailaddress){
                    try {
                        // 1.创建Session

                        // 2.创建邮件对象(Message抽象类的子类对象)
                        MimeMessage msg = new MimeMessage(session); // 传入session
                        msg.setFrom(new InternetAddress("elec_stack@126.com")); // 发件人
                        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(emailaddress)); // 收件人
                        msg.setRecipient(Message.RecipientType.CC, new InternetAddress("1689104420@qq.com")); // 抄送人
                        msg.setSubject("注册计算机白丁网站的授权码","utf-8"); // 标题

                        // 邮件正文中包含有“html”标签(控制文本的格式)
                        msg.setText("<br>恭喜您注册了计算机白丁的网站<br>以下为激活您账号的的六位授权码","utf-8","html"); // 正文
                        msg.setText("<br>授权码:<p>"+code+"<p>","utf-8","html"); // 正文

                        // 3.发送
                        Transport.send(msg);

                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }


        }
}
