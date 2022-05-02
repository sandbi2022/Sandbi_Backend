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
            mb.setHost(ReadDoc.getMailInfo().get("Host").toString()); // ����SMTP����(163)������126������Ϊ��smtp.126.com
            mb.setUsername(ReadDoc.getMailInfo().get("Username").toString()); // ���÷�����������û���
            mb.setPassword(ReadDoc.getMailInfo().get("Password").toString()); // ���÷�������������룬�轫*�Ÿĳ���ȷ������
            mb.setFrom(ReadDoc.getMailInfo().get("From").toString()); // ���÷����˵�����
            mb.setTo(mail); // �����ռ��˵�����
            mb.setSubject(MimeUtility.encodeWord("[Sandbi] Please verify your mail", "UTF-8", "Q")); // �����ʼ�������
            mb.setContent("Hey dear user!\r\n" + 
            		"\r\n" + 
            		"Welcome to Sandbi!\r\n" + 
            		"\r\n" + 
            		"Verification code: " + code); 
            SendMail sm = new SendMail();
            System.out.println("���ڷ����ʼ�...");
            // �����ʼ�
            if (sm.sendMail(mb)){
                System.out.println("���ͳɹ�!");
            }else{
                System.out.println("����ʧ��!");
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestJavaMail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
