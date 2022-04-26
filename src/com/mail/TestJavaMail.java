package com.mail;


import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.MimeUtility;

/**
 *
 * @author Ray peng Sun
 */

public class TestJavaMail {

    public static void main(String[] args) {

        
        MailBean mb = new MailBean();
        mb.setHost("smtp.qq.com"); // 设置SMTP主机(163)，若用126，则设为：smtp.126.com
        mb.setUsername("1504761725@qq.com"); // 设置发件人邮箱的用户名
        mb.setPassword("vqlcjwkxbvibffbe"); // 设置发件人邮箱的密码，需将*号改成正确的密码
        mb.setFrom("1504761725@qq.com"); // 设置发件人的邮箱
        mb.setTo("1504761725@qq.com"); // 设置收件人的邮箱
        mb.setSubject("测试"); // 设置邮件的主题
        mb.setContent("本邮件中包含三个附件，请检查！"); // 设置邮件的正文

        mb.attachFile("D:\\小说\\《黎明之剑》 远瞳.txt"); // 往邮件中添加附件

        SendMail sm = new SendMail();
        System.out.println("正在发送邮件...");
        // 发送邮件
        if (sm.sendMail(mb)){
            System.out.println("发送成功!");
        }else{
            System.out.println("发送失败!");
        }
    }
    public static void mail(String mail, String code) {

        
        try {
            MailBean mb = new MailBean();
            mb.setHost("smtp.qq.com"); // 设置SMTP主机(163)，若用126，则设为：smtp.126.com
            mb.setUsername("1504761725@qq.com"); // 设置发件人邮箱的用户名
            mb.setPassword("vqlcjwkxbvibffbe"); // 设置发件人邮箱的密码，需将*号改成正确的密码
            mb.setFrom("1504761725@qq.com"); // 设置发件人的邮箱
            mb.setTo(mail); // 设置收件人的邮箱
            mb.setSubject(MimeUtility.encodeWord("[Sandbi] Please verify your mail", "UTF-8", "Q")); // 设置邮件的主题
            mb.setContent("Hey dear user!\r\n" + 
            		"\r\n" + 
            		"Welcome to Sandbi!\r\n" + 
            		"\r\n" + 
            		"Verification code: " + code); // 设置邮件的正文
//            String fileName = "D:\\小说\\《" + book.getBookName() + "》 " + book.getWriter() + ".txt";
//            mb.attachFile(fileName); // 往邮件中添加附件
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
