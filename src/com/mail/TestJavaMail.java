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
        mb.setHost("smtp.qq.com"); // ����SMTP����(163)������126������Ϊ��smtp.126.com
        mb.setUsername("1504761725@qq.com"); // ���÷�����������û���
        mb.setPassword("vqlcjwkxbvibffbe"); // ���÷�������������룬�轫*�Ÿĳ���ȷ������
        mb.setFrom("1504761725@qq.com"); // ���÷����˵�����
        mb.setTo("1504761725@qq.com"); // �����ռ��˵�����
        mb.setSubject("����"); // �����ʼ�������
        mb.setContent("���ʼ��а����������������飡"); // �����ʼ�������

        mb.attachFile("D:\\С˵\\������֮���� Զͫ.txt"); // ���ʼ�����Ӹ���

        SendMail sm = new SendMail();
        System.out.println("���ڷ����ʼ�...");
        // �����ʼ�
        if (sm.sendMail(mb)){
            System.out.println("���ͳɹ�!");
        }else{
            System.out.println("����ʧ��!");
        }
    }
    public static void mail(String mail, String code) {

        
        try {
            MailBean mb = new MailBean();
            mb.setHost("smtp.qq.com"); // ����SMTP����(163)������126������Ϊ��smtp.126.com
            mb.setUsername("1504761725@qq.com"); // ���÷�����������û���
            mb.setPassword("vqlcjwkxbvibffbe"); // ���÷�������������룬�轫*�Ÿĳ���ȷ������
            mb.setFrom("1504761725@qq.com"); // ���÷����˵�����
            mb.setTo(mail); // �����ռ��˵�����
            mb.setSubject(MimeUtility.encodeWord("[Sandbi] Please verify your mail", "UTF-8", "Q")); // �����ʼ�������
            mb.setContent("Hey dear user!\r\n" + 
            		"\r\n" + 
            		"Welcome to Sandbi!\r\n" + 
            		"\r\n" + 
            		"Verification code: " + code); // �����ʼ�������
//            String fileName = "D:\\С˵\\��" + book.getBookName() + "�� " + book.getWriter() + ".txt";
//            mb.attachFile(fileName); // ���ʼ�����Ӹ���
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
