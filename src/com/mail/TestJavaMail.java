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
            mb.setHost(ReadDoc.getMailInfo().get("Host").toString()); // Set the SMTP host (163), if you use 126, set it as: smtp.126.com
            mb.setUsername(ReadDoc.getMailInfo().get("Username").toString()); // Set the username of the sender's mailbox
            mb.setPassword(ReadDoc.getMailInfo().get("Password").toString()); // To set the password of the sender's mailbox, you need to change the * to the correct password
            mb.setFrom(ReadDoc.getMailInfo().get("From").toString()); // Set the sender's email
            mb.setTo(mail); // Set the recipient's mailbox
            mb.setSubject(MimeUtility.encodeWord("[Sandbi] Please verify your mail", "UTF-8", "Q")); // Set the subject of the email
            mb.setContent("Hey dear user,\r\n" + 
            		"\r\n" + 
            		"Welcome to Sandbi.\r\n" + 
            		"\r\n" + 
            		"Verification code: " + code); 
            SendMail sm = new SendMail();
            System.out.println("Sending e-mail...");
            // send email
            if (sm.sendMail(mb)){
                System.out.println("Sent successfully!");
            }else{
                System.out.println("Failed to send!");
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestJavaMail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
