package com.mail;


import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.MimeUtility;

import com.ReadDoc;


public class TestJavaMail {
    public static void mail(String mail, String code) {

        
        try {
            MailBean mb = new MailBean();
            mb.setHost(ReadDoc.getMailInfo().get("Host").toString()); // 设置SMTP主机(163)，若用126，则设为：smtp.126.com
            mb.setUsername(ReadDoc.getMailInfo().get("Username").toString()); // 设置发件人邮箱的用户名
            mb.setPassword(ReadDoc.getMailInfo().get("Password").toString()); // 设置发件人邮箱的密码，需将*号改成正确的密码
            mb.setFrom(ReadDoc.getMailInfo().get("From").toString()); // 设置发件人的邮箱
            mb.setTo(mail); // 设置收件人的邮箱
            mb.setSubject(MimeUtility.encodeWord("[Sandbi] Please verify your mail", "UTF-8", "Q")); // 设置邮件的主题
            mb.setContent("Hey dear user!\r\n" + 
            		"\r\n" + 
            		"Welcome to Sandbi!\r\n" + 
            		"\r\n" + 
            		"Verification code: " + code); 
            SendMail sm = new SendMail();
            System.out.println("正在发送邮件...");
            // 发送邮件
            if (sm.sendMail(mb)){
                System.out.println("发送成功!");
            }else{
                System.out.println("发送失败!");
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestJavaMail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
